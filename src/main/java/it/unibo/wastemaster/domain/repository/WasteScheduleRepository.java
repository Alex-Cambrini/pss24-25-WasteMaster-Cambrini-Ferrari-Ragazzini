package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;

public interface WasteScheduleRepository {

    WasteSchedule findScheduleByWaste(Waste waste);
    WasteSchedule save(WasteSchedule schedule);
    WasteSchedule update(WasteSchedule schedule);
}
