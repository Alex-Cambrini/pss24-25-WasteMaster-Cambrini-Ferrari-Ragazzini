package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CustomerDetailController {

    @FXML private Label customerNameLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label billedCollectionsLabel;
    @FXML private Label totalInvoicedLabel;
    @FXML private Label totalPaidLabel;
    @FXML private Label totalUnpaidLabel;

    private CollectionManager collectionManager;
    private InvoiceManager invoiceManager;

    public void setManagers(CollectionManager collectionManager, InvoiceManager invoiceManager) {
        this.collectionManager = collectionManager;
        this.invoiceManager = invoiceManager;
    }

    public void setCustomer(Customer customer) {
        customerNameLabel.setText("Customer: " + customer.getName());


        double totalInvoiced = invoiceManager.getTotalBilledAmountForCustomer(customer);
        double totalPaid = invoiceManager.getTotalPaidAmountForCustomer(customer);
        double totalUnpaid = totalInvoiced - totalPaid;


        totalInvoicedLabel.setText("Total Invoiced: " + String.format("%.2f", totalInvoiced));
        totalPaidLabel.setText("Total Paid: " + String.format("%.2f", totalPaid));
        totalUnpaidLabel.setText("Total Unpaid: " + String.format("%.2f", totalUnpaid));
    }
}