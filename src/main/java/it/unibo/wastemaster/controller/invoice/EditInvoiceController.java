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

public final class EditInvoiceController {

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
    private Invoice invoiceToEdit;
    private List<Collection> allCollections;

    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setInvoiceToEdit(Invoice invoice) {
        this.invoiceToEdit = invoice;
    }

    @FXML
    public void initialize() {
        if (invoiceManager == null) {
            invoiceManager = AppContext.getServiceFactory().getInvoiceManager();
        }
        if (collectionManager == null) {
            collectionManager = AppContext.getServiceFactory().getCollectionManager();
        }
        setupCollectionCombo();
        setupStatusCombo();
    }

    private void setupCollectionCombo() {
        allCollections = collectionManager.getAllCollections();
        collectionCombo.setItems(FXCollections.observableArrayList(allCollections));
        collectionCombo.setConverter(new StringConverter<Collection>() {
            @Override
            public String toString(Collection collection) {
                if (collection == null) return "";
                return "ID: " + collection.getCollectionId();
            }
            @Override
            public Collection fromString(String string) {
                return null;
            }
        });
    }

    private void setupStatusCombo() {
        statusCombo.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
    }

    private void populateFields() {
        if (invoiceToEdit == null) return;
        collectionCombo.setValue(invoiceToEdit.getCollection());
        amountField.setText(String.valueOf(invoiceToEdit.getAmount()));
        statusCombo.setValue(invoiceToEdit.getPaymentStatus());
    }
}
