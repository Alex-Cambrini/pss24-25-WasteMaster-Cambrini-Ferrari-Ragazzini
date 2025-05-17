package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Invoice;
import jakarta.persistence.EntityManager;

public class InvoiceDAO extends GenericDAO<Invoice> {
    public InvoiceDAO(EntityManager entityManager) {
        super(entityManager, Invoice.class);
    }
}