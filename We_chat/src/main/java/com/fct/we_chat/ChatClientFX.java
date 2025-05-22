package com.fct.we_chat;

import java.io.PrintWriter;
import java.security.Key;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatClientFX extends ChatClient {

    //ChatClient c = new ChatClient();
    GrupoChatFX g = new GrupoChatFX();
    public static TextArea chatArea;
    public static TextArea chatAreaPrivate;
    private Key clavePublica;
    private PrintWriter out;
    private TextField messageField = new TextField();
    public static ListView<String> userList = new ListView<>(); // Lista de usuarios conectados

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        // Ventana de Login
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));
        TextField nicknameField = new TextField();
        Button loginButton = new Button("Entrar");
        loginLayout.getChildren().addAll(new Label("Ingrese su nick:"), nicknameField, loginButton);

        loginButton.setOnAction(e -> {
            nickname = nicknameField.getText();
            if (!nickname.isEmpty()) {
                loginStage.close();
                showChatWindow();
                try {
                    connectToServer(this);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        loginStage.setScene(new Scene(loginLayout, 300, 150));
        loginStage.show();
    }


    private void showPrivatechatWindow(String User) {

        chatAreaPrivate = new TextArea();
        TextField messageFieldPrivate = new TextField();
        Stage chatStagePrivate = new Stage();
        chatStagePrivate.setTitle("Chat Privado - " + nickname + " con " + User);

        BorderPane layoutPrivate = new BorderPane();
        layoutPrivate.setPadding(new Insets(10));

        chatAreaPrivate.setEditable(false);
        layoutPrivate.setCenter(chatAreaPrivate);

        
        Button logoutButtonPrivate = new Button("Cerrar sesion");

        //VBox rightPanel = new VBox(10);
        //rightPanel.getChildren().addAll(new Label("Usuario Conectado:"), userList);
        //rightPanel.setPrefWidth(150);
        //layout.setRight(rightPanel);

        HBox inputLayoutPrivate = new HBox(10);
        Button sendButtonPrivate = new Button("Enviar");

        sendButtonPrivate.setOnAction(e -> {
            try {
                String message = messageFieldPrivate.getText();
                messageFieldPrivate.clear();
                //sendMessage(message);
                sendMessageToUser(User, message);
                // chatArea.appendText(ChatClient.chat);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });




       // Button gruposButton = new Button("Nuevo Grupo");
        inputLayoutPrivate.getChildren().addAll(messageFieldPrivate, sendButtonPrivate);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        layoutPrivate.setBottom(inputLayoutPrivate);
        logoutButtonPrivate.setOnAction(e -> logout());

        chatStagePrivate.setScene(new Scene(layoutPrivate, 500, 400));
        chatStagePrivate.show();

    }


    private void showChatWindow() {
        // Button gruposButton = new Button("Crear Nuevo Grupo");
        chatArea = new TextArea();
        TextField messageField = new TextField();
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat - " + nickname);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        chatArea.setEditable(false);
        layout.setCenter(chatArea);

        // Configuración de la interfaz del chat
        // VBox layout = new VBox(10);
        // layout.setPadding(new Insets(10));

        // chatArea.setEditable(false);

        // HBox inputLayout = new HBox(10);
        // Button sendButton = new Button("Enviar");
        Button logoutButton = new Button("Cerrar sesion");

        VBox rightPanel = new VBox(10);
        rightPanel.getChildren().addAll(new Label("Usuarios Conectados:"), userList);
        rightPanel.setPrefWidth(150);
        layout.setRight(rightPanel);

        HBox inputLayout = new HBox(10);
        Button sendButton = new Button("Enviar");
        Button gruposButton = new Button("Nuevo Grupo");
        inputLayout.getChildren().addAll(messageField, sendButton, gruposButton);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        layout.setBottom(inputLayout);

        // layout.getChildren().addAll(new Label("Usuario: " + c.nickname), chatArea,
        // inputLayout);

        sendButton.setOnAction(e -> {
            try {
                String message = messageField.getText();
                messageField.clear();
                sendMessage(message);
                // chatArea.appendText(ChatClient.chat);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });


        userList.setOnMouseClicked(null);

        userList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    String selectedUser = userList.getSelectionModel().getSelectedItem();
                    if (selectedUser != null) {
                        try {
                            sendMessageToUser(selectedUser, message);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
            if (e.getClickCount() == 2) {
                               
                    String selectedUser = userList.getSelectionModel().getSelectedItem();
                    if (selectedUser != null) {
                        try {
                            //Mostramos Chat Privado
                            System.out.println("Doble clic en: " + selectedUser);
                            showPrivatechatWindow(selectedUser);
                            // sendMessageToUser(selectedUser, message);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
              
            }
        });

       

        gruposButton.setOnAction(e -> {

            // loginStage.close();
            // showGrupos();

            g.showGroupWindow();

        });

        logoutButton.setOnAction(e -> logout());

        chatStage.setScene(new Scene(layout, 400, 300));
        chatStage.show();
    }

    private void showGrupos() {

    }


    @Override
    void mostrar(String message) {
               
        if (message.startsWith("USERS:")) {
            updateUserList(message.substring(6).split(","));
        } else {
            chatArea.appendText(message + "\n");
            if (chatAreaPrivate != null )
            chatAreaPrivate.appendText(message + "\n");


        }
    }
 
    /*void mostrar(String message_descifrado) {
        super.mostrar(message_descifrado);
        if (message_descifrado.startsWith("USERS:")) {
            updateUserList(message_descifrado.substring(6).split(","));
        } else {
            chatArea.appendText(message_descifrado + "\n");

        }

    }*/

    private void updateUserList(String[] users) {
        userList.getItems().setAll(users);
    }

    
    /*
     * private void userList(){
     * ReadOnlyObjectProperty<ObservableList<Player>> playersProperty =
     * new SimpleObjectProperty<>(FXCollections.observableArrayList());
     * }
     */

}
