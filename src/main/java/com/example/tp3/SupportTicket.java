package com.example.tp3;

import java.time.LocalDate;

/**
 * Modèle métier immuable représentant un ticket support.
 * Sert de pont entre la base SQLite et l'interface JavaFX.
 */
public class SupportTicket {

    private final long id;
    private final String title;
    private final String customerName;
    private final String priority;
    private final LocalDate createdAt;
    private final String description;
    private final boolean urgent;
    private final String status;

    /** Constructeur complet (utilisé lors de la lecture depuis la BDD). */
    public SupportTicket(long id, String title, String customerName,
                         String priority, LocalDate createdAt,
                         String description, boolean urgent, String status) {
        this.id           = id;
        this.title        = title;
        this.customerName = customerName;
        this.priority     = priority;
        this.createdAt    = createdAt;
        this.description  = description;
        this.urgent       = urgent;
        this.status       = status;
    }

    /** Constructeur sans id (avant insertion en base). */
    public SupportTicket(String title, String customerName,
                         String priority, LocalDate createdAt,
                         String description, boolean urgent, String status) {
        this(0L, title, customerName, priority, createdAt, description, urgent, status);
    }

    // ---- Getters ----

    public long      getId()          { return id; }
    public String    getTitle()       { return title; }
    public String    getCustomerName(){ return customerName; }
    public String    getPriority()    { return priority; }
    public LocalDate getCreatedAt()   { return createdAt; }
    public String    getDescription() { return description; }
    public boolean   isUrgent()       { return urgent; }
    public String    getStatus()      { return status; }

    /**
     * Retourne un nouveau ticket identique mais avec l'id généré par la BDD.
     * Utile après un INSERT pour conserver l'identifiant auto-incrémenté.
     */
    public SupportTicket withId(long newId) {
        return new SupportTicket(newId, title, customerName, priority,
                createdAt, description, urgent, status);
    }

    @Override
    public String toString() {
        return "SupportTicket{id=" + id
                + ", title='" + title + '\''
                + ", customer='" + customerName + '\''
                + ", priority='" + priority + '\''
                + ", status='" + status + '\''
                + ", urgent=" + urgent + '}';
    }
}
