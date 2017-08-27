package at.ac.tuwien.dsg.emma.manager;

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
import at.ac.tuwien.dsg.emma.manager.network.sel.BrokerSelectionStrategy;
import at.ac.tuwien.dsg.emma.manager.network.sel.LowLoadAndLatencyStrategy;
import at.ac.tuwien.dsg.emma.manager.service.RedisBridgingTable;
import at.ac.tuwien.dsg.emma.manager.service.sub.SubscriptionTable;
import redis.clients.jedis.JedisPool;

/**
 * ManagerAppConfig.
 */
@Configuration
public class ManagerAppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerAppConfig.class);

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
        return new LowLoadAndLatencyStrategy();
        // return new LowestLatencyStrategy();
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedis(
            @Value("${emma.manager.redis.host}") String host,
            @Value("${emma.manager.redis.port}") Integer port) {
        LOG.info("Initializing JedisPool on {}:{}", host, port);
        return new JedisPool(host, port);
    }

    @Bean
    public BridgingTable bridgingTable(JedisPool jedis) {
        return new RedisBridgingTable(jedis);
    }
}
