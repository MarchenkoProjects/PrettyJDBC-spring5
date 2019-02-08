package com.github.marchenkoprojects.prettyjdbc.spring5;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import com.github.marchenkoprojects.prettyjdbc.session.Session;
import com.github.marchenkoprojects.prettyjdbc.spring5.transaction.PrettyJdbcTransactionManager;
import com.github.marchenkoprojects.prettyjdbc.transaction.Transaction;
import com.github.marchenkoprojects.prettyjdbc.transaction.TransactionStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @author Oleg Marchenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PrettyJdbcTransactionManagerTest.TestConfig.class)
public class PrettyJdbcTransactionManagerTest {

    @Configuration
    @EnableTransactionManagement
    public static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:hsqldb:mem:test_db", "SA", "");
        }

        @Bean
        public SessionFactory sessionFactory(DataSource dataSource) {
            LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
            localSessionFactoryBean.setDataSource(dataSource);
            return localSessionFactoryBean.getObject();
        }

        @Bean
        public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
            PrettyJdbcTransactionManager transactionManager = new PrettyJdbcTransactionManager();
            transactionManager.setSessionFactory(sessionFactory);
            return transactionManager;
        }

        @Bean
        public TransactionalService transactionalService(SessionFactory sessionFactory) {
            return new TransactionalService(sessionFactory);
        }
    }

    public static class TransactionalService {

        private final SessionFactory sessionFactory;

        public TransactionalService(SessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        @Transactional
        public void executeTransactionSuccessfully() {
            checkTransaction();
        }

        @Transactional
        public void executeTransactionWithThrowException() {
            checkTransaction();

            throw new RuntimeException();
        }

        private void checkTransaction() {
            Session currentSession = sessionFactory.getCurrentSession();
            Assert.assertNotNull(currentSession);
            Assert.assertTrue(currentSession.isOpen());

            Transaction currentTransaction = currentSession.getTransaction();
            Assert.assertNotNull(currentTransaction);
            Assert.assertEquals(currentTransaction.getStatus(), TransactionStatus.ACTIVE);
        }
    }

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TransactionalService transactionalService;

    @Test
    public void testExecuteTransactionSuccessfully() {
        Session currentSession = sessionFactory.getCurrentSession();
        Assert.assertNotNull(currentSession);
        Assert.assertNull(currentSession.getTransaction());

        transactionalService.executeTransactionSuccessfully();

        Transaction currentTransaction = currentSession.getTransaction();
        Assert.assertNotNull(currentTransaction);
        Assert.assertEquals(currentTransaction.getStatus(), TransactionStatus.COMPLETED);
    }

    @Test
    public void testExecuteTransactionUnsuccessfully() {
        Session currentSession = sessionFactory.getCurrentSession();
        Assert.assertNotNull(currentSession);
        Assert.assertNull(currentSession.getTransaction());

        /**
         * We have to catch the exception because
         * {@link org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction(Method, Class, TransactionAspectSupport.InvocationCallback)}
         * throws an exception from the test method up the stack.
         */
        try {
            transactionalService.executeTransactionWithThrowException();
        }
        catch (Exception e) {
        }

        Transaction currentTransaction = currentSession.getTransaction();
        Assert.assertNotNull(currentTransaction);
        Assert.assertEquals(currentTransaction.getStatus(), TransactionStatus.COMPLETED);
    }

    @After
    public void afterTest() {
        SessionFactory.unbindSession();
    }
}
