package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Waste;
import java.util.List;

public interface WasteRepository {

    List<Waste> findActive();
    boolean existsByName(String name);
    Waste save(Waste waste);
    Waste update(Waste waste);
}
