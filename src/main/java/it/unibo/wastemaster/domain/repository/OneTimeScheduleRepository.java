package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.OneTimeSchedule;

public interface OneTimeScheduleRepository {

    void save(OneTimeSchedule schedule);
    void update(OneTimeSchedule schedule);
}
