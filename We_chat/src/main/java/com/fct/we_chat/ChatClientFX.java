package com.fct.we_chat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.fct.we_chat.model.Message;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChatClientFX extends ChatClient {
    private HostServices hostServices; // Para abrir archivos sin usar AWT
    // private ListView<HBox> chatList = new ListView<>();
    private ListView<HBox> chatList;
    private ImageView imageView;
    // ChatClient c = new ChatClient();
    GrupoChatFX g = new GrupoChatFX();
    public static TextArea chatArea;
    public static TextArea chatAreaPrivate;
    private Key clavePublica;
    private PrintWriter out;
    private TextField messageField = new TextField();
    public static ListView<String> userList = new ListView<>(); // Lista de usuarios conectados
    Boolean privateChatWindow = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.hostServices = getHostServices(); // Inicializar HostServices
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        // Ventana de Login
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Entrar");
        Button registerButton = new Button("Registrarse");
        Label usuario = new Label("Ingrese su usuario:");
        usuario.getStyleClass().add("user-label");
        Label passwd = new Label("Ingrese su contraseña:");
        passwd.getStyleClass().add("user-label");

        loginLayout.getChildren().addAll(usuario, usernameField,
                passwd, passwordField, loginButton, registerButton);

                //.add("user-label")

        loginButton.setOnAction(e -> {
            username = usernameField.getText();
            password = passwordField.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                System.out.println("Nick: " + username);
                System.out.println("Contraseña: " + password);
                // comprobamos usuario y contraseña
                boolean login_success;
                try {
                    login_success = validateLogin(username, password);
                    if (login_success == false) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Error al validar usuario o passaword, debe de registrarse primero.");
                        alert.showAndWait();
                        return;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                loginStage.close();

                // ArrayList<String> grupos = getGroupsByUser(username);
                // Añadimos a la lista de usuarios los grupos a los que pertenece el username
                // userList.getItems().setAll(grupos);
                showChatWindow();
                try {
                    connectToServer(this);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, complete todos los campos.");
                alert.showAndWait();
            }
        });

        // Acción del botón de Registro
        registerButton.setOnAction(e -> showRegisterWindow());

        loginButton.getStyleClass().add("chat-button");
        registerButton.getStyleClass().add("chat-button");

        Scene scene = new Scene(loginLayout, 300, 275);
        loginStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        loginStage.show();
    }

    private void showRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Registro");

        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(10));

        TextField regUsernameField = new TextField();
        TextField regEmailField = new TextField();
        PasswordField regPasswordField = new PasswordField();
        PasswordField regConfirmPasswordField = new PasswordField();
        Button registerButton = new Button("Registrarse");

        registerLayout.getChildren().addAll(
                new Label("Ingrese su nick:"), regUsernameField,
                new Label("Ingrese su email:"), regEmailField,
                new Label("Ingrese su contraseña:"), regPasswordField,
                new Label("Confirme su contraseña:"), regConfirmPasswordField,
                registerButton);

        registerButton.setOnAction(e -> {
            String regUsername = regUsernameField.getText();
            String regEmail = regEmailField.getText();
            String regPassword = regPasswordField.getText();
            String regConfirmPassword = regConfirmPasswordField.getText();

            if (regUsername.isEmpty() || regEmail.isEmpty() || regPassword.isEmpty() || regConfirmPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, complete todos los campos.");
                alert.showAndWait();
            } else if (!isValidEmail(regEmail)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "El email ingresado no es válido.");
                alert.showAndWait();
            } else if (!regPassword.equals(regConfirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Las contraseñas no coinciden. Inténtelo de nuevo.");
                alert.showAndWait();
            } else {
                System.out.println("Registrado:");
                System.out.println("Nick: " + regUsername);
                System.out.println("Email: " + regEmail);
                System.out.println("Contraseña: " + regPassword);
                registerStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registro exitoso. Ahora puede iniciar sesión.");
                alert.showAndWait();
                try {
                    saveUserToDatabase(regUsername, regEmail, regConfirmPassword);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        registerStage.setScene(new Scene(registerLayout, 300, 300));
        registerStage.show();
    }

    // Método para validar el email con una expresión regular
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private void showPrivatechatWindow(String User) {
        privateChatWindow = true;
        chatAreaPrivate = new TextArea();
        TextField messageFieldPrivate = new TextField();
        Stage chatStagePrivate = new Stage();
        chatStagePrivate.setTitle("Chat Privado - " + username + " con " + User);

        BorderPane layoutPrivate = new BorderPane();
        layoutPrivate.setPadding(new Insets(10));

        chatAreaPrivate.setEditable(false);
        layoutPrivate.setCenter(chatAreaPrivate);

        Button logoutButtonPrivate = new Button("Cerrar sesion");

        // VBox rightPanel = new VBox(10);
        // rightPanel.getChildren().addAll(new Label("Usuario Conectado:"), userList);
        // rightPanel.setPrefWidth(150);
        // layout.setRight(rightPanel);

        HBox inputLayoutPrivate = new HBox(10);
        Button sendButtonPrivate = new Button("Enviar");
        Button changeNicknameButtonPrivate = new Button("Cambiar nickname");

        int from = getUserIdByUsername(username);
        int to = 0;
        int group = 0;
        if (!isGroup(User)) {
            to = getUserIdByUsername(User);
            group = 0;
        } else if (isGroup(User)) {
            group = getGroupIdByUsername(User);
            to = 0;
        }

        mostrarMensajes(to, from, group);
        /*
         * List<Message> messages = loadMessages(to,from,group);
         * for (int i = 0; i < messages.size(); i++) {
         * mostrar(getUsernameById(messages.get(i).getUser_from_id()) + ": " +
         * messages.get(i).getContent() + ": "
         * + messages.get(i).getTimestamp());
         * }
         */

        changeNicknameButtonPrivate.setOnAction(e -> {
            try {
                String message = messageFieldPrivate.getText();
                messageFieldPrivate.clear();

                changeNickname(User, message , username);

                // Llamar a funcion changeNickname

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        sendButtonPrivate.setOnAction(e -> {
            try {
                String message = messageFieldPrivate.getText();
                messageFieldPrivate.clear();
                // sendMessage(message);
                if (isGroup(User)) {
                    sendMessageToGroup(User, message);
                } else {
                    sendMessageToUser(User, message);
                }
                // chatArea.appendText(ChatClient.chat);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        // Button gruposButton = new Button("Nuevo Grupo");
        inputLayoutPrivate.getChildren().addAll(messageFieldPrivate, sendButtonPrivate, changeNicknameButtonPrivate);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        layoutPrivate.setBottom(inputLayoutPrivate);
        logoutButtonPrivate.setOnAction(e -> {
            try {
                logout();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        chatStagePrivate.setOnCloseRequest(event -> {
            privateChatWindow = false; // Cambia la variable a false
            System.out.println("La ventana se ha cerrado. ventanaAbierta = " + privateChatWindow);
        });
        chatStagePrivate.setScene(new Scene(layoutPrivate, 500, 400));
        chatStagePrivate.show();

    }

    public void selectAndPreviewImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.*"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            // imageView.setImage(image);
            System.out.println("Imagen seleccionada: " + file.getAbsolutePath());
            sendFileToServer(file);

            // addTextMessage("Usuario1: Hola!");
            // addImageMessage("https://www.w3.org/html/logo/downloads/HTML5_Badge_512.png");
            // addImageMessage(file.toURI().toString());
            displayReceivedFile(file.toURI().toString());
        }
    }

    private void addTextMessage(String message) {
        HBox messageBox = new HBox(new Text(message));
        chatList.getItems().add(messageBox);
    }

    /*
     * private void addImageMessage(String imageUrl) {
     * ImageView imageView = new ImageView(new Image(imageUrl));
     * imageView.setFitWidth(200);
     * imageView.setPreserveRatio(true);
     * HBox imageBox = new HBox(imageView);
     * chatList.getItems().add(imageBox);
     * }
     */

    private void addImageMessage(String imageUrl) {
        Platform.runLater(() -> {
            try {
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);

                // Botón para descargar la imagen
                Button downloadButton = new Button("Descargar");

                downloadButton.setOnAction(e -> {
                    downloadImage(imageUrl);
                });

                HBox imageBox = new HBox(10, imageView, downloadButton);
                if (chatList == null) {
                    System.out.println("Error: chatList es null. Asegúrate de inicializarlo antes de usarlo.");
                    return;
                }
                chatList.getItems().add(imageBox);
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen: " + e.getMessage());
            }
        });
    }

    // Método para descargar la imagen
    private void downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar imagen");
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg"));

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (InputStream in = url.openStream()) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Imagen descargada con éxito en: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al descargar la imagen: " + e.getMessage());
        }
    }

    private void showChatWindow() {
        // Button gruposButton = new Button("Crear Nuevo Grupo");
        chatArea = new TextArea();
        chatList = new ListView<>();
        TextField messageField = new TextField();
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat - " + username);

        imageView = new ImageView(); // Inicialización
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        Button sendFileButton = new Button("Adjuntar Archivo");
        Button refreshMessages = new Button("Actualizar Mensajes");
        refreshMessages.setOnAction(e -> {
            mostrarMensajes(0, 0, 0);
        });

        sendFileButton.getStyleClass().add("chat-button");
        refreshMessages.getStyleClass().add("chat-button");
        sendFileButton.setOnAction(e -> {
            /*
             * String groupName = "Amigos"; // Puedes hacer que el usuario lo elija
             * String message = messageField.getText();
             * sendGroupMessage(groupName, message);
             * messageField.clear();
             */
            // sendFile();
            selectAndPreviewImage();
        });

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        chatArea.setEditable(false);
        // layout.setCenter(chatArea);

        layout.setCenter(chatList);

        // Configuración de la interfaz del chat
        // VBox layout = new VBox(10);
        // layout.setPadding(new Insets(10));

        // chatArea.setEditable(false);

        // HBox inputLayout = new HBox(10);
        // Button sendButton = new Button("Enviar");
        Button logoutButton = new Button("Cerrar sesion");

        VBox rightPanel = new VBox(10);
        rightPanel.getChildren().addAll(new Label("Usuarios Conectados y grupos a los que pertenece:"), userList);
        rightPanel.setPrefWidth(150);
        layout.setRight(rightPanel);

        HBox inputLayout = new HBox(10);
        Button sendButton = new Button("Enviar");
        Button gruposButton = new Button("Nuevo Grupo");
        inputLayout.getChildren().addAll(messageField, sendButton, gruposButton, sendFileButton, refreshMessages,
                imageView, logoutButton);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        layout.setBottom(inputLayout);

        System.out.println("llamnado al método desde la clase Padre...");

        mostrarMensajes(0, 0, 0);

        
        sendButton.getStyleClass().add("chat-button");

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
                        // Mostramos Chat Privado
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

        gruposButton.getStyleClass().add("chat-button");

        gruposButton.setOnAction(e -> {

            // loginStage.close();
            // showGrupos();

            g.showGroupWindow();
            try {
                // connectToServer(this);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        });

        chatStage.setOnCloseRequest(event -> {
            try {
                System.out.println("La ventana se ha cerrado. ventanaAbierta");
                logout();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        });

        logoutButton.getStyleClass().add("chat-button");

        logoutButton.setOnAction(e -> {
            try {
                logout();
                chatStage.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        Scene scene = new Scene(layout, 1200, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        chatStage.setScene(scene);
        // chatStage.setScene(new Scene(chatList, 400, 300));
        chatStage.show();

        // Simulación de mensajes
        // addTextMessage("Usuario1: Hola!");
        // addImageMessage("https://www.w3.org/html/logo/downloads/HTML5_Badge_512.png");
    }

    private void showGrupos() {

    }

    @Override
    void mostrar(String message) {

        if (message.startsWith("USERS:")) {
            updateUserList(message.substring(6).split(","));
        } else if (message.startsWith("FILE:")) {
            String fileName = message.substring(5);
            fileName = "uploads/" + fileName;
            File file = new File(fileName);
            if (file.exists()) {
                String filePath = file.toURI().toString();
                System.out.println("Cargando imagen desde: " + filePath);

                // addImageMessage(filePath);
                displayReceivedFile(filePath);
            }
        } else {
            String mensaje = messageField.getText();
            // chatArea.appendText(message + "\n");
            if (mensaje.isBlank() && privateChatWindow) {
                chatAreaPrivate.appendText(message + "\n");
                // addImageMessage(file.toURI().toString());
            } else {
                Platform.runLater(() -> {
                    HBox messageBox = new HBox(new Text(message));
                    chatList.getItems().add(messageBox);
                    chatList.scrollTo(chatList.getItems().size() - 1);
                });
                // System.out.println("Error: chatList es null, mensaje no agregado.");
            }
            // if (chatAreaPrivate != null)

        }
    }

    private void updateUserList(String[] users) {
        Platform.runLater(() -> {
            try {
                // Usamos un LinkedHashSet para eliminar duplicados y mantener el orden
                Set<String> uniqueUsers = new LinkedHashSet<>(Arrays.asList(users));


                List<String> nicknames = new ArrayList<>();
                nicknames = getNicknamesByUser(username);
                // Comprobamos si el username tiene apodos
                if (HasNickname(username)) {

                    List<String> userList2 = new ArrayList<>(uniqueUsers);

                    uniqueUsers.clear();
                    for (int i = 0; i < userList2.size(); i++) {
                        System.out.println("Usuario " + i + ": " + userList2.get(i));
                        String apodo = getNicknameByUserName(userList2.get(i));

                        if (apodo != null) {
                            uniqueUsers.add(apodo);
                        } else {
                            uniqueUsers.add(userList2.get(i));
                        }

                    }
                }

                // Actualizamos la lista con los usuarios únicos
                userList.getItems().setAll(uniqueUsers);

                userList.getItems().addAll(getGroupsByUser(username));

            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("Error al actualizar los usuarios: " + e.getMessage());
            }

        });
    }

    /*
     * public void sendGroupMessage(String groupName, String message) {
     * if (!message.isEmpty()) {
     * out.println("#grupo " + groupName + " " + message);
     * }
     * }
     */

    public void createGroup(String groupName) {
        out.println("!crearGrupo " + groupName);
    }

    /*
     * private void userList(){
     * ReadOnlyObjectProperty<ObservableList<Player>> playersProperty =
     * new SimpleObjectProperty<>(FXCollections.observableArrayList());
     * }
     */
    public void loadImageFromServer(String fileName) {
        // Ruta donde el servidor guarda las imágenes
        String filePath = "uploads/" + fileName;

        // Crear un objeto File
        File file = new File(filePath);

        // Verificar si el archivo existe antes de cargarlo
        if (file.exists()) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            System.out.println("Imagen cargada correctamente.");
        } else {
            System.out.println("La imagen no existe en el servidor.");
        }
    }

    // Método para agregar un archivo genérico con botón para abrirlo
    private void addFileMessage(String filePath) {
        Platform.runLater(() -> {

            Path path = Paths.get(URI.create(filePath));
            File file = path.toFile();

            // File file = new File( new URI(filePath));
            if (!file.exists()) {
                System.out.println("Error: El archivo no existe en " + filePath);
                return;
            }

            Text fileNameText = new Text(file.getName());
            Button openButton = new Button("Descargar");

            // Usar HostServices para abrir el archivo
            openButton.setOnAction(e -> hostServices.showDocument(file.toURI().toString()));

            HBox fileBox = new HBox(10, fileNameText, openButton);
            chatList.getItems().add(fileBox);
        });
    }

    // Método para decidir si agregar una imagen o un archivo normal
    public void displayReceivedFile(String filePath) {
        if (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            addImageMessage(filePath);
        } else {
            addFileMessage(filePath);
        }
    }

    public void mostrarMensajes(int to, int from, int group) {
        List<Message> messages = loadMessages(to, from, group);

        for (Message msg : messages) {
            String username = getUsernameById(msg.getUser_from_id());
            String texto = username + ": " + msg.getContent() + ": " + msg.getTimestamp();
            mostrar(texto);
        }
    }

}
