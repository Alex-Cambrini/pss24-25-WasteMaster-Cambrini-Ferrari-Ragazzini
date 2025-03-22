package it.unibo.wastemaster.core.models;

public class OfficeWorker extends Employee {
    
    public OfficeWorker(int id, String name, String surname, Location address, String email, String phone, int employeeId ) {
        super(id, name, surname, address, email, phone, employeeId, Role.OfficeWorker);
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    //TEST
    // public static void main(String[] args) {
    //     Location location = new Location(2, "Via Milano", "45", "Torino", "Italy");
    //     OfficeWorker officeWorker = new OfficeWorker(3, "Alessandra", "Rossi", location, "alessandra.rossi@example.com", "1122334455", 3001);

    //     System.out.println("Info iniziali: " + officeWorker.getInfo());
    //     officeWorker.setPhone("6677889900");
    //     System.out.println("Info dopo la modifica del telefono: " + officeWorker.getInfo());
    // }
}
