package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO class for managing {@link Invoice} entities.
 * <p>
 * This class is {@code final} and not intended for extension.
 * It provides read-oriented queries in addition to the basic CRUD from
 * {@link GenericDAO}.
 */
public final class InvoiceDAO extends GenericDAO<Invoice> {

    /**
     * Limit used for fetching the most recently modified invoices.
     */
    private static final int LAST_INVOICES_LIMIT = 5;

    /**
     * Constructs an InvoiceDAO with the given EntityManager.
     *
     * @param entityManager the EntityManager instance to use
     */
    public InvoiceDAO(final EntityManager entityManager) {
        super(entityManager, Invoice.class);
    }

    /**
     * Finds invoices issued between the given start and end dates (inclusive).
     *
     * @param start the start date of the range
     * @param end the end date of the range
     * @return list of invoices issued in the given date range
     */
    public List<Invoice> findByDateRange(final LocalDate start, final LocalDate end) {
        return getEntityManager().createQuery(
                        "SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :start AND "
                                + ":end",
                        Invoice.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    /**
     * Finds invoices belonging to a given customer.
     *
     * @param customer the customer whose invoices should be retrieved
     * @return list of invoices for the specified customer
     */
    public List<Invoice> findByCustomer(final Customer customer) {
        return getEntityManager().createQuery(
                        "SELECT i FROM Invoice i WHERE i.customer = :customer",
                        Invoice.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    /**
     * Retrieves the most recently modified invoices, ordered by last modification
     * date descending.
     *
     * @return up to {@link #LAST_INVOICES_LIMIT} invoices sorted by last modified
     * date
     */
    public List<Invoice> findLast5InvoicesEvent() {
        return getEntityManager().createQuery(
                        "SELECT i FROM Invoice i ORDER BY i.lastModified DESC",
                        Invoice.class)
                .setMaxResults(LAST_INVOICES_LIMIT)
                .getResultList();
    }
}
