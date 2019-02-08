package com.github.marchenkoprojects.prettyjdbc.spring5.transaction;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import com.github.marchenkoprojects.prettyjdbc.session.Session;
import com.github.marchenkoprojects.prettyjdbc.transaction.Transaction;
import com.github.marchenkoprojects.prettyjdbc.transaction.TransactionIsolationLevel;
import com.github.marchenkoprojects.prettyjdbc.transaction.TransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Standard {@link org.springframework.transaction.PlatformTransactionManager} implementation
 * for a single specific {@link SessionFactory}.
 *
 * @author Oleg Marchenko
 */
public class PrettyJdbcTransactionManager extends AbstractPlatformTransactionManager {

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
     * {@inheritDoc}
     */
    @Override
    protected Object doGetTransaction() throws TransactionException {
        Session currentSession = sessionFactory.getCurrentSession();
        Transaction currentTransaction = currentSession.getTransaction();
        if (currentTransaction == null || currentTransaction.getStatus() == TransactionStatus.COMPLETED) {
            return currentSession.newTransaction();
        }
        return currentTransaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doBegin(Object transactionObject, TransactionDefinition transactionDefinition) throws TransactionException {
        Transaction transaction = (Transaction) transactionObject;
        doSetTransactionReadOnly(transaction, transactionDefinition.isReadOnly());
        doSetTransactionIsolationLevel(transaction, transactionDefinition.getIsolationLevel());
        transaction.begin();
    }

    private void doSetTransactionReadOnly(Transaction transaction, boolean readOnly) {
        transaction.setReadOnly(readOnly);
    }

    private void doSetTransactionIsolationLevel(Transaction transaction, int isolationLevel) {
        if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
            transaction.setIsolationLevel(TransactionIsolationLevel.valueOf(isolationLevel));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCommit(DefaultTransactionStatus transactionStatus) throws TransactionException {
        Transaction transaction = (Transaction) transactionStatus.getTransaction();
        transaction.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRollback(DefaultTransactionStatus transactionStatus) throws TransactionException {
        Transaction transaction = (Transaction) transactionStatus.getTransaction();
        transaction.rollback();
    }
}
