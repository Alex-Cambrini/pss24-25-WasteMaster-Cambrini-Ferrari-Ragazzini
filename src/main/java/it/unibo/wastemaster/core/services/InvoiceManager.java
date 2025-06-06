package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.InvoiceDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Invoice;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceManager {

    private final InvoiceDAO invoiceDAO;
    private final CollectionDAO collectionDAO;
    private static final double FIXED_FEE = 30.0;

    public InvoiceManager(InvoiceDAO invoiceDAO, CollectionDAO collectionDAO) {
        this.invoiceDAO = invoiceDAO;
        this.collectionDAO = collectionDAO;
    }

    

}
