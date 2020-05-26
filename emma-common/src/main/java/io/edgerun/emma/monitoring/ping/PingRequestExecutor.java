package io.edgerun.emma.monitoring.ping;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.edgerun.emma.monitoring.MonitoringLoop;
import io.edgerun.emma.monitoring.msg.PingMessage;
import io.edgerun.emma.monitoring.msg.PingReqMessage;
import io.edgerun.emma.monitoring.msg.PongMessage;
import io.edgerun.emma.util.Concurrent;

/**
 * De/Multiplexer for PingPong messages.
 *
 * TODO create proper packet TTL mechanism
 *
 * TODO clean up this mess
 */
public class PingRequestExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(PingRequestExecutor.class);

    private int interval = 250;
    private int pingCount = 10;

    private ScheduledExecutorService scheduler;
    private MonitoringLoop monitoringLoop;

    private AtomicInteger runIds;
    private Map<Integer, PingRun> runs;

    public PingRequestExecutor(MonitoringLoop monitoringLoop) {
        this.runIds = new AtomicInteger();
        this.runs = new HashMap<>();
        this.monitoringLoop = monitoringLoop;
    }

    public synchronized void start() {
        if (scheduler != null) {
            return;
        }
        scheduler = Executors.newScheduledThreadPool(2);
    }

    public synchronized void stop() {
        if (scheduler == null) {
            return;
        }
        LOG.debug("Terminating PingRequestExecutor scheduler");
        Concurrent.shutdownAndAwaitTermination(scheduler);
    }

    public void execute(PingReqMessage message, Consumer<PingPongResult> completionCallback) {
        int runId = runIds.incrementAndGet();
        PingRun run = new PingRun(runId, message, completionCallback);
        runs.put(runId, run);

        run.schedule(scheduler);
        scheduler.schedule(new PingRunFinalizer(run), interval * (pingCount + 4), TimeUnit.MILLISECONDS);
    }

    public void record(PongMessage message, long received) {
        PingRun pingRun = getPingRun(message.getPingId());

        if (pingRun != null) {
            pingRun.add(message, received);
        }
    }

    private PingRun getPingRun(int pingId) {
        if (pingId < 100) {
            throw new IllegalArgumentException();
        }
        int runId = pingId / 100;
        return runs.get(runId);
    }

    public static class PingRunFinalizer implements Runnable {

        private PingRun run;

        public PingRunFinalizer(PingRun run) {
            this.run = run;
        }

        @Override
        public void run() {
            run.onFinished();
        }
    }

    public class PingRun implements Runnable {

        private PingReqMessage request;
        private int runId;
        private int pingId = 0;

        private Consumer<PingPongResult> callback;
        private Map<Integer, PingPong> pingPongs;

        private InetSocketAddress target;

        private boolean finished;

        private ScheduledFuture<?> scheduledFuture;
        private int pongCount;

        public void schedule(ScheduledExecutorService executor) {
            this.scheduledFuture = executor.scheduleWithFixedDelay(this, 0, interval, TimeUnit.MILLISECONDS);
        }

        public PingRun(int runId, PingReqMessage request, Consumer<PingPongResult> callback) {
            this.runId = runId;
            this.request = request;
            this.callback = callback;
            this.pingPongs = new HashMap<>(pingCount);
            this.target = new InetSocketAddress(request.getTargetHost(), request.getTargetPort());
        }

        public PingReqMessage getRequest() {
            return request;
        }

        public void add(PongMessage message, long received) {
            PingPong pingPong = pingPongs.get(message.getPingId());
            if (pingPong != null) {
                pingPong.setReceived(Instant.ofEpochMilli(received));
                pongCount++;
            }

            if (pongCount == pingCount) {
                onFinished();
            }
        }

        public synchronized void onFinished() {
            if (!finished) {
                finished = true;
                callback.accept(new PingPongResult(request, pingPongs.values()));
                cancel();
                runs.remove(runId);
            }
        }

        public boolean isFinished() {
            return finished;
        }

        @Override
        public void run() {
            pingId++;
            int messageId = (runId * 100) + pingId;

            PingPong pingPong = new PingPong(messageId);
            pingPongs.put(messageId, pingPong);

            PingMessage pingMessage = new PingMessage(messageId);
            pingMessage.setDestination(target);

            pingPong.setSent(Instant.now());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Sending ping message {} {}", pingId, pingMessage);
            }
            monitoringLoop.send(pingMessage);

            if (pingId >= pingCount) {
                cancel();
            }
        }

        public synchronized void cancel() {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        }
    }

}
