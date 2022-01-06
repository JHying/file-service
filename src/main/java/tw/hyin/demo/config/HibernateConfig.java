package tw.hyin.demo.config;

import java.io.IOException;
import java.sql.Types;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

import tw.hyin.demo.utils.Log;

/*
在 spring application 已設定不要自動取得 db 設定，此處將自行宣告使用 hibernate (並以 spring orm 代管)
 */
@Configuration
public class HibernateConfig extends SQLServerDialect {

    private Properties hibernateProperties = new Properties();
    private static final String Hibernate_EntityPackages = "tw.hyin.demo.entity";
    private static final String HibernateProperties = "hibernate.properties";

    @Value("${spring.sql.init.driver-class-name}")
    private String datasourceDriver;

    @Value("${spring.sql.init.url}")
    private String datasourceUrl;

    @Value("${spring.sql.init.username}")
    private String dbUser;

    @Value("${spring.sql.init.password}")
    private String dbPass;

    //解決 MappingException: No Dialect mapping for JDBC (型態不一致)
    public HibernateConfig() {
        super();
        registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.CHAR, 1, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.NCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.TEXT.getName());
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.NTEXT.getName());
        registerHibernateType(Types.NCLOB, StandardBasicTypes.CLOB.getName());
    }

    @PostConstruct
    public void loadHibernateProperties() {
        try {
            hibernateProperties.load(new ClassPathResource(HibernateProperties).getInputStream());
            Log.info("Initialized hibernate properties complete.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Initialized hibernate properties exception: " + e.getMessage());
        }
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        //See: application.properties
        dataSource.setDriverClassName(datasourceDriver);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        Log.info("Initialized hibernate dataSource complete.");
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {
        //設定 sessionFactory
        try {
            LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
            // Package contain entity classes
            factoryBean.setPackagesToScan(Hibernate_EntityPackages);
            factoryBean.setDataSource(dataSource());
            factoryBean.setHibernateProperties(hibernateProperties);
            factoryBean.afterPropertiesSet();
            Log.info("Initialized hibernate sessionFactory complete.");
            return factoryBean.getObject();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.error("Initialized hibernate sessionFactory exception: " + e.getMessage());
            return null;
        }
    }

    @Autowired
    @Primary //沒特別寫 value 以這個為主
    @Bean
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        //注入 sessionFactory 並設定 HibernateTransactionManager
        //被標註 @Transactional 者可給 spring 代管
        Log.info("Initialized hibernate transactionManager complete.");
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public FilterRegistrationBean<OpenSessionInViewFilter> filterRegistrationBean() {
        //解決 Hibernate LazyInitializationException。
        //將 session 交給 servlet filter 管理，有請求時開啟 session，響應結束則關閉。
        FilterRegistrationBean<OpenSessionInViewFilter> registrationBean = new FilterRegistrationBean<>();
        //if you are using Hibernate, declare OpenSessionInViewFilter.
        //But if you are using JPA, declare OpenEntityManagerInViewFilter.
        //OpenEntityManagerInViewFilter filter = new OpenEntityManagerInViewFilter();
        OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(7);
        Log.info("Initialized hibernate openSessionInViewFilter complete.");
        return registrationBean;
    }

}
