package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Invoice.PaymentStatus;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller for the Add Invoice modal form. Manages input validation and invoice creation logic.
 */
public final class AddInvoiceController {

    @FXML
    private ComboBox<Collection> collectionCombo;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<PaymentStatus> statusCombo;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private List<Collection> allCollections;

    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
}