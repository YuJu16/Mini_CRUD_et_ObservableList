package com.example.tp3;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation SQLite du TicketDao.
 * Toutes les requêtes utilisent des PreparedStatement pour éviter les injections SQL.
 */
public class SQLiteTicketDao implements TicketDao {

    // ---- Méthode utilitaire interne ----

    /** Convertit une ligne du ResultSet en objet SupportTicket. */
    private SupportTicket mapRow(ResultSet rs) throws SQLException {
        return new SupportTicket(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("customer_name"),
                rs.getString("priority"),
                LocalDate.parse(rs.getString("created_at")),
                rs.getString("description"),
                rs.getInt("urgent") == 1,
                rs.getString("status")
        );
    }

    // ---- CRUD ----

    @Override
    public SupportTicket insert(SupportTicket ticket) {
        String sql = """
                INSERT INTO support_tickets
                    (title, customer_name, priority, created_at, description, urgent, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ticket.getTitle());
            pstmt.setString(2, ticket.getCustomerName());
            pstmt.setString(3, ticket.getPriority());
            pstmt.setString(4, ticket.getCreatedAt().toString());
            pstmt.setString(5, ticket.getDescription());
            pstmt.setInt   (6, ticket.isUrgent() ? 1 : 0);
            pstmt.setString(7, ticket.getStatus());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return ticket.withId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur insert : " + e.getMessage());
        }
        return ticket;
    }

    @Override
    public List<SupportTicket> findAll() {
        String sql = "SELECT * FROM support_tickets ORDER BY id DESC";
        List<SupportTicket> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll : " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<SupportTicket> findById(long id) {
        String sql = "SELECT * FROM support_tickets WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById : " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<SupportTicket> searchByTitleOrCustomer(String keyword) {
        String sql = """
                SELECT * FROM support_tickets
                WHERE LOWER(title) LIKE LOWER(?)
                   OR LOWER(customer_name) LIKE LOWER(?)
                ORDER BY id DESC
                """;
        String pattern = "%" + keyword + "%";
        List<SupportTicket> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(SupportTicket ticket) {
        String sql = """
                UPDATE support_tickets SET
                    title = ?,
                    customer_name = ?,
                    priority = ?,
                    created_at = ?,
                    description = ?,
                    urgent = ?,
                    status = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getTitle());
            pstmt.setString(2, ticket.getCustomerName());
            pstmt.setString(3, ticket.getPriority());
            pstmt.setString(4, ticket.getCreatedAt().toString());
            pstmt.setString(5, ticket.getDescription());
            pstmt.setInt   (6, ticket.isUrgent() ? 1 : 0);
            pstmt.setString(7, ticket.getStatus());
            pstmt.setLong  (8, ticket.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur update : " + e.getMessage());
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM support_tickets WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur deleteById : " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM support_tickets";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("Erreur deleteAll : " + e.getMessage());
        }
    }
}
