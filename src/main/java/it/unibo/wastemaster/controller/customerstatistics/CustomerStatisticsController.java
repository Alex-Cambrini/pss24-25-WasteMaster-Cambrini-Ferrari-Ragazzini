package it.unibo.wastemaster.controller.customerstatistics;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for displaying statistics related to a specific customer,
 * such as total invoices, collections, and amounts.
 */
public class CustomerStatisticsController {

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label totalInvoicesLabel;

    @FXML
    private Label totalCollectionsLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Label unpaidAmountLabel;

    @FXML
    private Label paidAmountLabel;

    private CustomerManager customerManager;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private Customer customer;

    /**
     * Sets the customer manager used for retrieving customer data.
     *
     * @param customerManager the CustomerManager to use
     */
    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Sets the invoice manager used for retrieving invoice data.
     *
     * @param invoiceManager the InvoiceManager to use
     */
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    /**
     * Sets the collection manager used for retrieving collection data.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the customer for which statistics are displayed and updates the statistics
     * view.
     *
     * @param customer the customer to display statistics for
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateStatistics();
    }

    /**
     * Initializes the statistics view by clearing all fields.
     */
    @FXML
    public void initialize() {
        clearStatistics();
    }

    /**
     * Updates the statistics labels with data for the current customer.
     */
    private void updateStatistics() {
        if (customer == null) {
            clearStatistics();
            return;
        }
        customerNameLabel.setText(customer.getName() + " " + customer.getSurname());

        List<Invoice> invoices = invoiceManager.getAllInvoices().stream()
                .filter(inv -> inv.getCustomer().equals(customer))
                .toList();

        List<Collection> collections = collectionManager.getAllCollections().stream()
                .filter(c -> c.getSchedule().getCustomer().equals(customer))
                .toList();

        totalInvoicesLabel.setText(String.valueOf(invoices.size()));
        totalCollectionsLabel.setText(String.valueOf(collections.size()));
        double totalAmount = invoices.stream().mapToDouble(Invoice::getAmount).sum();
        totalAmountLabel.setText(String.format("%.2f €", totalAmount));

        double unpaidAmount = invoices.stream()
                .filter(inv -> !inv.getPaymentStatus().toString()
                        .equalsIgnoreCase("PAID"))
                .mapToDouble(Invoice::getAmount)
                .sum();
        double paidAmount = invoices.stream()
                .filter(inv -> inv.getPaymentStatus().toString().equalsIgnoreCase("PAID"))
                .mapToDouble(Invoice::getAmount)
                .sum();

        unpaidAmountLabel.setText(String.format("%.2f €", unpaidAmount));
        paidAmountLabel.setText(String.format("%.2f €", paidAmount));
    }

    /**
     * Clears all statistics labels.
     */
    private void clearStatistics() {
        customerNameLabel.setText("-");
        totalInvoicesLabel.setText("-");
        totalCollectionsLabel.setText("-");
        totalAmountLabel.setText("-");
        unpaidAmountLabel.setText("-");
        paidAmountLabel.setText("-");
    }
}
