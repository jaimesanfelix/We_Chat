package com.fct.we_chat;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * GrupoChatFX is a JavaFX class that provides a graphical user interface for creating chat groups.
 */
public class GrupoChatFX {

    /**
     * Shows the group creation window.
     */
    public static void showGroupWindow() {
        Stage groupStage = new Stage();
        groupStage.setTitle("Crear Grupo");

        // Listas de usuarios
        ListView<String> availableUsers = new ListView<>();
        ListView<String> groupMembers = new ListView<>();

        // Botón para mover usuarios
        Button addButton = new Button(" >> Agregar");
        addButton.setOnAction(e -> {
            String selectedUser = availableUsers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                availableUsers.getItems().remove(selectedUser);
                groupMembers.getItems().add(selectedUser);
            }
        });

        Button removeButton = new Button(" << Quitar");
        removeButton.setOnAction(e -> {
            String selectedUser = groupMembers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                groupMembers.getItems().remove(selectedUser);
                availableUsers.getItems().add(selectedUser);
            }
        });

        // Layout principal
        HBox userSelectionLayout = new HBox(10);
        VBox leftPanel = new VBox(new Label("Usuarios Disponibles"), availableUsers);
        VBox rightPanel = new VBox(new Label("Miembros del Grupo"), groupMembers);
        VBox buttonPanel = new VBox(10, addButton, removeButton);
        buttonPanel.setPadding(new Insets(30, 10, 10, 10));
        
        userSelectionLayout.getChildren().addAll(leftPanel, buttonPanel, rightPanel);

        Button createGroupButton = new Button("Crear Grupo");
        createGroupButton.setOnAction(e -> {
            System.out.println("Grupo creado con miembros: " + groupMembers.getItems());
            groupStage.close();
        });

        VBox layout = new VBox(10, userSelectionLayout, createGroupButton);
        layout.setPadding(new Insets(10));

        groupStage.setScene(new Scene(layout, 500, 400));
        groupStage.show();
    }
}

