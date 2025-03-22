package it.unibo.wastemaster.core.models;

public class Customer extends Person{
    private int customerId;

    public Customer(int personId, String name, String surname, Location address, String email, String phone, int customerId) {
        super(personId, name, surname, address, email, phone);
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getInfo() {
        return super.getInfo() + String.format(", CustomerId: %d", customerId);
    }


        //TEST
        // public static void main(String[] args) {
        //     Location location = new Location(1, "Via Roma", "10", "Milano", "Italy");
    
        //     Customer customer = new Customer(1, "Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", 1001);
    
        //     System.out.println("Customer Info: " + customer.getInfo());
    
        //     System.out.println("Customer ID: " + customer.getCustomerId());
        // }
   
}
