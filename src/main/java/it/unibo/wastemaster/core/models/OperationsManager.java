package it.unibo.wastemaster.core.models;

public class OperationsManager extends Employees {
    
    // Constructor for OperationsManager
    public OperationsManager( int id, String name, String address, String email, String phone, int employeeId ) {
        super(id, name, address, email, phone, employeeId, "Operations Manager");
    }

    // Method to manage routes
    public void manageRoutes() {
        System.out.println("Managing Routes...");
    }

    // Method to assign operators to routes
    public void assignOperators() {
        System.out.println("Assigning operators to routes...");
    }


    // Add getInfo method to return all the attributes of the class OperationsManager
    @Override
    public String getInfo() {
        return super.getInfo() + ", (Privileges Operations Manager)";
    }
}
