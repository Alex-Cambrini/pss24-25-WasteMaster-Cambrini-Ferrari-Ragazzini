package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.infrastructure.dao.GenericDAO;
import jakarta.persistence.EntityManager;

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
}
