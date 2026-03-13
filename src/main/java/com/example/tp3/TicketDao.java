package com.example.tp3;

import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données pour les tickets support.
 * Toute implémentation concrète (SQLite, mémoire, etc.) doit respecter cette interface.
 */
public interface TicketDao {

    /** Insère un ticket en base et retourne la version avec l'id généré. */
    SupportTicket insert(SupportTicket ticket);

    /** Retourne tous les tickets, triés du plus récent au plus ancien. */
    List<SupportTicket> findAll();

    /** Recherche un ticket par son identifiant. */
    Optional<SupportTicket> findById(long id);

    /**
     * Recherche les tickets dont le titre ou le nom du client
     * contient le mot-clé (insensible à la casse).
     */
    List<SupportTicket> searchByTitleOrCustomer(String keyword);

    /** Met à jour toutes les colonnes du ticket (sauf l'id). */
    void update(SupportTicket ticket);

    /** Supprime le ticket correspondant à l'id donné. */
    void deleteById(long id);

    /** Supprime tous les tickets de la table. */
    void deleteAll();
}
