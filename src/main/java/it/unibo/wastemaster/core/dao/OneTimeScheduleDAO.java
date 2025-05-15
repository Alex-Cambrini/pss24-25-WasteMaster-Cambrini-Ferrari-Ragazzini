package it.unibo.wastemaster.core.dao;


import it.unibo.wastemaster.core.models.OneTimeSchedule;
import jakarta.persistence.EntityManager;

public class OneTimeScheduleDAO extends GenericDAO<OneTimeSchedule> {

    public OneTimeScheduleDAO(EntityManager entityManager) {
        super(entityManager, OneTimeSchedule.class);
    }
}
