package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository {

    List<Collection> findAllBySchedule(Schedule schedule);

    List<Collection> findByStatus(CollectionStatus status);

    Optional<Collection> findActiveByRecurringSchedule(RecurringSchedule schedule);

    List<Collection> findByDateRange(LocalDate start, LocalDate end);

    void update(Collection collection);

    void save(Collection collection);

    void delete(Collection collection);
}
