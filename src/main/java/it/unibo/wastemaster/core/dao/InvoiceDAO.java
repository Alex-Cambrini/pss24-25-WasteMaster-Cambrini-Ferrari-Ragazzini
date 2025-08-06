package it.unibo.wastemaster.core.dao;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.core.models.Invoice;


public class InvoiceDAO extends GenericDAO<Invoice> {

    public InvoiceDAO(EntityManager entityManager) {
        super(entityManager, Invoice.class);
    }
}