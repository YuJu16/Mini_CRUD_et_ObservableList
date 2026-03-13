package com.example.tp3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.StringJoiner;

/**
 * Classe utilitaire pour exporter une collection de tickets au format CSV.
 */
public final class TicketExporter {

    private TicketExporter() {}

    /**
     * Exporte les tickets dans un fichier CSV.
     *
     * @param tickets  la collection de tickets à exporter
     * @param filePath chemin du fichier de destination (ex: "exports/tickets_export.csv")
     */
    public static void exportToCsv(Collection<SupportTicket> tickets, String filePath) {
        Path path = Path.of(filePath);

        // Crée le dossier parent si nécessaire
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            StringJoiner sb = new StringJoiner("\n");

            // En-tête CSV
            sb.add("id;title;customer_name;priority;created_at;description;urgent;status");

            // Lignes
            for (SupportTicket t : tickets) {
                String line = t.getId()          + ";"
                        + escape(t.getTitle())       + ";"
                        + escape(t.getCustomerName())+ ";"
                        + t.getPriority()            + ";"
                        + t.getCreatedAt()           + ";"
                        + escape(t.getDescription()) + ";"
                        + t.isUrgent()               + ";"
                        + t.getStatus();
                sb.add(line);
            }

            Files.writeString(path, sb.toString());
            System.out.println("Export CSV réussi : " + path.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV : " + e.getMessage());
        }
    }

    /** Échappe les guillemets dans les champs pour éviter les problèmes CSV. */
    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
