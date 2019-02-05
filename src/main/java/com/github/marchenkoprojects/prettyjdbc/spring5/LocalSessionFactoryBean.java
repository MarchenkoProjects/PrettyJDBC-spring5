package com.github.marchenkoprojects.prettyjdbc.spring5;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * This is the main way to create and configure a {@link SessionFactory} in a Spring application context;
 * the next step is passing the <code>SessionFactory</code> to the data access objects via dependency injection.
 *
 * @author Oleg Marchenko
 *
 * @see SessionFactory
 */
public class LocalSessionFactoryBean implements FactoryBean<SessionFactory>, InitializingBean {

    private DataSource dataSource;

    /**
     * Sets the {@link DataSource} to be used by the {@link SessionFactory}.
     *
     * @param dataSource configured data source
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionFactory getObject() {
        return SessionFactory.create(() -> dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getObjectType() {
        return SessionFactory.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(dataSource, "Property 'dataSource' is required");
    }
}
