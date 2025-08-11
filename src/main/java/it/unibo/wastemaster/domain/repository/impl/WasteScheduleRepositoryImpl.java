package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.infrastructure.dao.WasteScheduleDAO;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.repository.WasteScheduleRepository;

import java.util.Objects;

public class WasteScheduleRepositoryImpl implements WasteScheduleRepository {

    private final WasteScheduleDAO wasteScheduleDAO;

    public WasteScheduleRepositoryImpl(WasteScheduleDAO wasteScheduleDAO) {
        this.wasteScheduleDAO = Objects.requireNonNull(wasteScheduleDAO);
    }

    @Override
    public WasteSchedule findScheduleByWaste(Waste waste) {
        return wasteScheduleDAO.findSchedulebyWaste(waste);
    }

    @Override
    public WasteSchedule save(WasteSchedule schedule) {
        wasteScheduleDAO.insert(schedule);
        return schedule;
    }

    @Override
    public WasteSchedule update(WasteSchedule schedule) {
        wasteScheduleDAO.update(schedule);
        return schedule;
    }
}
