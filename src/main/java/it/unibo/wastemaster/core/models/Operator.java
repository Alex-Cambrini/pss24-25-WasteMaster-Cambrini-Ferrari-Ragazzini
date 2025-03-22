package it.unibo.wastemaster.core.models;
public class Operator extends Employee {

    public Operator(int id, String name, String surname, Location address, String email, String phone, int employeeId) {
        super(id, name, surname, address, email, phone, employeeId, Role.Operator);
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    // //TEST
    // public static void main(String[] args) {
    //     Location location = new Location(1, "Via Torino", "30", "Milano", "Italy");
    //     Operator operator = new Operator(2, "Giovanni", "Bianchi", location, "giovanni.bianchi@example.com", "9876543210", 2001);    
    //     System.out.println("Info iniziali: " + operator.getInfo());   
    //     operator.setPhone("0123456789");
    //     System.out.println("Info dopo la modifica del telefono: " + operator.getInfo());
    // }
}
