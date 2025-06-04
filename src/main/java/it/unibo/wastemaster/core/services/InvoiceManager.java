package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.InvoiceDAO;

public class InvoiceManager {

    private final InvoiceDAO invoiceDAO;

    public InvoiceManager(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

}
