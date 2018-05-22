/**
 * Created by zhanglei863 on 2017/12/21.
 */
package win.leizhang.demo.springboot.mq.bootstrap.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.crt.jms.mq.JmsCluster;
import com.crt.jms.mq.JmsClusterMgr;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import win.leizhang.demo.springboot.mq.service.JmsRegisterListenerFactory;

import javax.sql.DataSource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MqJmsConfig {

    @Value("${mq.jms.urls}")
    private String urls;
    @Value("${mq.jms.username}")
    private String username;
    @Value("${mq.jms.password}")
    private String password;
    @Value("${mq.jms.maxconnections}")
    private int maxconnections;
    @Value("${mq.jms.retrycount}")
    private int retrycount;
    @Value("${mq.jms.retryinterval}")
    private int retryinterval;

    @Bean
    public ThreadPoolTaskExecutor jmsProviderTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(200);
        executor.setMaxPoolSize(380);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(300);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        executor.setRejectedExecutionHandler(handler);
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor jmsConsumerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(200);
        executor.setMaxPoolSize(380);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(300);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        executor.setRejectedExecutionHandler(handler);
        return executor;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public JmsCluster jmsNewCluster() {
        JmsCluster jmsCluster = new JmsCluster();
        jmsCluster.setUrls(urls);
        jmsCluster.setUserName(username);
        jmsCluster.setPassword(password);
        jmsCluster.setMaxConnections(maxconnections);
        jmsCluster.setIdleTimeout(60000);
        jmsCluster.setExpiryTimeout(600000);
        jmsCluster.setPubSubDomain(false);
        jmsCluster.setUseAsyncSend(true);
        jmsCluster.setAlwaysSessionAsync(false);
        jmsCluster.setOptimizeAcknowledge(true);
        jmsCluster.setProducerWindowSize(1024000);

        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(1);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setMaximumRedeliveries(-1);
        redeliveryPolicy.setBackOffMultiplier(2.0);

        jmsCluster.setPrefetchPolicy(prefetchPolicy);
        jmsCluster.setRedeliveryPolicy(redeliveryPolicy);
        return jmsCluster;
    }

    @Bean(initMethod = "init")
    public JmsClusterMgr jmsNewClusterMgr(@Qualifier("dsMaster") DataSource masterDataSource) {
        JmsClusterMgr jmsClusterMgr = new JmsClusterMgr();
        jmsClusterMgr.setJmsCluster(jmsNewCluster());
        jmsClusterMgr.setRetryCount(retrycount);
        jmsClusterMgr.setRetryInterval(retryinterval);
        jmsClusterMgr.setDataSource((DruidDataSource) masterDataSource);
        return jmsClusterMgr;
    }

    @Bean(initMethod = "init")
    public JmsRegisterListenerFactory mqRegisterListenerFactory(@Qualifier("jmsNewClusterMgr") JmsClusterMgr jmsClusterMgr) {
        JmsRegisterListenerFactory listenerFactory = new JmsRegisterListenerFactory();
        listenerFactory.setJmsClusterMgr(jmsClusterMgr);
        return listenerFactory;
    }

}
