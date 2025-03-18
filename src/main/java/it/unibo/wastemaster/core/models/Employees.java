package it.unibo.wastemaster.core.models;

public class Employees extends Person {
    private int employeeId;
    private String role;

    // Constructor class Employees
    public Employees(int id, String name, String address, String email, String phone, int employeeId, String role) {
        super(id, name, address, email, phone);
        this.employeeId = employeeId;
        this.role = role;
    }

    // Getter method for obtaining the attributes of the class Employees
    public int getEmployeeId() {
        return employeeId;
    }
    public String getRole() {
        return role;
    }

    // Setter method for setting the attributes of the class Employees
    public void setRole(String role) {
        this.role = role;
    }

    // Add getInfo method to return all the attributes of the class Employees
    @Override
    public String getInfo() {
        return super.getInfo() + String.format(", EmployeeId: %d, Role: %s", employeeId, role);
    }
}
