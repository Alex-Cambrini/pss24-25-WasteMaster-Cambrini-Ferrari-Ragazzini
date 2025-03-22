package it.unibo.wastemaster.main;

import it.unibo.wastemaster.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Test di connessione al database
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                System.out.println("Connessione al database riuscita!");
            } else {
                System.out.println("Connessione al database fallita!");
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        }
    }
}
