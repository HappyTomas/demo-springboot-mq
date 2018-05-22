package win.leizhang.demo.springboot.mq.bootstrap.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = {"win.leizhang.demo.springboot.mq.dal.automapperdao.mapper.dsoauth2", "win.leizhang.demo.springboot.mq.dal.customdao.dao"}, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class DruidDataSourceConfig {

    @Primary
    @ConfigurationProperties("spring.datasource.druid.master")
    @Bean
    public DataSource dsMaster() {
        return DruidDataSourceBuilder.create().build();
    }

    @ConfigurationProperties("spring.datasource.druid.master")
    @Bean
    public DataSource dsOne() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(dsMaster());
    }

    @Primary
    @Bean
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("dsMaster") DataSource masterDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);

        // 设置sqlMap的解析
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:mybatis/automapperdao/dsoauth2/*.xml");
        Resource[] customResources = resolver.getResources("classpath:mybatis/customdao/*.xml");
        sessionFactory.setMapperLocations(ArrayUtils.addAll(resources, customResources));

        return sessionFactory.getObject();
    }

}
