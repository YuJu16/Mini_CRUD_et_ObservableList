package com.example.tp3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Service applicatif : fait le lien entre le DAO SQLite et l'interface JavaFX.
 * Expose une ObservableList que la TableView peut écouter directement.
 */
public class TicketPersistenceService {

    private final TicketDao dao     = new SQLiteTicketDao();
    private final ObservableList<SupportTicket> tickets =
            FXCollections.observableArrayList();

    /** Crée le service et charge immédiatement les données depuis la base. */
    public TicketPersistenceService() {
        refresh();
    }

    /** Retourne la liste observable liée à la TableView. */
    public ObservableList<SupportTicket> getTickets() {
        return tickets;
    }

    /** Recharge tous les tickets depuis la base et met à jour la liste observable. */
    public void refresh() {
        tickets.setAll(dao.findAll());
    }

    /**
     * Insère un nouveau ticket en base, recharge la liste et
     * retourne le ticket enrichi de son id auto-généré.
     */
    public SupportTicket createTicket(SupportTicket ticket) {
        SupportTicket saved = dao.insert(ticket);
        refresh();
        return saved;
    }

    /** Met à jour un ticket existant en base, puis recharge la liste. */
    public void updateTicket(SupportTicket ticket) {
        dao.update(ticket);
        refresh();
    }

    /** Supprime le ticket identifié par l'id, puis recharge la liste. */
    public void deleteTicket(long id) {
        dao.deleteById(id);
        refresh();
    }

    /** Supprime tous les tickets, puis vide la liste observable. */
    public void deleteAllTickets() {
        dao.deleteAll();
        tickets.clear();
    }

    /**
     * Recherche les tickets correspondant au mot-clé dans le titre ou le nom du client.
     * Ne modifie pas la liste observable principale.
     */
    public List<SupportTicket> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return dao.findAll();
        }
        return dao.searchByTitleOrCustomer(keyword);
    }
}
