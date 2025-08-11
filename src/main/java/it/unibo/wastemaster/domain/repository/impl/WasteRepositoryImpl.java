package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.repository.WasteRepository;
import it.unibo.wastemaster.infrastructure.dao.WasteDAO;
import java.util.List;

public class WasteRepositoryImpl implements WasteRepository {

    private final WasteDAO wasteDAO;

    public WasteRepositoryImpl(WasteDAO wasteDAO) {
        this.wasteDAO = wasteDAO;
    }

    @Override
    public List<Waste> findActive() {
        return wasteDAO.findActiveWastes();
    }

    @Override
    public boolean existsByName(String name) {
        return wasteDAO.existsByName(name);
    }

    @Override
    public Waste save(Waste waste) {
        wasteDAO.insert(waste);
        return waste;
    }

    @Override
    public Waste update(Waste waste) {
        wasteDAO.update(waste);
        return waste;
    }
}
