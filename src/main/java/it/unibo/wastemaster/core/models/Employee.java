package it.unibo.wastemaster.core.models;

public class Employee extends Person {
    private int employeeId;
    private Role role;

    public enum Role {
        Administrator,
        OfficeWorker,
        Operator
    }

    public Employee(int id, String name, String surname, Location address, String email, String phone, int employeeId, Role role) {
        super(id, name, surname, address, email, phone);
        this.employeeId = employeeId;
        this.role = role;
    }

    public int getEmployeeId() {
        return employeeId;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String getInfo() {
        return super.getInfo() + String.format(", EmployeeId: %d, Role: %s", employeeId, role);
    }

    // public static void main(String[] args) {
    //     Location location = new Location(0, "Via Roma", "10", "Milano", "Italy");

    //     Employee employee = new Employee(1, "Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", 1001, Role.Operator);

    //     System.out.println("Info iniziali: " + employee.getInfo());

    //     System.out.println("Employee ID: " + employee.getEmployeeId());
    //     System.out.println("Employee Role: " + employee.getRole());

    //     employee.setRole(Role.Administrator);

    //     System.out.println("Info dopo la modifica: " + employee.getInfo());
    // }
}
