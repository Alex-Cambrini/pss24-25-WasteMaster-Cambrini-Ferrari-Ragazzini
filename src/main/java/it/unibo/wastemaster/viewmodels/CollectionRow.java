package it.unibo.wastemaster.viewmodels;

import java.time.LocalDate;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;

public class CollectionRow {
    private final int id;
    private final String wasteName;
    private final LocalDate collectionDate;
    private final String zone;
    private final CollectionStatus status;
    private final String customerName;

    public CollectionRow(Collection collection) {
        this.id = collection.getCollectionId();
        this.wasteName = collection.getWaste().getWasteName();
        this.collectionDate = collection.getCollectionDate();
        this.zone = collection.getCustomer().getLocation().getPostalCode();
        this.status = collection.getCollectionStatus();
        this.customerName = collection.getCustomer().getName() + " " + collection.getCustomer().getSurname();
    }

    public int getId() {
        return id;
    }

    public String getWasteName() {
        return wasteName;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public String getZone() {
        return zone;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

}
