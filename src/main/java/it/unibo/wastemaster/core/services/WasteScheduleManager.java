package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

public class WasteScheduleManager {
    private final WasteScheduleDAO wasteScheduleDAO;

    public WasteScheduleManager(WasteScheduleDAO wasteScheduleDAO) {
        this.wasteScheduleDAO = wasteScheduleDAO;
    }

    public WasteSchedule getWasteScheduleForWaste(Waste.WasteType wasteType) {
        return wasteScheduleDAO.findByWasteType(wasteType);
    }
}
