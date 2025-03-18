package it.unibo.wastemaster.core.models;

public class Administrator extends Employees {

    // Constructor class Administrator
    public Administrator(int id, String name, String address, String email, String phone, int employeeId) {
        super(id, name, address, email, phone, employeeId, "Administrator");
    }

    //method to manage users
    public void manageUsers() {
        System.out.println("Manage Users...");
    }

    //method to manage roles
    public void manageRoles() {
        System.out.println("Manage Roles...");
    }

    // Add getInfo method to return all the attributes of the class Administrator
    @Override
    public String getInfo() {
        return super.getInfo() + ", (Privileges Administrator)";
    }
    
}
