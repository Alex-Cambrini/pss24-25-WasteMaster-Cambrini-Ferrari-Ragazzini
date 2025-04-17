package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Waste;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;


class RecurringScheduleManagerTest extends AbstractDatabaseTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ReservationExtraManager manager;
    private GenericDAO<ReservationExtra> reservationExtraDAO;
    private GenericDAO<Collection> collectionDAO;


    @Test
    void testCreateReservationExtra() {
        em.getTransaction().begin();

        Customer customer = new Customer();
        customer.setName("Mario Rossi");
        em.persist(customer);

        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PLASTIC;

        ReservationExtra reservation = manager.createReservationExtra(customer, date, wasteType);

        em.getTransaction().commit();

        assertNotNull(reservation.getReservationId());
        assertEquals(ReservationExtra.ReservationStatus.PENDING, reservation.getStatus());
        assertNotNull(reservation.getCollection());
        assertEquals(Collection.ScheduleType.EXTRA, reservation.getCollection().getScheduleType());
    }
}

