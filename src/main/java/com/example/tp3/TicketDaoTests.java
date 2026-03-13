package com.example.tp3;

import java.time.LocalDate;

/**
 * Tests simples du DAO SQLiteTicketDao.
 * Lancer avec : java -ea -cp target/classes com.example.tp3.TicketDaoTests
 */
public class TicketDaoTests {

    public static void main(String[] args) {
        System.out.println("=== TicketDaoTests ===");

        // 1 – Initialisation de la base
        DatabaseManager.initializeDatabase();

        TicketDao dao = new SQLiteTicketDao();

        // 2 – Partir proprement
        dao.deleteAll();
        assert dao.findAll().isEmpty() : "La table devrait être vide après deleteAll()";

        // 3 – Insertion
        SupportTicket newTicket = new SupportTicket(
                "Erreur login", "Client Alpha", "Haute",
                LocalDate.now(), "Impossible de se connecter.", false, "Nouveau"
        );
        SupportTicket saved = dao.insert(newTicket);

        // 4 – Vérifier que l'id est généré
        assert saved.getId() > 0 : "L'id généré devrait être positif";
        System.out.println("✅ insert() OK – id=" + saved.getId());

        // 5 – findAll()
        assert !dao.findAll().isEmpty() : "findAll() devrait retourner au moins un ticket";
        System.out.println("✅ findAll() OK – " + dao.findAll().size() + " ticket(s)");

        // 6 – findById()
        var opt = dao.findById(saved.getId());
        assert opt.isPresent() : "findById() devrait trouver le ticket";
        assert opt.get().getTitle().equals("Erreur login") : "Le titre devrait correspondre";
        System.out.println("✅ findById() OK");

        // 7 – Recherche
        var results = dao.searchByTitleOrCustomer("alpha");
        assert !results.isEmpty() : "searchByTitleOrCustomer() devrait retourner un résultat";
        System.out.println("✅ searchByTitleOrCustomer() OK");

        // 8 – update()
        SupportTicket updated = new SupportTicket(
                saved.getId(), "Erreur login (mise à jour)", "Client Alpha",
                "Critique", LocalDate.now(), "Problème identifié.", true, "En cours"
        );
        dao.update(updated);
        var afterUpdate = dao.findById(saved.getId());
        assert afterUpdate.isPresent() : "Le ticket devrait toujours exister après update";
        assert afterUpdate.get().getPriority().equals("Critique") : "La priorité devrait être mise à jour";
        System.out.println("✅ update() OK");

        // 9 – deleteById()
        dao.deleteById(saved.getId());
        assert dao.findAll().isEmpty() : "La table devrait être vide après deleteById()";
        System.out.println("✅ deleteById() OK");

        System.out.println("\n🎉 Tous les tests TicketDao passent avec succès !");
    }
}
