package it.unibo.wastemaster.core.services;

import java.time.DayOfWeek;

import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class WasteScheduleManager {
    private final WasteScheduleDAO wasteScheduleDAO;

    public WasteScheduleManager(WasteScheduleDAO wasteScheduleDAO) {
        this.wasteScheduleDAO = wasteScheduleDAO;
    }

    public WasteSchedule setupCollectionRoutine(Waste waste, DayOfWeek dayOfWeek) {
        WasteSchedule wasteSchedule = new WasteSchedule(waste, dayOfWeek);
        wasteScheduleDAO.insert(wasteSchedule);
        return wasteSchedule;
    }

    public WasteSchedule changeCollectionDay(WasteSchedule wasteSchedule, DayOfWeek newDayOfWeek) {
        wasteSchedule.setDayOfWeek(newDayOfWeek);
        wasteScheduleDAO.update(wasteSchedule);
        return wasteSchedule;
    }

    public WasteSchedule getWasteScheduleByWaste(Waste waste) {
        ValidateUtils.requireArgNotNull(waste, "WasteType cannot be null");
        WasteSchedule schedule = wasteScheduleDAO.findSchedulebyWaste(waste);
        ValidateUtils.requireStateNotNull(schedule, "No WasteSchedule found for waste type: " + waste.getWasteName());
        return schedule;
    }    
}
