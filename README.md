# Mini CRUD et ObservableList (TP 03 - JavaFX & SQLite)

Bienvenue dans le gestionnaire de Tickets Support. Ce projet est une application desktop permettant d'effectuer des opérations CRUD (Create, Read, Update, Delete) complètes sur des tickets d'assistance, avec une vraie persistance des données via une base SQLite locale.

---

## 🚀 Comment lancer le projet ?

Ce projet utilise **JavaFX** (Version 21) et **Maven**.  
Afin d'éviter l'erreur classique `JavaFX runtime components are missing` dans la plupart des éditeurs (comme VSCode ou IntelliJ), l'application possède une classe de lancement spécifique : **le Launcher**.

**La méthode la plus simple pour démarrer l'application :**
1. Ouvre le dossier du projet dans ton éditeur.
2. Va dans l'explorateur de fichiers : `src` > `main` > `java` > `com` > `example` > `tp3`.
3. Ouvre le fichier **`Launcher.java`**.
4. Au-dessus de la ligne `public static void main(String[] args)`, clique sur le bouton **"Run"** (ou "Exécuter").
5. La fenêtre de l'application s'ouvrira (le fichier de base de données `tickets.db` se créera tout seul s'il n'existe pas encore).

---

## 📂 Arborescence du projet

Voici la structure de l'application, respectant le modèle MVC et le DAO pattern :

```
Mini_CRUD_et_ObservableList/
├── pom.xml                               # Configuration Maven (JavaFX + SQLite)
├── tickets.db                            # Base de données SQLite (générée)
├── exports/
│   └── tickets_export.csv                # Dossier et export CSV générés
└── src/
    └── main/
        ├── java/com/example/tp3/
        │   ├── Launcher.java                 # Contournement JavaFX VSCode
        │   ├── TicketPersistenceApp.java     # Fichier VUE JavaFX (Root Application)
        │   ├── TicketPersistenceService.java # Couche SERVICE (liaison UI ↔ DAO)
        │   ├── TicketDao.java                # Interface de la couche DAO
        │   ├── SQLiteTicketDao.java          # Implémentation DAO en SQLite (Requêtes CRUD)
        │   ├── DatabaseManager.java          # Gestionnaire de connexion DB locale
        │   ├── SupportTicket.java            # Modèle métier (DataObject)
        │   ├── TicketExporter.java           # Outil statique de création CSV
        │   ├── TicketDaoTests.java           # Tests de la persistance SQL (assert)
        │   └── TicketPersistenceServiceTests.java # Tests du service et de la liste
        └── resources/com/example/tp3/
            └── ticket-persistence.css        # Styles pour uniformiser l'UI
```

---

## 🧪 Série de tests à réaliser (Démonstration)

Voici comment valider que toutes les fonctionnalités du projet marchent à 100% :

### Test 1 : Création et Persistance (Le cœur du TP)
*   **Action :** Remplis le formulaire à gauche (Titre, Client, Priorité, etc.) et clique sur **"➕ Ajouter"**.
*   **Résultat attendu :** Le ticket apparaît instantanément dans le tableau à droite.
*   **Action :** Ferme l'application avec la croix (X) puis relance-la via `Launcher.java`.
*   **Résultat attendu :** Le ticket est **toujours là**. Les données sont bien sauvegardées dans la base de données.

### Test 2 : Modification (Update)
*   **Action :** Clique sur la ligne de ton ticket dans le tableau. Modifie le statut (par exemple passe-le à "Résolu") sur le formulaire de gauche, puis clique sur **"✏️ Modifier"**.
*   **Résultat attendu :** La ligne se met intelligemment à jour dans le tableau sans avoir besoin de redémarrer.

### Test 3 : Recherche en temps réel
*   **Action :** Crée un deuxième ticket avec un titre complètement différent. Dans la barre de recherche en bas à gauche, commence à taper le titre de ton premier ticket.
*   **Résultat attendu :** La liste se réduit *pendant que tu tapes* pour ne garder que le ticket correspondant (comportement d'un filtre dynamique).

### Test 4 : L'export CSV
*   **Action :** Clique sur le bouton **"📤 Exporter CSV"**.
*   **Résultat attendu :** L'interface affiche "Export réussi" et un dossier `exports/` apparaît dans l'arborescence du projet contenant un fichier `.csv` lisible avec les données.

### Test 5 : Suppression
*   **Action :** Sélectionne un ticket et clique sur **"🗑️ Supprimer"**.
*   **Résultat attendu :** Une alerte demande confirmation. Si "Oui", le ticket disparaît de la vue et de la lase de données.

---

## 🏗️ Ce qui a été fait (De A à Z)

Pour répondre à toutes les exigences du TP, voici les éléments implémentés :

1.  **Modèle Métier (`SupportTicket.java`) :** Création d'un objet Java immuable représentant la donnée pour éviter toute corruption d'état.
2.  **Couche d'accès aux données (`TicketDao.java` & `SQLiteTicketDao.java`) :** Implémentation du "Data Access Object". On utilise `PreparedStatement` pour sécuriser les insertions et on convertit proprement les types (comme `LocalDate` en `String` ISO 8601 pour SQLite).
3.  **Gestion de Base de Données (`DatabaseManager.java`) :** Connexion isolée avec `CREATE TABLE IF NOT EXISTS` automatique au démarrage.
4.  **Service Utilisateur (`TicketPersistenceService.java`) :** Création d'une couche intermédiaire entre la base SQLite et JavaFX afin de stocker et rafraichir une `ObservableList<SupportTicket>` (ce qui met l'UI à jour de manière réactive).
5.  **Fenêtre et UI (`TicketPersistenceApp.java` & `ticket-persistence.css`) :** Formulaire complet, TableView bindée, filtres dynamiques, contraintes de Layouts (FlowPane) pour le responsive et application d'une esthétique par CSS.
6.  **Tests Locaux :** Implémentations de classes de tests `main` avec directives d'assertions (`assert`).

---

## ⚠️ Difficultés rencontrées & Solutions

*   **Récupération de la clé primaire (ID) générée par SQLite :** 
    *   *Difficulté :* Lors de l'ajout d'un ticket, le modèle n'a pas d'ID (il vaut 0), mais la TableView en a besoin pour l'affichage immédiat. 
    *   *Solution :* Utilisation de `Statement.RETURN_GENERATED_KEYS` dans l'insertion JDBC pour attraper l'ID auto-incrémenté depuis la db, puis retour d'un nouveau ticket `ticket.withId(id_genere)` vers l'UI grâce à l'immuabilité et la programmation fonctionnelle.
*   **Lien dynamique entre la Base de Données et l'Affichage (JavaFX) :**
    *   *Difficulté :* Rafraîchir l'interface sans surcharger le code UI ni faire de gros lags sur l'UI avec SQLite.
    *   *Solution :* Le contrôleur modifie la source de vérité (SQLite), puis demande au service de déclencher son `.refresh()`, qui vide et reremplit proprement l'`ObservableList` unique ; la TableView s'actualisant toute seule.
*   **Textes rognés sur Windows avec JavaFX :**
    *   *Difficulté :* Les boutons du bas avaient un texte découpé sur certaines résolutions car JavaFX calculait mal l'espace sur un conteneur rigide `HBox`.
    *   *Solution :* Substitution par une `FlowPane` adaptative (`wrap` automatique) assorti d'un ajustement CSS du padding et des largeurs du panneau contenant le formulaire.