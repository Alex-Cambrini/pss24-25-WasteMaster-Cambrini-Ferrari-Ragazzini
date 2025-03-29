package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.utils.TransactionHelper;
import it.unibo.wastemaster.database.HibernateUtil;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.persistence.EntityManager;

import java.util.Date;

public class GenericDAO<T> {
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    public GenericDAO(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public void insert(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> entityManager.persist(entity));
    }

    public void update(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> entityManager.merge(entity));
    }

    public void delete(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> {
            T attached = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(attached);
        });
    }

    public T findById(int id) {
        return entityManager.find(entityClass, id);
    }

    // Test
    public static void main(String[] args) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        GenericDAO<Collection> collectionDAO = new GenericDAO<>(entityManager, Collection.class);


        Collection collection = new Collection(
                null,
                new Date(),
                null,
                null,
                10,
                0,
                false
        );


        collectionDAO.insert(collection);
        System.out.println("Collection inserita con successo!");

        Collection foundCollection = collectionDAO.findById(collection.getCollectionId());
        if (foundCollection != null) {
            System.out.println("Collection trovata: " + foundCollection);
        } else {
            System.out.println("Errore: Collection non trovata!");
        }


        Customer newCustomer = new Customer("Nome", "Cognome", new Location("Via Milano", "12", "Milano", "Italia"), "email@example.com", "1234567890");

        // Aggiornamento
        foundCollection.setCustomer(newCustomer);
        collectionDAO.update(foundCollection);
        System.out.println("Collection aggiornata con successo!");

        // Eliminazione
        collectionDAO.delete(foundCollection);
        System.out.println("Collection eliminata con successo!");

        entityManager.close();
        HibernateUtil.shutdown();
    }
}
