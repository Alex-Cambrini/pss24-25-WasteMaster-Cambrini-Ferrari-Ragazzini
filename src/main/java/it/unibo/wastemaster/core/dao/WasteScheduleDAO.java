package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class WasteScheduleDAO extends GenericDAO<WasteSchedule> {

    public WasteScheduleDAO(EntityManager entityManager) {
        super(entityManager, WasteSchedule.class);
    }


    public WasteSchedule findByWasteType(Waste.WasteType wasteType) {
        TypedQuery<WasteSchedule> query = entityManager.createQuery(
                "SELECT ws FROM WasteSchedule ws WHERE ws.waste.type = :wasteType", WasteSchedule.class);
        query.setParameter("wasteType", wasteType);

        List<WasteSchedule> result = query.setMaxResults(1).getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }
}
