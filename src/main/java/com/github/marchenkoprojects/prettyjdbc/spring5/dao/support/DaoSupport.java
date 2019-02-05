package com.github.marchenkoprojects.prettyjdbc.spring5.dao.support;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import com.github.marchenkoprojects.prettyjdbc.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Convenient super class to support all data access objects.
 * Requires a {@link SessionFactory} to be set, providing the current {@link Session} object.
 *
 * @author Oleg Marchenko
 */
public abstract class DaoSupport implements InitializingBean {

    private SessionFactory sessionFactory;

    /**
     * Sets an active session factory.
     *
     * @param sessionFactory active session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieves and returns the current {@link Session} object.
     *
     * @return the current session
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionFactory, "Property 'sessionFactory' is required");
    }
}
