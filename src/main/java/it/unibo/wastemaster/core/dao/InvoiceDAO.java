package it.unibo.wastemaster.core.dao;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.core.models.Invoice;


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