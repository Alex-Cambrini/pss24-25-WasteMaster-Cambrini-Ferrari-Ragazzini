package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
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
    private boolean invoiceCreated = false;

    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public boolean isInvoiceCreated() {
    return invoiceCreated;
    }
    

    /**
     * Sets up UI components and listeners.
     */
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

    /**
     * Handles the save action for the invoice.
     */
    @FXML
    public void handleSaveInvoice(final ActionEvent event) {
        try {
            Collection selectedCollection = collectionCombo.getValue();
            
            PaymentStatus status = statusCombo.getValue();

            if (selectedCollection == null) {
                throw new IllegalArgumentException("- Please select a collection");
            }
            if (status == null) {
                throw new IllegalArgumentException("- Please select a status");
            }

            
            var customer = selectedCollection.getCustomer();

            
            invoiceManager.createInvoice(customer, List.of(selectedCollection));

            invoiceCreated = true;

            DialogUtils.showSuccess("Invoice saved successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (final Exception e) {
            DialogUtils.showError("Validation error", e.getMessage(), AppContext.getOwner());
        }
    }

    /**
     * Cancels and closes the invoice creation modal.
     */
    @FXML
    private void handleAbortInvoiceCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}