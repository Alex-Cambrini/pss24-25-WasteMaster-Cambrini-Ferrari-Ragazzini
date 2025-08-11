package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.ScheduleRepository;
import java.util.List;

public class ScheduleManager {
    private final ScheduleRepository scheduleRepository;

    public ScheduleManager(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> findAllSchedule() {
        List<Schedule> allSchedule = scheduleRepository.findAll();
        return allSchedule;
    }
}
