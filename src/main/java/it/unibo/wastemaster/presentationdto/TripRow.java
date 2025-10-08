package it.unibo.wastemaster.presentationdto;

import it.unibo.wastemaster.domain.model.Trip;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Presentation DTO class that represents a trip for the table view.
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
     * Constructs a TripRow from a given {@link Trip} object.
     * Formats trip fields into human-readable string representations.
     *
     * @param trip the {@link Trip} to be represented in this row
     */
    public TripRow(final Trip trip) {
        this.trip = trip;
        this.id = String.valueOf(trip.getTripId());
        this.postalCodes = trip.getPostalCode() != null ? trip.getPostalCode() : "";
        this.vehicle =
                trip.getAssignedVehicle() != null ? trip.getAssignedVehicle().getPlate()
                        : "";
        this.operators = trip.getOperators() != null
                ? trip.getOperators().stream()
                .map(op -> op.getName() + " " + op.getSurname())
                .collect(Collectors.joining(", "))
                : "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.departure =
                trip.getDepartureTime() != null ? trip.getDepartureTime().format(fmt)
                        : "";
        this.returnTime = trip.getExpectedReturnTime() != null
                ? trip.getExpectedReturnTime().format(fmt) : "";
        this.status = trip.getStatus() != null ? trip.getStatus().toString() : "";
    }

    /**
     * Returns the underlying {@link Trip} object.
     *
     * @return original trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Returns the trip ID as a string.
     *
     * @return trip ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the trip ID as an integer.
     *
     * @return trip ID
     */
    public int getIdAsInt() {
        return trip.getTripId();
    }

    /**
     * Returns the postal codes associated with the trip.
     *
     * @return postal codes, or empty string if null
     */
    public String getPostalCodes() {
        return postalCodes;
    }

    /**
     * Returns the license plate of the assigned vehicle.
     *
     * @return vehicle plate, or empty string if no vehicle assigned
     */
    public String getVehicle() {
        return vehicle;
    }

    /**
     * Returns the brand and model of the assigned vehicle.
     *
     * @return formatted vehicle brand and model, or empty string if not available
     */
    public String getVehicleModel() {
        var v = trip.getAssignedVehicle();
        if (v == null) {
            return "";
        }
        String brand = v.getBrand() != null ? v.getBrand() : "";
        String model = v.getModel() != null ? v.getModel() : "";
        return (brand + " " + model).trim();
    }

    /**
     * Returns the required number of operators for the assigned vehicle.
     *
     * @return number of required operators, or 0 if no vehicle assigned
     */
    public int getVehicleCapacity() {
        var v = trip.getAssignedVehicle();
        return v != null ? v.getRequiredOperators() : 0;
    }

    /**
     * Returns the operators assigned to the trip as a comma-separated string.
     *
     * @return formatted operator names, or empty string if none
     */
    public String getOperators() {
        return operators;
    }

    /**
     * Returns the departure time of the trip formatted as "yyyy-MM-dd HH:mm".
     *
     * @return formatted departure time, or empty string if null
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * Returns the expected return time of the trip formatted as "yyyy-MM-dd HH:mm".
     *
     * @return formatted return time, or empty string if null
     */
    public String getReturnTime() {
        return returnTime;
    }

    /**
     * Returns the status of the trip as a string.
     *
     * @return trip status, or empty string if null
     */
    public String getStatus() {
        return status;
    }
}
