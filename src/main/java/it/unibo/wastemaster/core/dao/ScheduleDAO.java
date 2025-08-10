package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.infrastructure.dao.GenericDAO;
import jakarta.persistence.EntityManager;

/**
 * DAO class for managing Schedule entities.
 */
public class ScheduleDAO extends GenericDAO<Schedule> {

    /**
     * Constructs a new ScheduleDAO with the given EntityManager.
     *
     * @param entityManager the EntityManager used for persistence operations
     */
    public ScheduleDAO(final EntityManager entityManager) {
        super(entityManager, Schedule.class);
    }
}
