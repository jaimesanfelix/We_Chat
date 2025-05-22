package com.fct.we_chat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatClientFX extends Application {

    ChatClient c = new ChatClient();
    public static TextArea chatArea;

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
            c.nickname = nicknameField.getText();
            if (!c.nickname.isEmpty()) {
                loginStage.close();
                showChatWindow();
                try {
                    c.connectToServer();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        loginStage.setScene(new Scene(loginLayout, 300, 150));
        loginStage.show();
    }

    private void showChatWindow() {
        chatArea = new TextArea();
        TextField messageField = new TextField();
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat - " + c.nickname);

        // Configuración de la interfaz del chat
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        chatArea.setEditable(false);

        HBox inputLayout = new HBox(10);
        Button sendButton = new Button("Enviar");
        Button logoutButton = new Button("Cerrar sesion");

        inputLayout.getChildren().addAll(messageField, sendButton, logoutButton);
        inputLayout.setHgrow(messageField, Priority.ALWAYS);

        layout.getChildren().addAll(new Label("Usuario: " + c.nickname), chatArea, inputLayout);
        
        sendButton.setOnAction(e -> {
            try {
                String message = messageField.getText();
                messageField.clear();
                c.sendMessage(message);
                //chatArea.appendText(ChatClient.chat);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        
        logoutButton.setOnAction(e -> c.logout());

        chatStage.setScene(new Scene(layout, 400, 300));
        chatStage.show();
    }


    /*private void userList(){
        ReadOnlyObjectProperty<ObservableList<Player>> playersProperty =
				new SimpleObjectProperty<>(FXCollections.observableArrayList());
    }*/

}
