package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.repository.WasteRepository;
import java.util.List;

/**
 * Manages operations related to Waste entities, including validation, insertion and soft
 * deletion.
 */
public final class WasteManager {

    private final WasteRepository wasteRepository;

    /**
     * Constructs a WasteManager with the specified DAO.
     *
     * @param wasteRepository the DAO to use, must not be null
     */
    public WasteManager(final WasteRepository wasteRepository) {
        this.wasteRepository = wasteRepository;
    }

    /**
     * Retrieves all active (non-deleted) wastes.
     *
     * @return a list of active Waste objects
     */
    public List<Waste> getActiveWastes() {
        return wasteRepository.findActive();
    }

    /**
     * Adds a new Waste entity after validation.
     *
     * @param waste the waste to add
     * @return the inserted Waste
     * @throws IllegalArgumentException if waste is null or invalid
     */
    public Waste addWaste(final Waste waste) {
        ValidateUtils.requireArgNotNull(waste, "Waste cannot be null");
        ValidateUtils.validateEntity(waste);

        if (isWasteNameRegistered(waste.getWasteName())) {
            throw new IllegalArgumentException(
                    "Waste with the same name already exists.");
        }

        wasteRepository.save(waste);
        return waste;
    }

    /**
     * Checks if a waste with the given name already exists.
     *
     * @param name the name to check
     * @return true if already registered, false otherwise
     */
    private boolean isWasteNameRegistered(final String name) {
        return wasteRepository.existsByName(name);
    }

    /**
     * Performs a soft delete of the waste by marking it as deleted.
     *
     * @param waste the waste to delete
     * @return true if successful, false otherwise
     */
    public boolean softDeleteWaste(final Waste waste) {
        try {
            ValidateUtils.requireArgNotNull(waste, "Waste cannot be null");
            ValidateUtils.requireArgNotNull(waste.getWasteId(),
                    "Waste ID cannot be null");
            waste.delete();
            wasteRepository.update(waste);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
