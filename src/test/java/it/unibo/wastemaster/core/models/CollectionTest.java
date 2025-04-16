package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

class CollectionTest extends AbstractDatabaseTest {


    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PLASTIC;
        Collection.CollectionStatus status = Collection.CollectionStatus.PENDING;
        Collection.ScheduleCategory scheduleCategory = Collection.ScheduleCategory.ONE_TIME;
        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, ScheduleStatus.SCHEDULED, new java.sql.Date(System.currentTimeMillis()));
		Collection collection = new Collection(customer, date, wasteType, status, schedule, scheduleCategory);    
        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getDate());
        assertEquals(wasteType, collection.getWaste());
        assertEquals(status, collection.getCollectionStatus());
        assertEquals(schedule, collection.getScheduleId());
        assertEquals(scheduleCategory, collection.getScheduleCategory());
    }

    @Test
    void testToString() {
        Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PAPER;
        Collection.CollectionStatus status = Collection.CollectionStatus.COMPLETED;
        Collection.ScheduleCategory scheduleCategory = Collection.ScheduleCategory.ONE_TIME;
    
        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, ScheduleStatus.SCHEDULED, new java.sql.Date(System.currentTimeMillis()));
		Collection collection = new Collection(customer, date, wasteType, status, schedule, scheduleCategory);    

    
        String expectedInfo = String.format(
            "Collection {ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %s, Schedule Category: %s}",
            collection.getCollectionId(), 
            customer.getName(), 
            date.toString(), 
            wasteType, 
            status, 
            Collection.CANCEL_LIMIT_DAYS,
            schedule, 
            scheduleCategory
        );

    
        assertEquals(expectedInfo, collection.toString());
    }
    

	@Test
	void testPersistence() {
		Customer customer = new Customer("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890");
		Date date = new Date();
		Waste.WasteType wasteType = Waste.WasteType.PLASTIC;
		Collection.CollectionStatus status = Collection.CollectionStatus.PENDING;
		Collection.ScheduleCategory scheduleCategory = Collection.ScheduleCategory.ONE_TIME;

		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();

		OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, ScheduleStatus.SCHEDULED, new java.sql.Date(System.currentTimeMillis()));
        Collection collection = new Collection(customer, date, wasteType, status, schedule, scheduleCategory);


		entityManager.getTransaction().begin();
		entityManager.persist(collection);
		entityManager.getTransaction().commit();

		assertNotNull(collection.getCollectionId());
		assertTrue(collection.getCollectionId() > 0);

		Collection retrievedCollection = entityManager.find(Collection.class, collection.getCollectionId());

		assertNotNull(retrievedCollection);
		assertEquals(collection.getCollectionId(), retrievedCollection.getCollectionId());
		assertEquals(collection.getCustomer().getName(), retrievedCollection.getCustomer().getName());
		assertEquals(collection.getScheduleCategory(), retrievedCollection.getScheduleCategory());
		entityManager.getTransaction().begin();
		entityManager.remove(retrievedCollection);
		entityManager.remove(customer);
		entityManager.getTransaction().commit();
	}
}