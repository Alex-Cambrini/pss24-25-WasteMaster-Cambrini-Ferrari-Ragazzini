package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * ViewModel class representing a row in the collection table. It wraps a Collection
 * entity for displaying in the UI.
 */
public final class CollectionRow {

    private final int id;
    private final String wasteName;
    private final LocalDate collectionDate;
    private final String zone;
    private final CollectionStatus status;
    private final String customerName;
    private final Collection collection;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    /**
     * Constructs a CollectionRow based on a Collection entity.
     *
     * @param collection the collection entity to represent
     */
    public CollectionRow(final Collection collection) {
        this.collection = collection;
        this.id = collection.getCollectionId();
        this.wasteName = collection.getWaste().getWasteName();
        this.collectionDate = collection.getCollectionDate();
        this.zone = collection.getCustomer().getLocation().getPostalCode();
        this.status = collection.getCollectionStatus();
        this.customerName = collection.getCustomer().getName() + " "
                + collection.getCustomer().getSurname();
    }

    /**
     * @return the JavaFX boolean property backing the selection state
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * @return whether the row is selected
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Sets the selection state.
     *
     * @param value the new selection value
     */
    public void setSelected(final boolean value) {
        selected.set(value);
    }

    /**
     * Returns the ID of the collection.
     *
     * @return the collection ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the waste associated with this collection.
     *
     * @return the waste name
     */
    public String getWasteName() {
        return wasteName;
    }

    /**
     * Returns the scheduled date of collection.
     *
     * @return the collection date
     */
    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    /**
     * Returns the postal code of the collection zone.
     *
     * @return the postal code zone
     */
    public String getZone() {
        return zone;
    }

    /**
     * Returns the current status of the collection.
     *
     * @return the collection status
     */
    public CollectionStatus getStatus() {
        return status;
    }

    /**
     * Returns the full name of the customer associated with the collection.
     *
     * @return the customer's full name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the underlying collection domain entity.
     *
     * @return the {@link Collection} entity
     */
    public Collection getCollection() {
        return collection;
    }
}
