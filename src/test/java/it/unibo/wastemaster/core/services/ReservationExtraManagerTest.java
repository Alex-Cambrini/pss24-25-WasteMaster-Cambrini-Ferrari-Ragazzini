package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.ReservationExtra;
import it.unibo.wastemaster.core.models.Waste;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;


class ReservationExtraManagerTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ReservationExtraManager manager;
    private GenericDAO<ReservationExtra> reservationExtraDAO;
    private GenericDAO<Collection> collectionDAO;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        em = emf.createEntityManager();
        manager = new ReservationExtraManager(em);
        reservationExtraDAO = new GenericDAO<>(em, ReservationExtra.class);
        collectionDAO = new GenericDAO<>(em, Collection.class);
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();

        Collection collection = em.find(Collection.class, 1);
        if (collection != null) {
            collectionDAO.delete(collection);
        }

        ReservationExtra reservation = em.find(ReservationExtra.class, 1);
        if (reservation != null) {
            reservationExtraDAO.delete(reservation);
        }

        em.getTransaction().commit();

        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }

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

