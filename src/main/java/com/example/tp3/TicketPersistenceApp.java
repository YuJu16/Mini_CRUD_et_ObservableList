package com.example.tp3;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Application JavaFX principale – CRUD complet sur les tickets support.
 * Fonctionnalités : création, lecture, modification, suppression,
 * recherche locale et export CSV.
 */
public class TicketPersistenceApp extends Application {

    // ---- Service ----
    private TicketPersistenceService service;

    // ---- Composants formulaire ----
    private final TextField        titleField    = new TextField();
    private final TextField        customerField = new TextField();
    private final ComboBox<String> priorityBox   = new ComboBox<>();
    private final ComboBox<String> statusBox     = new ComboBox<>();
    private final DatePicker       datePicker    = new DatePicker(LocalDate.now());
    private final TextArea         descArea      = new TextArea();
    private final CheckBox         urgentCheck   = new CheckBox("Urgent");
    private final TextField        searchField   = new TextField();
    private final Label            statusLabel   = new Label("Prêt.");

    // ---- Table ----
    private final TableView<SupportTicket> table = new TableView<>();

    // ---- Mode édition : id du ticket sélectionné (-1 = aucun) ----
    private long selectedId = -1;

    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();
        service = new TicketPersistenceService();

        // ---- Titre ----
        Label pageTitle = new Label("🎫 Gestionnaire de Tickets Support");
        pageTitle.getStyleClass().add("page-title");

        // ---- Formulaire ----
        priorityBox.getItems().addAll("Faible", "Moyenne", "Haute", "Critique");
        priorityBox.setValue("Moyenne");
        statusBox.getItems().addAll("Nouveau", "En cours", "Résolu");
        statusBox.setValue("Nouveau");

        titleField.setPromptText("Titre du ticket");
        customerField.setPromptText("Nom du client");
        descArea.setPromptText("Description...");
        descArea.setPrefRowCount(3);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));

        form.add(new Label("Titre :"),       0, 0); form.add(titleField,    1, 0);
        form.add(new Label("Client :"),      0, 1); form.add(customerField, 1, 1);
        form.add(new Label("Priorité :"),    0, 2); form.add(priorityBox,   1, 2);
        form.add(new Label("Statut :"),      0, 3); form.add(statusBox,     1, 3);
        form.add(new Label("Date :"),        0, 4); form.add(datePicker,    1, 4);
        form.add(new Label("Description :"),0, 5); form.add(descArea,      1, 5);
        form.add(urgentCheck,                1, 6);

        ColumnConstraints cc0 = new ColumnConstraints(90);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(cc0, cc1);

        // ---- Boutons ----
        Button addBtn    = new Button("➕ Ajouter");
        Button updBtn    = new Button("✏️ Modifier");
        Button delBtn    = new Button("🗑️ Supprimer");
        Button resetBtn  = new Button("↩ Réinitialiser");
        Button reloadBtn = new Button("🔄 Recharger");
        Button exportBtn = new Button("📤 Exporter CSV");

        FlowPane btnBar = new FlowPane(8, 8, addBtn, updBtn, delBtn, resetBtn, reloadBtn, exportBtn);
        btnBar.setPadding(new Insets(6, 0, 6, 0));

        // ---- Recherche ----
        searchField.setPromptText("🔍 Titre ou client...");
        HBox searchBar = new HBox(8, new Label("Recherche :"), searchField);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // ---- Status ----
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        // ---- Colonne gauche ----
        VBox leftPane = new VBox(10, pageTitle, form, btnBar, searchBar, statusLabel);
        leftPane.setPadding(new Insets(14));
        // Agrandissement significatif pour laisser la place aux 6 boutons
        leftPane.setPrefWidth(520);
        leftPane.setMinWidth(520);

        // ---- Table ----
        buildTable();
        VBox rightPane = new VBox(10, new Label("Liste des tickets :"), table);
        rightPane.setPadding(new Insets(14, 14, 14, 0));
        VBox.setVgrow(table, Priority.ALWAYS);

        // ---- Layout racine ----
        HBox root = new HBox(leftPane, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // ---- Scène ----
        Scene scene = new Scene(root, 1100, 640);
        try {
            String css = getClass().getResource("ticket-persistence.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("CSS introuvable : " + e.getMessage());
        }

        stage.setTitle("Gestionnaire de Tickets – TP03");
        stage.setScene(scene);
        stage.show();

        // ---- Câblage des événements ----
        wireEvents(addBtn, updBtn, delBtn, resetBtn, reloadBtn, exportBtn);
    }

    // ======================================================
    //  Construction de la TableView
    // ======================================================

    private void buildTable() {
        TableColumn<SupportTicket, Long>    colId       = new TableColumn<>("ID");
        TableColumn<SupportTicket, String>  colTitle    = new TableColumn<>("Titre");
        TableColumn<SupportTicket, String>  colCustomer = new TableColumn<>("Client");
        TableColumn<SupportTicket, String>  colPriority = new TableColumn<>("Priorité");
        TableColumn<SupportTicket, String>  colDate     = new TableColumn<>("Date");
        TableColumn<SupportTicket, Boolean> colUrgent   = new TableColumn<>("Urgent");
        TableColumn<SupportTicket, String>  colStatus   = new TableColumn<>("Statut");

        colId      .setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()).asObject());
        colTitle   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));
        colPriority.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPriority()));
        colDate    .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt().toString()));
        colUrgent  .setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isUrgent()).asObject());
        colStatus  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        colId.setPrefWidth(45);
        colTitle.setPrefWidth(170);
        colCustomer.setPrefWidth(120);
        colPriority.setPrefWidth(80);
        colDate.setPrefWidth(95);
        colUrgent.setPrefWidth(60);
        colStatus.setPrefWidth(90);

        table.getColumns().addAll(colId, colTitle, colCustomer, colPriority,
                colDate, colUrgent, colStatus);
        table.setItems(service.getTickets());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ======================================================
    //  Câblage des événements
    // ======================================================

    private void wireEvents(Button addBtn, Button updBtn, Button delBtn,
                            Button resetBtn, Button reloadBtn, Button exportBtn) {

        addBtn.setOnAction(e -> {
            if (!validateForm()) return;
            SupportTicket t = buildFromForm(0);
            service.createTicket(t);
            setStatus("✅ Ticket '" + t.getTitle() + "' créé.");
            clearForm();
        });

        updBtn.setOnAction(e -> {
            if (selectedId < 0) { setStatus("⚠️ Sélectionnez un ticket à modifier."); return; }
            if (!validateForm()) return;
            service.updateTicket(buildFromForm(selectedId));
            setStatus("✏️ Ticket #" + selectedId + " mis à jour.");
            clearForm();
        });

        delBtn.setOnAction(e -> {
            if (selectedId < 0) { setStatus("⚠️ Sélectionnez un ticket à supprimer."); return; }
            Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                    "Supprimer le ticket #" + selectedId + " ?",
                    ButtonType.YES, ButtonType.NO);
            dlg.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    service.deleteTicket(selectedId);
                    setStatus("🗑️ Ticket #" + selectedId + " supprimé.");
                    clearForm();
                }
            });
        });

        resetBtn.setOnAction(e -> clearForm());

        reloadBtn.setOnAction(e -> {
            service.refresh();
            table.setItems(service.getTickets());
            setStatus("🔄 Rechargé – " + service.getTickets().size() + " ticket(s).");
        });

        exportBtn.setOnAction(e -> {
            TicketExporter.exportToCsv(table.getItems(), "exports/tickets_export.csv");
            setStatus("📤 Export CSV → exports/tickets_export.csv");
        });

        // Sélection d'une ligne
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) fillFromTicket(sel);
        });

        // Recherche en temps réel
        searchField.textProperty().addListener((obs, old, kw) -> {
            if (kw == null || kw.isBlank()) {
                table.setItems(service.getTickets());
            } else {
                List<SupportTicket> res = service.search(kw);
                table.setItems(FXCollections.observableArrayList(res));
            }
        });
    }

    // ======================================================
    //  Utilitaires formulaire
    // ======================================================

    private SupportTicket buildFromForm(long id) {
        return new SupportTicket(
                id,
                titleField.getText().trim(),
                customerField.getText().trim(),
                priorityBox.getValue(),
                datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now(),
                descArea.getText().trim(),
                urgentCheck.isSelected(),
                statusBox.getValue()
        );
    }

    private void fillFromTicket(SupportTicket t) {
        selectedId = t.getId();
        titleField.setText(t.getTitle());
        customerField.setText(t.getCustomerName());
        priorityBox.setValue(t.getPriority());
        statusBox.setValue(t.getStatus());
        datePicker.setValue(t.getCreatedAt());
        descArea.setText(t.getDescription());
        urgentCheck.setSelected(t.isUrgent());
        setStatus("🖊️ Ticket #" + t.getId() + " sélectionné – modifiez puis cliquez Modifier.");
    }

    private void clearForm() {
        selectedId = -1;
        titleField.clear();
        customerField.clear();
        priorityBox.setValue("Moyenne");
        statusBox.setValue("Nouveau");
        datePicker.setValue(LocalDate.now());
        descArea.clear();
        urgentCheck.setSelected(false);
        searchField.clear();
        table.getSelectionModel().clearSelection();
        table.setItems(service.getTickets());
    }

    private boolean validateForm() {
        if (titleField.getText().isBlank())   { setStatus("⚠️ Titre obligatoire.");   return false; }
        if (customerField.getText().isBlank()) { setStatus("⚠️ Client obligatoire."); return false; }
        if (priorityBox.getValue() == null)    { setStatus("⚠️ Choisissez une priorité."); return false; }
        if (statusBox.getValue() == null)      { setStatus("⚠️ Choisissez un statut."); return false; }
        return true;
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    public static void main(String[] args) { launch(args); }
}
