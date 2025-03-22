package it.unibo.wastemaster.core.models;

public class Employee extends Person {
    private int employeeId;
    private Role role;

    public enum Role {
        Administrator,
        OfficeWorker,
        Operator
    }

    public Employee(int id, String name, Location address, String email, String phone, int employeeId, Role role) {
        super(id, name, address, email, phone);
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

    public static void main(String[] args) {
        // Creazione di un oggetto Location per il test
        Location location = new Location(0, "Via Roma", "10", "Milano", "Italy");

        // Creazione di un oggetto Employee
        Employee employee = new Employee(1, "Mario Rossi", location, "mario.rossi@example.com", "1234567890", 1001, Role.Operator);

        // Stampa delle informazioni iniziali dell'employee
        System.out.println("Info iniziali: " + employee.getInfo());

        // Test del getter per il ruolo e ID dell'employee
        System.out.println("Employee ID: " + employee.getEmployeeId());
        System.out.println("Employee Role: " + employee.getRole());

        // Modifica del ruolo con il setter
        employee.setRole(Role.Administrator);

        // Stampa delle informazioni dopo la modifica del ruolo
        System.out.println("Info dopo la modifica: " + employee.getInfo());
    }
}
