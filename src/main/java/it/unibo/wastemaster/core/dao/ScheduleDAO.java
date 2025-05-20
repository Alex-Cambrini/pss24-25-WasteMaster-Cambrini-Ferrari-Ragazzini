package it.unibo.wastemaster.core.dao;
import it.unibo.wastemaster.core.models.Schedule;
import jakarta.persistence.EntityManager;

public class ScheduleDAO extends GenericDAO<Schedule> {

    public ScheduleDAO(EntityManager entityManager) {
        super(entityManager, Schedule.class);
    }
}
