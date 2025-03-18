package it.unibo.wastemaster.main;

import it.unibo.wastemaster.core.models.Person;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Person testPerson = new Person(1, "Mario", "Via roma 27", "mario@example.com", "1234567890") {};
        System.out.println(testPerson.getInfo());
    }
}
