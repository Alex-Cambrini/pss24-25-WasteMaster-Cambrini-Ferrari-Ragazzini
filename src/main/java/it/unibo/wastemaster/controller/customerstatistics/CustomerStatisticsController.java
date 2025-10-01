package it.unibo.wastemaster.controller.customerstatistics;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.CollectionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class CustomerStatisticsController {

    @FXML private Label customerNameLabel;
    @FXML private Label totalInvoicesLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label unpaidAmountLabel;
    @FXML private Label paidAmountLabel;

    private CustomerManager customerManager;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private Customer customer;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateStatistics();
    }

    @FXML
    public void initialize() {
        clearStatistics();
    }

    private void updateStatistics() {
        if (customer == null || invoiceManager == null || collectionManager == null) {
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
            .filter(inv -> !inv.getPaymentStatus().toString().equalsIgnoreCase("PAID"))
            .mapToDouble(Invoice::getAmount)
            .sum();
        double paidAmount = invoices.stream()
            .filter(inv -> inv.getPaymentStatus().toString().equalsIgnoreCase("PAID"))
            .mapToDouble(Invoice::getAmount)
            .sum();

        unpaidAmountLabel.setText(String.format("%.2f €", unpaidAmount));
        paidAmountLabel.setText(String.format("%.2f €", paidAmount));
    }

    private void clearStatistics() {
        customerNameLabel.setText("-");
        totalInvoicesLabel.setText("-");
        totalCollectionsLabel.setText("-");
        totalAmountLabel.setText("-");
        unpaidAmountLabel.setText("-");
        paidAmountLabel.setText("-");
    }
}