package win.leizhang.mqcommon.activemq.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;

public class JmsCluster {
    private static Logger logger = LoggerFactory.getLogger(JmsCluster.class.getName());
    private List<JmsBroker> jmsBrokerList;
    // 连接到JMS服务器的地址，多个MQ地址使用逗号间隔
    private String urls;
    // 连接用户名
    private String userName;
    // 连接密码
    private String password;
    // 最大连接数
    private int maxConnections = 30;
    // 闲置超时时间
    private int idleTimeout = 60000;
    // 过期时间
    private long expiryTimeout = 600000L;
    // 使用异步发送消息
    private boolean useAsyncSend = true;
    // 必须connection上仅仅有一个session相关联。如果存在多个session实例则无效
    private boolean alwaysSessionAsync = false;
    // 缺省情况支持批量确认消息（true）
    private boolean optimizeAcknowledge = true;
    // 在producer发送消息过程中，最大字节数,与使用异步参数同时使用
    private int producerWindowSize = 1024000;
    // 预读取策略
    private ActiveMQPrefetchPolicy prefetchPolicy;
    //
    private RedeliveryPolicy redeliveryPolicy;

    // 区分它采用的模式为false是P2P，为true是订阅
    private boolean pubSubDomain = false;
    // 20161216注:
    // defaultDestinationName这个属性是用于MQ连接修复线程的，
    // 且值必须是固定的ping_queue，务必不能修改该值或去掉该属性
    private String defaultDestinationName = "ping_queue";

    public JmsCluster() {
        super();
        this.prefetchPolicy = new ActiveMQPrefetchPolicy();
        this.prefetchPolicy.setQueuePrefetch(1);
        this.redeliveryPolicy = new RedeliveryPolicy();
        this.redeliveryPolicy.setUseExponentialBackOff(true);
        this.redeliveryPolicy.setMaximumRedeliveries(-1);
        this.redeliveryPolicy.setBackOffMultiplier(2.0d);
    }

    public void start() {
        if (null == urls || 0 == urls.trim().length()) {
            throw new BootstrapException("缺少配置，未通过brokerURLs属性指定MQ服务器地址信息");
        }

        logger.info("MQ集群地址: {}", urls);

        List<JmsBroker> jmsBrokerList = new ArrayList<JmsBroker>();
        String[] brokerURLsArray = urls.trim().split(",");
        if (0 == brokerURLsArray.length) {
            return;
        }
        for (String brokerURL : brokerURLsArray) {
            brokerURL = brokerURL.trim();
            if (0 == brokerURL.length()) {
                continue;
            }
            ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory();
            connFactory.setBrokerURL(brokerURL);
            connFactory.setUserName(userName);
            connFactory.setPassword(password);
            connFactory.setUseAsyncSend(useAsyncSend);
            connFactory.setAlwaysSessionAsync(alwaysSessionAsync);
            connFactory.setOptimizeAcknowledge(optimizeAcknowledge);
            connFactory.setProducerWindowSize(producerWindowSize);
            connFactory.setPrefetchPolicy(prefetchPolicy);
            connFactory.setRedeliveryPolicy(redeliveryPolicy);

            PooledConnectionFactory connPool = new PooledConnectionFactory();
            connPool.setConnectionFactory(connFactory);
            connPool.setMaxConnections(maxConnections);
            connPool.setIdleTimeout(idleTimeout);
            connPool.setExpiryTimeout(expiryTimeout);

            // start pool
            connPool.start();

            JmsTemplate jmsTemplate = new JmsTemplate();
            jmsTemplate.setConnectionFactory(connPool);
            // 区分它采用的模式为false是P2P，为true是订阅
            jmsTemplate.setPubSubDomain(pubSubDomain);
            jmsTemplate.setDefaultDestinationName(defaultDestinationName);

            JmsBroker jmsBroker = new JmsBroker();
            jmsBroker.setJmsFactory(connPool);
            jmsBroker.setJmsTemplate(jmsTemplate);
            jmsBroker.setNormalWork(true);

            jmsBrokerList.add(jmsBroker);
        }

        setJmsBrokerList(jmsBrokerList);
    }

    public void stop() {
        if (null == getJmsBrokerList()) {
            return;
        }

        for (JmsBroker jmsBroker : getJmsBrokerList()) {
            try {
                jmsBroker.getJmsFactory().stop();
            } catch (Throwable e) {
                logger.warn("关闭JMS连接池时发生异常，异常信息：{}", e.getMessage(), e);
            }
        }
    }

    public final void setUrls(String urls) {
        this.urls = urls;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public final void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public final void setExpiryTimeout(long expiryTimeout) {
        this.expiryTimeout = expiryTimeout;
    }

    public final void setUseAsyncSend(boolean useAsyncSend) {
        this.useAsyncSend = useAsyncSend;
    }

    public final void setAlwaysSessionAsync(boolean alwaysSessionAsync) {
        this.alwaysSessionAsync = alwaysSessionAsync;
    }

    public final void setOptimizeAcknowledge(boolean optimizeAcknowledge) {
        this.optimizeAcknowledge = optimizeAcknowledge;
    }

    public final void setProducerWindowSize(int producerWindowSize) {
        this.producerWindowSize = producerWindowSize;
    }

    public final void setPrefetchPolicy(ActiveMQPrefetchPolicy prefetchPolicy) {
        this.prefetchPolicy = prefetchPolicy;
    }

    public final void setRedeliveryPolicy(RedeliveryPolicy redeliveryPolicy) {
        this.redeliveryPolicy = redeliveryPolicy;
    }

    public final void setPubSubDomain(boolean pubSubDomain) {
        this.pubSubDomain = pubSubDomain;
    }

    public final void setDefaultDestinationName(String defaultDestinationName) {
        this.defaultDestinationName = defaultDestinationName;
    }

    public List<JmsBroker> getJmsBrokerList() {
        return this.jmsBrokerList;
    }

    public void setJmsBrokerList(List<JmsBroker> jmsBrokerList) {
        this.jmsBrokerList = jmsBrokerList;
    }

}