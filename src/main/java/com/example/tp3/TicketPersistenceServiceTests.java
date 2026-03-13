package com.example.tp3;

import java.time.LocalDate;

/**
 * Tests simples du TicketPersistenceService.
 * Lancer avec : java -ea -cp target/classes com.example.tp3.TicketPersistenceServiceTests
 */
public class TicketPersistenceServiceTests {

    public static void main(String[] args) {
        System.out.println("=== TicketPersistenceServiceTests ===");

        // 1 – Initialisation de la base
        DatabaseManager.initializeDatabase();

        TicketPersistenceService service = new TicketPersistenceService();

        // 2 – Vider la table
        service.deleteAllTickets();
        assert service.getTickets().isEmpty() : "deleteAllTickets() devrait vider l'ObservableList";
        System.out.println("✅ deleteAllTickets() OK");

        // 3 – Créer deux tickets
        SupportTicket t1 = new SupportTicket(
                "VPN inaccessible", "Société Delta", "Critique",
                LocalDate.now(), "VPN de prod hors ligne.", true, "Nouveau"
        );
        SupportTicket t2 = new SupportTicket(
                "Imprimante HS", "Bureau Paris", "Faible",
                LocalDate.now(), "Imprimante muette.", false, "En cours"
        );
        service.createTicket(t1);
        service.createTicket(t2);

        // 4 – Vérifier l'ObservableList
        assert service.getTickets().size() == 2 : "L'ObservableList devrait contenir 2 tickets";
        System.out.println("✅ createTicket() × 2 OK – liste = " + service.getTickets().size());

        // 5 – Recherche
        var results = service.search("VPN");
        assert !results.isEmpty() : "search('VPN') devrait retourner au moins un résultat";
        assert results.get(0).getTitle().contains("VPN") : "Le résultat devrait contenir 'VPN'";
        System.out.println("✅ search() OK – " + results.size() + " résultat(s)");

        // 6 – Recherche sans résultat
        var empty = service.search("introuvable_xyz");
        assert empty.isEmpty() : "Une recherche sans correspondance devrait retourner une liste vide";
        System.out.println("✅ search() vide OK");

        // 7 – deleteAllTickets()
        service.deleteAllTickets();
        assert service.getTickets().isEmpty() : "L'ObservableList devrait être vide après deleteAllTickets()";
        System.out.println("✅ deleteAllTickets() final OK");

        System.out.println("\n🎉 Tous les tests TicketPersistenceService passent avec succès !");
    }
}
