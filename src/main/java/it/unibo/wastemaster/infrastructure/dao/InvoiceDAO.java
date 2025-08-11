package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Invoice;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO class for managing Invoice entities.
 * Extends GenericDAO to provide basic CRUD operations.
 */
public class InvoiceDAO extends GenericDAO<Invoice> {

    /**
     * Constructs an InvoiceDAO with the given EntityManager.
     *
     * @param entityManager the EntityManager instance to use
     */
    public InvoiceDAO(final EntityManager entityManager) {
        super(entityManager, Invoice.class);
    }

    public List<Invoice> findByDateRange(LocalDate start, LocalDate end) {
        return getEntityManager().createQuery(
                        "SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :start AND "
                                + ":end",
                        Invoice.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

}
