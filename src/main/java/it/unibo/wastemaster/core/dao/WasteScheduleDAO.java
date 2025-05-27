package it.unibo.wastemaster.core.dao;

import java.util.List;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * DAO for managing WasteSchedule entities.
 */
public final class WasteScheduleDAO extends GenericDAO<WasteSchedule> {

    /**
     * Constructs a WasteScheduleDAO with the given entity manager.
     * 
     * @param entityManager the EntityManager to use
     */
    public WasteScheduleDAO(final EntityManager entityManager) {
        super(entityManager, WasteSchedule.class);
    }

    /**
     * Finds the schedule associated with the given waste.
     * 
     * @param waste the Waste entity to look up
     * @return the WasteSchedule if found, or null otherwise
     */
    public WasteSchedule findSchedulebyWaste(final Waste waste) {
        TypedQuery<WasteSchedule> query = entityManager.createQuery(
                "SELECT ws FROM WasteSchedule ws WHERE ws.waste.name = :wasteName",
                WasteSchedule.class);
        query.setParameter("wasteName", waste.getWasteName());

        List<WasteSchedule> result = query.setMaxResults(1).getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }
}
