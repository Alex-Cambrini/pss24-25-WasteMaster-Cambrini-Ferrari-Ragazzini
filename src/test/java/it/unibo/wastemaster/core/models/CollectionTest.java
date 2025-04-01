package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

class CollectionTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("myJpaUnit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null) {
            entityManager.close();
        }
    }

    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PLASTIC;
        Collection.CollectionStatus status = Collection.CollectionStatus.PENDING;
        Collection.ScheduleType scheduleType = Collection.ScheduleType.SCHEDULED;
        Collection collection = new Collection(customer, date, wasteType, status, 3, 1001, scheduleType);
    
        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getDate());
        assertEquals(wasteType, collection.getWaste());
        assertEquals(status, collection.getCollectionStatus());
        assertEquals(3, collection.getCancelLimitDays());
        assertEquals(1001, collection.getScheduleId());
        assertEquals(scheduleType, collection.getScheduleType());
    }

    @Test
    void testToString() {
        Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PAPER;
        Collection.CollectionStatus status = Collection.CollectionStatus.COMPLETED;
        Collection.ScheduleType scheduleType = Collection.ScheduleType.SCHEDULED;
    
        Collection collection = new Collection(customer, date, wasteType, status, 10, 456, scheduleType);
    
        String expectedInfo = String.format(
            "Collection {ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %d, Schedule Type: %s}",
            collection.getCollectionId(), 
            customer.getName(), 
            date.toString(), 
            wasteType, 
            status, 
            collection.getCancelLimitDays(), 
            collection.getScheduleId(), 
            collection.getScheduleType()
        );
    
        assertEquals(expectedInfo, collection.toString());
    }
    

    @Test
    void testPersistence() {
        Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PLASTIC;
        Collection.CollectionStatus status = Collection.CollectionStatus.PENDING;
        Collection.ScheduleType scheduleType = Collection.ScheduleType.SCHEDULED;
    
        Collection collection = new Collection(customer, date, wasteType, status, 5, 123, scheduleType);
    
        entityManager.getTransaction().begin();
        entityManager.persist(collection);
        entityManager.getTransaction().commit();
    
        assertNotNull(collection.getCollectionId());
        assertTrue(collection.getCollectionId() > 0);
    
        Collection retrievedCollection = entityManager.find(Collection.class, collection.getCollectionId());
    
        assertNotNull(retrievedCollection);
        assertEquals(collection.getCollectionId(), retrievedCollection.getCollectionId());
        assertEquals(collection.getCustomer().getName(), retrievedCollection.getCustomer().getName());
        assertEquals(collection.getScheduleType(), retrievedCollection.getScheduleType());
    
        entityManager.getTransaction().begin();
        entityManager.remove(retrievedCollection);
        entityManager.getTransaction().commit();
    }
    
}
