package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.repository.RecurringScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.RecurringScheduleDAO;
import java.util.List;
import java.util.Optional;

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
    public Optional<RecurringSchedule> findById(Integer id) {
        return recurringScheduleDAO.findById(id);
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
}
