package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Invoice;
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
