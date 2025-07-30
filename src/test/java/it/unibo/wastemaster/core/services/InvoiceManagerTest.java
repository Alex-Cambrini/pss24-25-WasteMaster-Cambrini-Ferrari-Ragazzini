package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceManagerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Waste waste;

    
    private static final int FUTURE_TEST_YEAR = LocalDate.now().getYear() + 1; 

    
    private static final LocalDate FUTURE_DATE_FOR_SCHEDULE_VALIDATION = LocalDate.of(2030, 1, 1);

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        customer = new Customer("Mario", "Rossi",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "mario.rossi@example.com", "1234567890");
        waste = new Waste("Organico", true, false);

        getCustomerDAO().insert(customer);
        getWasteDAO().insert(waste);
    }

    private Collection insertCompletedCollection(LocalDate collectionScheduledDate) {

        OneTimeSchedule schedule = new OneTimeSchedule(customer, waste, FUTURE_DATE_FOR_SCHEDULE_VALIDATION);
        getOneTimeScheduleDAO().insert(schedule);

        Collection collection = new Collection(schedule);
        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
        
        
        collection.setCollectionDate(collectionScheduledDate); 

        getCollectionDAO().insert(collection);

        return collection;
    }

    

   
}