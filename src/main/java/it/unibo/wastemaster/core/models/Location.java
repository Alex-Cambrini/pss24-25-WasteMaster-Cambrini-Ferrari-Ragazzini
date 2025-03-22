package it.unibo.wastemaster.core.models;

public class Location {
    private int id;
    private String street;
    private String civicNumber;
    private String city;
    private String postalCode;

    public Location(int id, String street, String civicNumber, String city, String postalCode) {
        this.id = id;
        this.street = street;
        this.civicNumber = civicNumber;
        this.city = city;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCivicNumber() {
        return civicNumber;
    }

    public void setCivicNumber(String civicNumber) {
        this.civicNumber = civicNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return street + ", " + civicNumber + ", " + city + " " + postalCode;
    }

    // TEST
    // public static void main(String[] args) {
    //     Location location = new Location(0, "Via Roma", "10", "Milano", "20100");

    //     System.out.println("ID Location: " + location.getId());
    //     System.out.println("Street: " + location.getStreet());
    //     System.out.println("Civic Number: " + location.getCivicNumber());
    //     System.out.println("City: " + location.getCity());
    //     System.out.println("Postal Code: " + location.getPostalCode());

    //     location.setStreet("Via Milano");
    //     location.setCivicNumber("15");
    //     location.setCity("Bologna");
    //     location.setPostalCode("40100");

    //     System.out.println("\nDopo modifica:");
    //     System.out.println("Street: " + location.getStreet());
    //     System.out.println("Civic Number: " + location.getCivicNumber());
    //     System.out.println("City: " + location.getCity());
    //     System.out.println("Postal Code: " + location.getPostalCode());
    // }
}
