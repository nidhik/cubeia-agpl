package com.cubeia.games.poker.common.jpa;

import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Helper class for wrapping JPA calls in a transaction and with a cleared entity manager
 * to avoid caching problems.
 * 
 * @author w
 */
public class TransactionHelper {
    
    /**
     * Wrap the call in a JPA transaction and clear the entity manager before start.
     * @param em entity manager
     * @param transactionalCall call to do within the transaction
     * @throws RuntimeException wrapping the causing exception in the call
     * @return
     */
    public static <R> R doInTrasaction(EntityManager em, Callable<R> transactionalCall) {
        R result;
        
        // IMPORTANT: Clear entity manager to avoid caching entities forever as we are running the whole system with a 
        // single entity manager.
        em.clear(); 
        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            result = transactionalCall.call();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }        
        
        tx.commit();
        return result;
    }
    

}
