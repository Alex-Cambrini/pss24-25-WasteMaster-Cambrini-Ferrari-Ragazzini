package it.unibo.wastemaster.domain.repository.impl;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.domain.repository.RecurringScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;

import java.util.List;

public class RecurringScheduleRepositoryImpl implements RecurringScheduleRepository {

    private final RecurringScheduleDAO recurringScheduleDAO;

    public RecurringScheduleRepositoryImpl(RecurringScheduleDAO recurringScheduleDAO) {
        this.recurringScheduleDAO = recurringScheduleDAO;
    }

    @Override
    public void save(RecurringSchedule schedule) {
        recurringScheduleDAO.insert(schedule);
    }

    @Override
    public void update(RecurringSchedule schedule) {
        recurringScheduleDAO.update(schedule);
    }

    @Override
    public List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections() {
        return recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();
    }

    @Override
    public List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday() {
        return recurringScheduleDAO.findActiveSchedulesWithNextDateBeforeToday();
    }

    @Override
    public List<RecurringSchedule> findSchedulesByCustomer(Customer customer) {
        return recurringScheduleDAO.findSchedulesByCustomer(customer);
    }

    @Override
    public List<RecurringSchedule> findAll() {
        return recurringScheduleDAO.findAll();
    }
}
