package it.unibo.wastemaster.main;

import it.unibo.wastemaster.core.models.Person;
import it.unibo.wastemaster.core.models.Client;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Person testPerson = new Person(1, "Mario", "Via roma 27", "mario@example.com", "1234567890") {};
        System.out.println("Person: " + testPerson.getInfo());
        Client testClient = new Client(2, "Luigi", "Via milano 12", "luigi@example.com", "0987654321", "17") {};
        System.out.println("Client: " + testClient.getInfo());
    }
}
