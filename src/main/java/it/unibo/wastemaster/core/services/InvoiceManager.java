package it.unibo.wastemaster.core.services;



import it.unibo.wastemaster.core.dao.InvoiceDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceManager {

    private final InvoiceDAO invoiceDAO;

    public InvoiceManager(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }


}
