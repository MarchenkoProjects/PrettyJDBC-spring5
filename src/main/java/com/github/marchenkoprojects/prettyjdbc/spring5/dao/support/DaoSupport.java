package com.github.marchenkoprojects.prettyjdbc.spring5.dao.support;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import com.github.marchenkoprojects.prettyjdbc.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Convenient super class to support all data access objects.
 * Requires a {@link SessionFactory} to be set, providing the current {@link Session} object.
 *
 * @author Oleg Marchenko
 */
@Deprecated
public abstract class DaoSupport implements InitializingBean {

    private SessionFactory sessionFactory;

    protected DaoSupport() {
    }

    /**
     * Sets an active session factory.
     *
     * @param sessionFactory active session factory
     */
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieves and returns the current {@link Session} object.
     *
     * @return the current session
     * @see Session
     */
    protected Session getSession() {
        return sessionFactory.getSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(sessionFactory, "Property 'sessionFactory' is required");
    }
}
