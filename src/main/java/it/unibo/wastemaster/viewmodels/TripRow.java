package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.domain.model.Trip;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * ViewModel class that represents a trip for the table view.
 */
public final class TripRow {

    private final Trip trip;
    private final String id;
    private final String postalCodes;
    private final String vehicle;
    private final String operators;
    private final String departure;
    private final String returnTime;
    private final String status;

    public TripRow(final Trip trip) {
        this.trip = trip;
        this.id = String.valueOf(trip.getTripId());
        this.postalCodes = trip.getPostalCode() != null ? trip.getPostalCode() : "";
        this.vehicle = trip.getAssignedVehicle() != null ? trip.getAssignedVehicle().getPlate() : "";
        this.operators = trip.getOperators() != null
                ? trip.getOperators().stream()
                        .map(op -> op.getName() + " " + op.getSurname())
                        .collect(Collectors.joining(", "))
                : "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.departure = trip.getDepartureTime() != null ? trip.getDepartureTime().format(fmt) : "";
        this.returnTime = trip.getExpectedReturnTime() != null ? trip.getExpectedReturnTime().format(fmt) : "";
        this.status = trip.getStatus() != null ? trip.getStatus().toString() : "";
    }

    public Trip getTrip() {
        return trip;
    }

    public String getId() {
        return id;
    }

    public int getIdAsInt() {
        return trip.getTripId();
    }

    public String getPostalCodes() {
        return postalCodes;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getVehicleModel() {
        var v = trip.getAssignedVehicle();
        return v != null ? v.getModel() : "";
    }

    public int getVehicleCapacity() {
        var v = trip.getAssignedVehicle();
        return v != null ? v.getRequiredOperators() : 0;
    }

    public String getOperators() {
        return operators;
    }

    public String getDeparture() {
        return departure;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getStatus() {
        return status;
    }
}
