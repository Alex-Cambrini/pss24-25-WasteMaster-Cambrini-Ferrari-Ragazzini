package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;

import java.util.List;

public interface RecurringScheduleRepository {

    void save(RecurringSchedule schedule);
    void update(RecurringSchedule schedule);
    List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections();
    List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday();
    List<RecurringSchedule> findSchedulesByCustomer(Customer customer);
    List<RecurringSchedule> findAll();
}
