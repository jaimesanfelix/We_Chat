package com.fct.we_chat;

import java.util.ArrayList;
import java.util.List;

import com.fct.we_chat.model.Group;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GrupoChatFX extends ChatClient {

    // Lista para almacenar los grupos creados
    private static final List<Group> gruposCreados = new ArrayList<>();
    private static ArrayList<Group> gruposDelUsuario = new ArrayList<>();
    private static String nombreGrupoaEditar;

    public static void showGroupWindow() {
        Stage groupStage = new Stage();
        groupStage.setTitle("Crear Grupo");

        // Listas de usuarios
        ListView<String> availableUsers = new ListView<>();
        ListView<String> groupMembers = new ListView<>();

        // Obtener la lista de usuarios conectados
        availableUsers.setItems(FXCollections.observableArrayList(ChatClientFX.userList.getItems()));

        // Campo para el nombre del grupo
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("nombre del campo");

        availableUsers.setItems(ChatClientFX.userList.getItems());

        // Botón para mover usuarios
        Button addButton = new Button(">> Agregar");
        addButton.setOnAction(e -> {
            String selectedUser = availableUsers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                availableUsers.getItems().remove(selectedUser);
                groupMembers.getItems().add(selectedUser);
            }
        });

        Button removeButton = new Button("<< Quitar");
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

        // Botón para crear el grupo
        Button createGroupButton = new Button("Crear Grupo");
        createGroupButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                System.out.println("Por favor, ingrese un nombre para el grupo.");
                return;
            }

            if (groupMembers.getItems().isEmpty()) {
                System.out.println("El grupo debe tener al menos un miembro.");
                return;
            }

            // Crear un nuevo grupo y agregarlo a la lista
            // Group nuevoGrupo = new Group(groupName, new
            // ArrayList<User>(groupMembers.getItems()));
            // gruposCreados.add(nuevoGrupo);
            System.out.println("Miembros del grupo");
            for (String user : groupMembers.getItems()) {
                System.out.println(user); // O cualquier operación con el usuario
            }

            // Agregar el grupo a la lista de usuarios conectados
            // ChatClientFX.userList.getItems().add(groupName);
            saveGroup(groupName, groupMembers.getItems());

            // Mostrar mensaje
            /*
             * System.out.println("Grupo creado con nombre: " + nuevoGrupo.getName() +
             * " con miembros: " + nuevoGrupo.getUserGroups());
             */

            groupStage.close();
        });

        VBox layout = new VBox(10, new Label("Nombre del Grupo:"), groupNameField, userSelectionLayout,
                createGroupButton);
        layout.setPadding(new Insets(10));

        groupStage.setScene(new Scene(layout, 650, 400));
        groupStage.show();
    }

    /**
     * @param grupo
     */
    public static void showEditGroupWindow(Group grupo) {
        Stage groupStage = new Stage();
        groupStage.setTitle("Editar Grupo: " + grupo.getName());

        // ComboBox para seleccionar el grupo
        ComboBox<Group> groupSelector = new ComboBox<>();
        List<Group> gruposDelUsuario = getGroupsByUser2(username);
       
        // Listas de usuarios
        ListView<String> availableUsers = new ListView<>();
        ListView<String> groupMembers = new ListView<>();

        // Obtener usuarios conectados y actuales miembros
        List<String> todosLosUsuarios = new ArrayList<>(ChatClientFX.userList.getItems());
        //List<String> miembros = new ArrayList<>(getUsersByGroup()); // Asegúrate
        // de tener getUserGroups()

        // Separar disponibles y miembros
        // todosLosUsuarios.removeAll(miembros);
        availableUsers.setItems(FXCollections.observableArrayList(todosLosUsuarios));
        // groupMembers.setItems(FXCollections.observableArrayList(miembros));
       
       
        groupSelector.setItems(FXCollections.observableArrayList(gruposDelUsuario));
        groupSelector.setPromptText("Seleccionar grupo...");

        // Campo de nombre deshabilitado
        TextField groupNameField = new TextField(grupo.getName());
        groupNameField.setDisable(true);

        // Al seleccionar un grupo
       
        groupSelector.setOnAction(e -> {
        Group selectedGroup = groupSelector.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                groupNameField.setText(selectedGroup.getName());
                nombreGrupoaEditar = selectedGroup.getName();
                List<String> todos = new ArrayList<>(ChatClientFX.userList.getItems());
                List<String> miembros = getUsersByGroup(selectedGroup.getName());

                List<String> disponibles = new ArrayList<>(todos);
                // disponibles.removeAll(miembros);

                availableUsers.setItems(FXCollections.observableArrayList(disponibles));
                groupMembers.setItems(FXCollections.observableArrayList(miembros));

               
            }

            
        
        });

       

        

        // Botón para mover usuarios
        Button addButton = new Button(">> Agregar");
        addButton.setOnAction(e -> {
            String selectedUser = availableUsers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                availableUsers.getItems().remove(selectedUser);
                groupMembers.getItems().add(selectedUser);
            }
        });

        Button removeButton = new Button("<< Quitar");
        removeButton.setOnAction(e -> {
            String selectedUser = groupMembers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                groupMembers.getItems().remove(selectedUser);
                availableUsers.getItems().add(selectedUser);
            }
        });

        

        // Botón para guardar cambios
        Button saveChangesButton = new Button("Guardar Cambios");
        saveChangesButton.setOnAction(e -> {
            // grupo.getUserGroups().clear();
            // grupo.getUserGroups().addAll(groupMembers.getItems());
            // System.out.println("Grupo actualizado: " + grupo.getName() + " -> " +
            // grupo.getUserGroups());
            saveOrUpdateGroup(nombreGrupoaEditar, groupMembers.getItems());
            groupStage.close();
        });

        // Layouts
        HBox userSelectionLayout = new HBox(10);
        VBox leftPanel = new VBox(new Label("Usuarios Disponibles"), availableUsers);
        VBox rightPanel = new VBox(new Label("Miembros del Grupo"), groupMembers);
        VBox buttonPanel = new VBox(10, addButton, removeButton);
        buttonPanel.setPadding(new Insets(30, 10, 10, 10));
        userSelectionLayout.getChildren().addAll(leftPanel, buttonPanel, rightPanel);

        VBox layout = new VBox(10,
                new Label("Seleccionar Grupo:"), groupSelector,
                new Label("Nombre del Grupo:"), groupNameField, userSelectionLayout,
                saveChangesButton);
        layout.setPadding(new Insets(10));

        groupStage.setScene(new Scene(layout, 650, 400));
        groupStage.show();
    }

    // Método para obtener la lista de grupos creados
    public static List<Group> getGruposCreados() {
        return gruposCreados;
    }

}
