package module.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.log4j.Logger;

import module.config.ConfigWago;

public class persistenceManager {

    private static Logger logger = Logger.getLogger(persistenceManager.class.getName());
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(ConfigWago.getPersistenceUnitName());
        } catch (PersistenceException e) {
            logger.error("PersistenceManager - EntityManagerFactory", e);
        }
    }

    public persistenceManager() {
    }

    private static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closePersistenceConnection() {
        try {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        } catch (Exception e) {
            logger.error("PersistenceManager - closePersistenceConnection", e);
        }
    }
    
    public void persistEntity(Object entity) {

        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(entity);
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("PersistenceManager - persistEntity", e);
        } finally {
            entityManager.close();
        }
    }

    public void updateEntity(String entityNamedQuery, Map<String, Object> queryParameters) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNamedQuery(entityNamedQuery);
            for (Map.Entry<String, Object> parameterPair : queryParameters.entrySet()) {
                query.setParameter(parameterPair.getKey(), parameterPair.getValue());
            }
            query.executeUpdate();
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("PersistenceManager - updateEntity", e);
        } finally {
            entityManager.close();
        }
    }

    public Object findEntity(String entityNamedQuery, Map<String, Object> queryParameters) {
        Object entity = null;
        EntityManager entityManager = emf.createEntityManager();

        try {
            Query query = entityManager.createNamedQuery(entityNamedQuery);
            query.setHint("javax.persistence.cache.retrieveMode", "BYPASS"); // refresh query result
            for (Map.Entry<String, Object> parameterPair : queryParameters.entrySet()) {
                query.setParameter(parameterPair.getKey(), parameterPair.getValue());
            }
            entity = query.getSingleResult();
        } catch (NoResultException ex) {
            //
        } catch (Exception e) {
            logger.error("PersistenceManager - findEntity", e);
        } finally {
            entityManager.close();
        }

        return entity;
    }

    public static List<equipment_info> getAllHB() {
        List<equipment_info> entities = null;
        EntityManager entityManager = emf.createEntityManager();

        try {
            entityManager.getEntityManagerFactory().getCache().evictAll();
            Query query = entityManager.createNamedQuery("equipment_info.equipmentHB");
            query.setHint("javax.persistence.cache.retrieveMode", "BYPASS"); // refresh query result
            entities = query.getResultList();
        } catch (Exception e) {
            logger.error("PersistenceManager - findEntities", e);
        } finally {
            entityManager.close();
        }

        return entities;
    }

    public void updateEIU1HB(int heartbeatRunningNumber) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNamedQuery("equipment_info.eiu1HB");
            query.setParameter(1, heartbeatRunningNumber);
        
            query.executeUpdate();
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("PersistenceManager - updateEIU1HB", e);
        } finally {
            entityManager.close();
        }
    }

    public void updatePLCHB(Integer heartbeatRunningNumber) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNamedQuery("equipment_info.PLCHB");
            query.setParameter(1, heartbeatRunningNumber);
        
            query.executeUpdate();
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("PersistenceManager - updatePLCHB", e);
        } finally {
            entityManager.close();
        }
    }

    public static void updateJcHB(int heartbeatRunningNumber) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNamedQuery("equipment_info.jcHB");
            query.setParameter(1, heartbeatRunningNumber);


            query.executeUpdate();
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("PersistenceManager - updateJCHB", e);
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findEntities(String entityNamedQuery, Map<String, Object> queryParameters) {
        List<T> entities = null;
        EntityManager entityManager = emf.createEntityManager();
        

        try {
            //entityManager.getEntityManagerFactory().getCache().evictAll();
            Query query = entityManager.createNamedQuery(entityNamedQuery);
            query.setHint("javax.persistence.cache.retrieveMode", "BYPASS"); // refresh query result
            for (Map.Entry<String, Object> parameterPair : queryParameters.entrySet()) {
                query.setParameter(parameterPair.getKey(), parameterPair.getValue());
            }
            entities = query.getResultList();
        } catch (Exception e) {
            logger.error("PersistenceManager - findEntities", e);
        } finally {
            entityManager.close();
        }

        return entities;
    }

    public List<Date> getPlcLastComm() {
        List<Date> entities = null;
        EntityManager entityManager = emf.createEntityManager();

        try {
            entityManager.getEntityManagerFactory().getCache().evictAll();
            Query query = entityManager.createNamedQuery("equipment_info.equipmentLastComm");
            query.setHint("javax.persistence.cache.retrieveMode", "BYPASS"); // refresh query result
            entities = query.getResultList();
        } catch (Exception e) {
            logger.error("PersistenceManager - findEntities", e);
        } finally {
            entityManager.close();
        }

        return entities;
    }

    public List<Date> getJcLastComm() {
        List<Date> entities = null;
        EntityManager entityManager = emf.createEntityManager();

        try {
            entityManager.getEntityManagerFactory().getCache().evictAll();
            Query query = entityManager.createNamedQuery("equipment_info.jcLastCom");
            query.setHint("javax.persistence.cache.retrieveMode", "BYPASS"); // refresh query result
            entities = query.getResultList();
        } catch (Exception e) {
            logger.error("PersistenceManager - findEntities", e);
        } finally {
            entityManager.close();
        }

        return entities;
    }

    public Object getLatestSingleResult(String nameQuery, Map<String, Object> hmPara) {
        Object result = null;
        EntityManager entityManager = getEntityManager();

        try {
            Query q = entityManager.createNamedQuery(nameQuery);
            q.setMaxResults(1);

            if (hmPara != null) {
                for (Map.Entry<String, Object> pair : hmPara.entrySet()) {
                    q.setParameter(pair.getKey(), pair.getValue());
                }
            }

            result = q.getSingleResult();
        } catch (NoResultException ex) {
            //
        } catch (Exception e) {
            logger.error("PersistenceManager - getLatestSingleResult", e);
        } finally{
            entityManager.close();
        }

        return result;
    }
    
}
