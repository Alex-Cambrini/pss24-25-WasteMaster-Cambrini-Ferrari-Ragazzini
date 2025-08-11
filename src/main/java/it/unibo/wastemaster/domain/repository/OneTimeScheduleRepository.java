package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import java.util.Optional;

public interface OneTimeScheduleRepository {

    void save(OneTimeSchedule schedule);

    void update(OneTimeSchedule schedule);

    Optional<OneTimeSchedule> findById(Integer id);
}
