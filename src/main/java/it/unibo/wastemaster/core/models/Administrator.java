package it.unibo.wastemaster.core.models;

public class Administrator extends Employee {

    public Administrator(int id, String name, String surname, Location address, String email, String phone, int employeeId) {
        super(id, name, surname, address, email, phone, employeeId, Role.Administrator);
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    // public static void main(String[] args) {
    //     // Crea un oggetto Location per testare l'indirizzo
    //     Location location = new Location(1, "Via Roma", "15", "Milano", "Italy");

    //     Administrator admin = new Administrator(1, "Mario", "Rossi", location, "mario.rossi@example.com", "1234567890", 1001);

    //     System.out.println(admin.getInfo());
    //     admin.setPhone("55555");
        
    //     System.out.println("ID: " + admin.getId());
    //     System.out.println("Name: " + admin.getName());
    //     System.out.println("Surname: " + admin.getSurname());
    //     System.out.println("Email: " + admin.getEmail());
    //     System.out.println("Phone: " + admin.getPhone());
    //     System.out.println("Employee ID: " + admin.getEmployeeId());
    //     System.out.println("Role: " + admin.getRole());
    // }
    
}
