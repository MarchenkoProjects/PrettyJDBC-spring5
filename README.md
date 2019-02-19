# Integrating PrettyJDBC with Spring 5
PrettyJDBC offers a set of Spring integrations that allow it to be used as a full-featured library for the data access layer in Spring applications.

### Getting started ###
#### Installation ####
**Maven:**
```xml
<dependency>
    <groupId>com.github.marchenkoprojects</groupId>
    <artifactId>prettyjdbc-spring5</artifactId>
    <version>0.3</version>
</dependency>
```
**Gradle:**
```groovy
compile 'com.github.marchenkoprojects:prettyjdbc-spring5:0.3'
```

#### Configuration ####
The basic configuration is as follows:
```java
@Configuration
public void Config {

    @Bean
    public DataSource dataSource() {
        return ...
    }
    
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        return localSessionFactoryBean;
    }
}
```
The main bean that needs to be configured is `LocalSessionFactoryBean` which accepts configured `DataSource`.

If you want to use declarative transaction management in a **Spring** application, 
you need to `@EnableTransactionManagement` and configure `PrettyJdbcTransactionManager`.
```java
@Configuration
@EnableTransactionManagement
public void Config {
    // LocalSessionFactoryBean and other configuration
    
    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        PrettyJdbcTransactionManager transactionManager = new PrettyJdbcTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
```
`PrettyJdbcTransactionManager` accepts configured `SessionFactory` from basic configuration.

In applications like **Spring Web MVC** you must use `CurrentSessionManagementInterceptor` to control the closure of the current session.
```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CurrentSessionManagementInterceptor());
    }
}
```