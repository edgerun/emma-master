package at.ac.tuwien.dsg.emma.monitoring;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessageReader;
import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessageWriter;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageRequest;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageResponse;

public class MonitoringMessageIOTest {

    private ByteBuffer buf;

    private MonitoringMessageReader reader;
    private MonitoringMessageWriter writer;

    @Before
    public void setUp() throws Exception {
        this.reader = new MonitoringMessageReader();
        this.writer = new MonitoringMessageWriter();

        this.buf = ByteBuffer.allocate(128);
    }

    @Test
    public void usageRequest() throws Exception {
        UsageRequest write = new UsageRequest("my-host-id");
        UsageRequest read = writeAndRead(write);

        assertThat(read.getHostId(), is("my-host-id"));
    }

    @Test
    public void usageResponse() throws Exception {
        UsageResponse write = new UsageResponse();
        write.setHostId("my-host-id");
        write.setProcessors(2);
        write.setLoad(1.24f);
        write.setThroughputOut(42);

        UsageResponse read = writeAndRead(write);

        assertThat(read.getHostId(), is("my-host-id"));
        assertThat(read.getProcessors(), is(2));
        assertThat(read.getLoad(), is(1.24f));
        assertThat(read.getThroughputIn(), is(0));
        assertThat(read.getThroughputOut(), is(42));
    }


    // TODO other tests

    @SuppressWarnings("unchecked")
    private <T extends MonitoringMessage> T writeAndRead(MonitoringMessage write) {
        writer.write(buf, write);
        buf.flip();
        System.out.println(buf.remaining());
        try {
            return (T) reader.read(buf);
        } finally {
            buf.clear();
        }
    }
}
