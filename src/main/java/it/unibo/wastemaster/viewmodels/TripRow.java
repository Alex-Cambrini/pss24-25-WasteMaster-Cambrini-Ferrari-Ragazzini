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

    /**
     * Creates a {@code TripRow} wrapper from a {@link Trip} entity.
     *
     * @param trip the domain trip to wrap
     */
    public TripRow(final Trip trip) {
        this.trip = trip;
        this.id = String.valueOf(trip.getTripId());
        this.postalCodes = trip.getPostalCode() != null
                ? trip.getPostalCode()
                : "";
        this.vehicle = trip.getAssignedVehicle() != null
                ? trip.getAssignedVehicle().getPlate()
                : "";
        this.operators = trip.getOperators() != null
                ? trip.getOperators().stream()
                .map(op -> op.getName() + " " + op.getSurname())
                .collect(Collectors.joining(", "))
                : "";
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.departure = trip.getDepartureTime() != null
                ? trip.getDepartureTime().format(fmt)
                : "";
        this.returnTime = trip.getExpectedReturnTime() != null
                ? trip.getExpectedReturnTime().format(fmt)
                : "";
        this.status = trip.getStatus() != null
                ? trip.getStatus().toString()
                : "";
    }

    /**
     * Returns the wrapped {@link Trip} entity.
     *
     * @return the trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Returns the trip id as a string.
     *
     * @return the trip id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the trip id as an integer.
     *
     * @return the trip id as int
     */
    public int getIdAsInt() {
        return trip.getTripId();
    }

    /**
     * Returns the postal codes associated with the trip.
     *
     * @return the postal codes
     */
    public String getPostalCodes() {
        return postalCodes;
    }

    /**
     * Returns the vehicle plate (if assigned).
     *
     * @return the vehicle plate or empty string
     */
    public String getVehicle() {
        return vehicle;
    }

    /**
     * Returns the vehicle brand and model (trimmed), if present.
     *
     * @return "brand model" or empty string
     */
    public String getVehicleModel() {
        final var v = trip.getAssignedVehicle();
        if (v == null) {
            return "";
        }
        final String brand = v.getBrand() != null ? v.getBrand() : "";
        final String model = v.getModel() != null ? v.getModel() : "";
        return (brand + " " + model).trim();
    }

    /**
     * Returns the required number of operators for the assigned vehicle.
     *
     * @return required operators or 0 if no vehicle
     */
    public int getVehicleCapacity() {
        final var v = trip.getAssignedVehicle();
        return v != null ? v.getRequiredOperators() : 0;
    }

    /**
     * Returns the operators' full names, comma-separated.
     *
     * @return operators list or empty string
     */
    public String getOperators() {
        return operators;
    }

    /**
     * Returns the formatted departure timestamp.
     *
     * @return formatted departure or empty string
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * Returns the formatted expected return timestamp.
     *
     * @return formatted return time or empty string
     */
    public String getReturnTime() {
        return returnTime;
    }

    /**
     * Returns the trip status as a string.
     *
     * @return the status or empty string
     */
    public String getStatus() {
        return status;
    }
}
