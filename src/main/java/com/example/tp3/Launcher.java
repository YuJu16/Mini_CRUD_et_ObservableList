package com.example.tp3;

/**
 * Astuce classique pour lancer une application JavaFX depuis l'éditeur (VSCode, IntelliJ...)
 * sans se prendre l'erreur "JavaFX runtime components are missing".
 *
 * Cette classe NE DOIT PAS hériter de `Application`.
 */
public class Launcher {
    public static void main(String[] args) {
        // On redirige vers le vrai point d'entrée de notre application
        TicketPersistenceApp.main(args);
    }
}
