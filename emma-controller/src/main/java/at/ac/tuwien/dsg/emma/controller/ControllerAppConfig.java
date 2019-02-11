package at.ac.tuwien.dsg.emma.controller;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import at.ac.tuwien.dsg.emma.bridge.BridgingTable;
import at.ac.tuwien.dsg.emma.controller.control.ControlServer;
import at.ac.tuwien.dsg.emma.controller.network.balancing.BalancingStrategy;
import at.ac.tuwien.dsg.emma.controller.network.balancing.ConnectionBalancingStrategy;
import at.ac.tuwien.dsg.emma.controller.network.sel.BrokerSelectionStrategy;
import at.ac.tuwien.dsg.emma.controller.network.sel.LowestLatencyStrategy;
import at.ac.tuwien.dsg.emma.controller.service.RedisBridgingTable;
import at.ac.tuwien.dsg.emma.controller.service.sub.SubscriptionTable;
import redis.clients.jedis.JedisPool;

@Configuration
public class ControllerAppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerAppConfig.class);

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("emma.Async-");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(2);
        executor.setThreadNamePrefix("emma.Scheduled-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SubscriptionTable subscriptionTable() {
        return new SubscriptionTable();
    }

    @Bean
    public BrokerSelectionStrategy brokerSelectionStrategy() {
        //        return new LowLoadAndLatencyStrategy();
        return new LowestLatencyStrategy();
    }

    @Bean
    public BalancingStrategy balancingStrategy() {
        return new ConnectionBalancingStrategy();
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedis(
            @Value("${emma.controller.redis.host}") String host,
            @Value("${emma.controller.redis.port}") Integer port) {
        LOG.info("Initializing JedisPool on {}:{}", host, port);
        return new JedisPool(host, port);
    }

    @Bean
    public BridgingTable bridgingTable(JedisPool jedis) {
        return new RedisBridgingTable(jedis);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ControlServer controlServer(@Value("${emma.controller.control.port}") Integer port) {
        return new ControlServer(port);
    }
}
