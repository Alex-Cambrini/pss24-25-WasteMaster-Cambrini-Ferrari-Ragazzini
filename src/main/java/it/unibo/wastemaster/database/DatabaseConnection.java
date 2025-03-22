package it.unibo.wastemaster.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Dati di connessione al database (modifica con le tue credenziali)
    private static final String URL = "jdbc:mysql://localhost:3306/wastemaster_db";
    private static final String USER = "wastemaster";
    private static final String PASSWORD = "wastemaster";

    // Metodo per ottenere la connessione al database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
