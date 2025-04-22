package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.utils.DateUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CollectionTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste.WasteType wasteType;
    private Collection.CollectionStatus status;
    private Collection.ScheduleCategory scheduleCategory;
    private OneTimeSchedule schedule;
    private Collection collection;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");

        date = DateUtils.getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;
        status = Collection.CollectionStatus.COMPLETED;
        scheduleCategory = Collection.ScheduleCategory.ONE_TIME;

        schedule = new OneTimeSchedule(customer, wasteType, ScheduleStatus.SCHEDULED, date);
        collection = new Collection(customer, date, wasteType, status, schedule, scheduleCategory);
    }

    @Test
    void testCollectionGettersAndSetters() {
        collection = new Collection();
        collection.setCustomer(customer);
        collection.setDate(date);
        collection.setWaste(wasteType);
        collection.setCollectionStatus(status);
        collection.setSchedule(schedule);
        collection.setScheduleCategory(scheduleCategory);
        collection.setCancelLimitDays(Collection.CANCEL_LIMIT_DAYS);
        collection.setExtra(true);

        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getDate());
        assertEquals(wasteType, collection.getWaste());
        assertEquals(status, collection.getCollectionStatus());
        assertEquals(scheduleCategory, collection.getScheduleCategory());
        assertEquals(Collection.CANCEL_LIMIT_DAYS, collection.getCancelLimitDays());
        assertTrue(collection.isExtra());

        collection.setExtra(false);
        assertFalse(collection.isExtra());

        collection.setCollectionStatus(Collection.CollectionStatus.IN_PROGRESS);
        assertEquals(Collection.CollectionStatus.IN_PROGRESS, collection.getCollectionStatus());

        collection.setCancelLimitDays(5);
        assertEquals(5, collection.getCancelLimitDays());
    }

    @Test
    void testToString() {
        String toStringOutput = collection.toString();
        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("Collection"));
        assertTrue(toStringOutput.contains(customer.getName()));
        assertTrue(toStringOutput.contains(wasteType.name()));
        assertTrue(toStringOutput.contains(status.name()));
        assertTrue(toStringOutput.contains(scheduleCategory.name()));
        assertTrue(toStringOutput.contains(String.valueOf(Collection.CANCEL_LIMIT_DAYS)));
    }

    @Test
    void testPersistence() {
        em.getTransaction().begin();
        em.persist(location);
        em.persist(customer);
        em.persist(schedule);
        em.persist(collection);
        em.getTransaction().commit();

        Collection found = em.find(Collection.class, collection.getCollectionId());
        assertNotNull(found);
        assertEquals(customer.getName(), found.getCustomer().getName());

        em.getTransaction().begin();
        em.remove(collection);
        em.getTransaction().commit();

        Collection deleted = em.find(Collection.class, collection.getCollectionId());
        assertNull(deleted);
    }

    @Test
    void testCollectionValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Collection invalid = new Collection();
        invalid.setDate(date.minusDays(1));
        invalid.setCustomer(null);
        invalid.setCancelLimitDays(-1);
        invalid.setSchedule(null);

        Set<ConstraintViolation<Collection>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("The date must be today or in the future")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("The customer cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Cancellation days must be >= 0")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Schedule cannot be null")));
    }
}
