package it.unibo.wastemaster.infrastructure.dao;

import java.util.List;




import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Trip;
import jakarta.persistence.EntityManager;

/**
 * DAO class for managing Trip entities.
 * Extends GenericDAO to provide basic CRUD operations.
 */
public class TripDAO extends GenericDAO<Trip> {

    /**
     * Constructs a TripDAO with the given EntityManager.
     *
     * @param entityManager the EntityManager instance to use
     */
    public TripDAO(final EntityManager entityManager) {
        super(entityManager, Trip.class);
    }

     /**
     * Finds all collections by postal code (CAP).
     *
     * @param postalCode the postal code to filter collections
     * @return a list of collections associated with the given postal code
     */
    
       
    public List<Collection> findCollectionsByPostalCode(String postalCode) {
        String jpql = "SELECT c FROM Trip t JOIN t.collections c WHERE t.postalCode = :postalCode";
        return getEntityManager().createQuery(jpql, Collection.class)
                .setParameter("postalCode", postalCode)
                .getResultList();
    }
}
