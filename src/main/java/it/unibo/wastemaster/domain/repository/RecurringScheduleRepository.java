package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import java.util.List;
import java.util.Optional;

public interface RecurringScheduleRepository {

    void save(RecurringSchedule schedule);

    void update(RecurringSchedule schedule);

    Optional<RecurringSchedule> findById(Integer id);

    List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections();

    List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday();

    List<RecurringSchedule> findSchedulesByCustomer(Customer customer);
}
