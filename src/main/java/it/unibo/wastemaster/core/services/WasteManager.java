package it.unibo.wastemaster.core.services;

import java.util.List;

import it.unibo.wastemaster.core.dao.WasteDAO;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class WasteManager {
    private WasteDAO wasteDAO;

    public WasteManager(WasteDAO wasteDAO) {
        this.wasteDAO = wasteDAO;
    }

    public List<Waste> getAllWastes() {
        return wasteDAO.findAll();
    }

    public Waste addWaste(Waste waste) {
        ValidateUtils.requireArgNotNull(waste, "Waste cannot be null");
        ValidateUtils.validateEntity(waste);

        if (isWasteNameRegistered(waste.getWasteName())) {
            throw new IllegalArgumentException("Waste with the same name already exists.");
        }

        wasteDAO.insert(waste);
        return waste;
    }

    private boolean isWasteNameRegistered(String name) {
        return wasteDAO.existsByName(name);
    }

    public boolean softDeleteWaste(Waste waste) {
        try {
            ValidateUtils.requireArgNotNull(waste, "Waste cannot be null");
            ValidateUtils.requireArgNotNull(waste.getWasteId(), "Waste ID cannot be null");
            waste.delete();
            wasteDAO.update(waste);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
