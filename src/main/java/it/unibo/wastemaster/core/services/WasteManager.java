package it.unibo.wastemaster.core.services;

import java.util.List;

import it.unibo.wastemaster.core.dao.WasteDAO;
import it.unibo.wastemaster.core.models.Waste;

public class WasteManager {
    private WasteDAO wasteDAO;

    public WasteManager(WasteDAO wasteDAO) {
        this.wasteDAO = wasteDAO;
    }

    public List<Waste> getAllWastes() {
        return wasteDAO.findAll();
    }
    
}
