package com.fct.we_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.sql.Timestamp;

import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSAReceiver;
import com.fct.we_chat.utils.RSASender;

import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * ChatClient is an abstract class that provides common functionality for chat clients.
 */
class ChatClient extends Application {
    public final int RSA_BLOCK_SIZE = 256; // Tamaño de bloque para RSA con una clave de 2048 bits
    public static PrintWriter out;
    public static ListView<String> userList = new ListView<>(); // Lista de usuarios conectados
    public static String nickname;
    public static Timestamp tiempoUsuario = new Timestamp(System.currentTimeMillis());
    static Key clavePublica;
    public static String chat;

    /**
     * Displays a message.
     * @param message the message to display
     */
    void mostrar(String message) {
        System.out.println(message);
    }

    /**
     * Calls the mostrar method to display a message.
     * @param message the message to display
     */
    void llamarAMostrar(String message) {
        System.out.println("llamnado al método desde la clase Padre...");
        mostrar(message);
    }

    /**
     * Connects to the chat server.
     * @param clientInstance the instance of the chat client
     */
    public static void connectToServer(ChatClient clientInstance) {
        Key clavePrivada;
        try {
            Socket socket = new Socket("localhost", 12345);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            String nickname_cifrado = "";
            clavePublica = KeysManager.getClavePublica();
            clavePrivada = KeysManager.getClavePrivada();
            byte[] decryptedAESKeyBytes;

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            out = new PrintWriter(output, true);

            // Enviar nickname al servidor
            nickname_cifrado = RSASender.encryptMessage(nickname, clavePublica);
            out.println(nickname_cifrado);

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                String message;
                String message_descifrado;
                try {
                    while ((message = reader.readLine()) != null) {
                        message_descifrado = RSAReceiver.decryptMessage(message, clavePrivada);
                        clientInstance.mostrar(message_descifrado);
                        System.out.println(message_descifrado);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Error al conectar al servidor: " + e.getMessage());
        }
    }

    private static void updateUserList(String[] users) {
        ChatClientFX.userList.getItems().setAll(users);
    }

    /**
     * Sends a message to the chat server.
     * @param message the message to send
     */
    public static void sendMessage(String message) {
        String message_cifrado;
        if (!message.isEmpty()) {
            message_cifrado = RSASender.encryptMessage(message, clavePublica);
            out.println(message_cifrado);
        }
    }

    /**
     * Sends a message to a specific user.
     * @param user the user to send the message to
     * @param message the message to send
     * @throws Exception if an error occurs while sending the message
     */
    public void sendMessageToUser(String user, String message) throws Exception {
        clavePublica = KeysManager.getClavePublica();
        String message2 = "@" + user + " " + message;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        out.println(message_cifrado);
    }

    /**
     * Logs out from the chat server.
     */
    public void logout() {
        sendMessage("!logout");
    }

    @Override
    public void start(Stage arg0) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}
