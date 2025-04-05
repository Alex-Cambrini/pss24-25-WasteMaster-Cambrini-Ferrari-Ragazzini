package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Date;

public class ReservationExtraManager {

    private final GenericDAO<ReservationExtra> reservationExtraDAO;
    private final GenericDAO<Collection> collectionDAO;

    public ReservationExtraManager(EntityManager entityManager) {
        this.reservationExtraDAO = new GenericDAO<>(entityManager, ReservationExtra.class);
        this.collectionDAO = new GenericDAO<>(entityManager, Collection.class);
    }

    @Transactional
    public ReservationExtra createReservationExtra(Customer customer, Date reservationDate, Waste.WasteType wasteType) {
        ReservationExtra reservation = new ReservationExtra(customer, reservationDate, ReservationExtra.ReservationStatus.PENDING, wasteType);
        reservationExtraDAO.insert(reservation);

        Collection collection = new Collection(
            customer,
            reservationDate,
            wasteType,
            Collection.CollectionStatus.PENDING,
            7,
            reservation.getReservationId(),
            Collection.ScheduleType.EXTRA
        );
        collection.setExtra(true);

        collectionDAO.insert(collection);

        reservation.setCollection(collection);
        reservationExtraDAO.update(reservation);

        return reservation;
    }
}
