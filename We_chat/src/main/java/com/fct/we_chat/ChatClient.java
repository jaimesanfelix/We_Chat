package com.fct.we_chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class ChatClient extends Application {
    public final int RSA_BLOCK_SIZE = 256; // Tamaño de bloque para RSA con una clave de 2048 bits
    public static PrintWriter out;
    public static ListView<String> userList = new ListView<String>(); // Lista de
    static private DataOutputStream dataOut;
    private DataInputStream dataIn;

    public static ListView<String> getUserList() {
        return userList;
    }

    /*
     * public void setUserList(String[] userList) {
     * this.userList = userList;
     * }
     */

    // usuarios conectados
    public static String nickname;
    public static Timestamp tiempoUsuario = new Timestamp(System.currentTimeMillis());
    static Key clavePublica;
    public static String chat;

    void mostrar(String message) {
        // método en la clase padre
        System.out.println(message);
    };

    void llamarAMostrar(String message) {
        System.out.println("llamnado al método desde la clase Padre...");
        mostrar(message);
    }

    public static void connectToServer(ChatClient clientInstance) {
        Key clavePrivada;
        try {
            Socket socket = new Socket("localhost", 12345);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            dataOut = new DataOutputStream(socket.getOutputStream());

            String nickname_cifrado = "";
            clavePublica = KeysManager.getClavePublica();
            clavePrivada = KeysManager.getClavePrivada();
            byte[] decryptedAESKeyBytes;

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            out = new PrintWriter(output, true);

            // Enviar nickname al servidor
            // Ciframos el nickname.

            // nickname_cifrado = new String(RSASender.cipher(nickname, clavePublica));

            nickname_cifrado = RSASender.encryptMessage(nickname, clavePublica);

            // nickname_cifrado = encryptLargeData(nickname.getBytes(), clavePublica);

            // nickname_cifrado = Base64.getEncoder().encodeToString(encryptedMessage);
            // Lo enviamos al servidor cifrado.
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Error al conectar al servidor: " + e.getMessage());
        }
    }

    /*
     * public void updateUserList(String[] users) {
     * ChatClientFX.userList.getItems().setAll(users);
     * }
     */

    private static void updateUserList(String[] users) {
        ChatClientFX.userList.getItems().setAll(users);
    }

    public static void sendMessage(String message) {
        String message_cifrado;
        if (!message.isEmpty()) {
            // message_cifrado = new String(RSASender.cipher(nickname, clavePublica));
            message_cifrado = RSASender.encryptMessage(message, clavePublica);
            out.println(message_cifrado);
        }
    }

    public void sendMessageToUser(String user, String message) throws Exception {
        // String message;
        clavePublica = KeysManager.getClavePublica();
        // String message1 = messageField.getText();
        String message2 = "@" + user + " " + message;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        out.println(message_cifrado);

    }

    public void logout() {
        sendMessage("!logout");
        // Este botón puede quedar opcional, ya que los mensajes llegan automáticamente
        // chatArea.appendText("Chat actualizado... \n");

    }

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    protected void sendFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
 
        if (file != null) {
            try {
                dataOut.writeUTF("FILE:");
                dataOut.writeUTF(file.getName());
                dataOut.writeLong(file.length());
                System.out.println("Archivo enviado: " + file.getName() + "\n");
 
                FileInputStream fileIn = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                fileIn.close();
               
                //chatArea.appendText("Archivo enviado: " + file.getName() + "\n");
            } catch (IOException e) {
                System.out.println("Error enviando archivo: " + e.getMessage());
            }finally {
                try {
                    if (dataOut != null) {
                        dataOut.flush();
                    }
                } catch (IOException e) {
                    System.out.println("Error flushing dataOut: " + e.getMessage());
                }
            }
        }
    }

}
