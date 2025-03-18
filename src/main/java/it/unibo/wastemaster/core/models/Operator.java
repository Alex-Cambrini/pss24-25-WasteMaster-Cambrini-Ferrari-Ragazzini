package it.unibo.wastemaster.core.models;

public class Operator extends Employees {
    // Constructor for Operator
    public Operator(int id, String name, String address, String email, String phone, int employeeId) {
        super(id, name, address, email, phone, employeeId, "Operator");
    }

    // Add getInfo method to return all the attributes of the class Operator
    @Override
    public String getInfo() {
        return super.getInfo() + ", (Privileges Operator)";
    }
}
