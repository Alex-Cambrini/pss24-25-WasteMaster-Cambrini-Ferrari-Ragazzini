package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Waste;
import java.util.List;

/**
 * Repository interface for managing Waste entities.
 * Provides CRUD operations and retrieval methods for active wastes and existence checks.
 */
public interface WasteRepository {

    /**
     * Retrieves all active (not deleted) waste types.
     *
     * @return a list of active Waste entities
     */
    List<Waste> findActive();

    /**
     * Checks if a waste type with the given name already exists.
     *
     * @param name the name of the waste type to check
     * @return true if a waste with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Persists a new waste type.
     *
     * @param waste the Waste entity to save
     * @return the saved Waste entity
     */
    Waste save(Waste waste);

    /**
     * Updates an existing waste type.
     *
     * @param waste the Waste entity to update
     * @return the updated Waste entity
     */
    Waste update(Waste waste);
}
