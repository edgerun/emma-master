package at.ac.tuwien.dsg.emma.manager;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import at.ac.tuwien.dsg.emma.bridge.BridgingTable;
import at.ac.tuwien.dsg.emma.manager.network.sel.BrokerSelectionStrategy;
import at.ac.tuwien.dsg.emma.manager.network.sel.LowestLatencyStrategy;
import at.ac.tuwien.dsg.emma.manager.service.RedisBridgingTable;
import at.ac.tuwien.dsg.emma.manager.service.sub.SubscriptionTable;
import redis.clients.jedis.JedisPool;

/**
 * ManagerAppConfig.
 */
@Configuration
public class ManagerAppConfig {

    @Bean
    public Executor monitoringCommandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("emma.Monitoring-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SubscriptionTable subscriptionTable() {
        return new SubscriptionTable();
    }

    @Bean
    public BrokerSelectionStrategy brokerSelectionStrategy() {
        return new LowestLatencyStrategy();
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedis() {
        return new JedisPool("localhost");
    }

    @Bean
    public BridgingTable bridgingTable(JedisPool jedis) {
        return new RedisBridgingTable(jedis);
    }
}
