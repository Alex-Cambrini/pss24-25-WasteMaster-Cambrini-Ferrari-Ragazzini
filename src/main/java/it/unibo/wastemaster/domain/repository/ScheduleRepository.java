package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Schedule;
import java.util.List;

public interface ScheduleRepository {
    List<Schedule> findAll();
}
