package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.OneTimeSchedule;
import jakarta.persistence.EntityManager;

/**
 * DAO for {@link OneTimeSchedule} entity operations.
 */
public class OneTimeScheduleDAO extends GenericDAO<OneTimeSchedule> {

    /**
     * Constructor that initializes the DAO with the provided entity manager.
     * 
     * @param entityManager the entity manager instance
     */
    public OneTimeScheduleDAO(final EntityManager entityManager) {
        super(entityManager, OneTimeSchedule.class);
    }
}
