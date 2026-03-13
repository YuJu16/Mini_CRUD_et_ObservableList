package com.example.tp3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centralise l'ouverture de la connexion JDBC et l'initialisation du schéma SQLite.
 */
public final class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:tickets.db";

    // Classe utilitaire : pas d'instanciation
    private DatabaseManager() {}

    /** Ouvre et retourne une nouvelle connexion à la base. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Crée la table support_tickets si elle n'existe pas encore.
     * À appeler au démarrage de l'application.
     */
    public static void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS support_tickets (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    title         TEXT    NOT NULL,
                    customer_name TEXT    NOT NULL,
                    priority      TEXT    NOT NULL,
                    created_at    TEXT    NOT NULL,
                    description   TEXT    NOT NULL,
                    urgent        INTEGER NOT NULL,
                    status        TEXT    NOT NULL
                );
                """;

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
        }
    }
}
