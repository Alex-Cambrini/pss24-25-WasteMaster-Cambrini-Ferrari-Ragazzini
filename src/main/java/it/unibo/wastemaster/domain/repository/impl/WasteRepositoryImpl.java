package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.repository.WasteRepository;
import it.unibo.wastemaster.infrastructure.dao.WasteDAO;
import java.util.List;

/**
 * Implementation of {@link WasteRepository} that uses {@link WasteDAO}
 * to perform CRUD operations on Waste entities.
 */
public class WasteRepositoryImpl implements WasteRepository {

    private final WasteDAO wasteDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param wasteDAO the DAO used to access waste data
     */
    public WasteRepositoryImpl(final WasteDAO wasteDAO) {
        this.wasteDAO = wasteDAO;
    }

    /**
     * Retrieves all active wastes.
     *
     * @return a list of active wastes
     */
    @Override
    public List<Waste> findActive() {
        return wasteDAO.findActiveWastes();
    }

    /**
     * Checks if a waste exists by its name.
     *
     * @param name the waste name to check
     * @return true if a waste with the name exists, false otherwise
     */
    @Override
    public boolean existsByName(final String name) {
        return wasteDAO.existsByName(name);
    }

    /**
     * Persists a new waste.
     *
     * @param waste the waste to save
     * @return the saved waste
     */
    @Override
    public Waste save(final Waste waste) {
        wasteDAO.insert(waste);
        return waste;
    }

    /**
     * Updates an existing waste.
     *
     * @param waste the waste to update
     * @return the updated waste
     */
    @Override
    public Waste update(final Waste waste) {
        wasteDAO.update(waste);
        return waste;
    }
}
