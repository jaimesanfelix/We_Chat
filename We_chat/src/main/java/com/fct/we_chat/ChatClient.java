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

public class ChatClient{
    public final int RSA_BLOCK_SIZE = 256; // Tamaño de bloque para RSA con una clave de 2048 bits
    public PrintWriter out;

    public String nickname;
    public static Timestamp tiempoUsuario = new Timestamp(System.currentTimeMillis());
    Key clavePublica;
    public static String chat;

        public void connectToServer() {
            Key clavePrivada;
            try {
                Socket socket = new Socket("localhost", 12345);
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                
                String nickname_cifrado="";
                clavePublica = KeysManager.getClavePublica();
                clavePrivada = KeysManager.getClavePrivada();
                byte[] decryptedAESKeyBytes;
                
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                out = new PrintWriter(output, true);
    
                // Enviar nickname al servidor
                //Ciframos el nickname.
              
    
                //nickname_cifrado = new String(RSASender.cipher(nickname, clavePublica));
    
                nickname_cifrado = RSASender.encryptMessage(nickname,  clavePublica);
    
                //nickname_cifrado = encryptLargeData(nickname.getBytes(), clavePublica);
    
                //nickname_cifrado = Base64.getEncoder().encodeToString(encryptedMessage);
                //Lo enviamos al servidor cifrado.
                out.println(nickname_cifrado);
    
                // Hilo para escuchar mensajes del servidor
                new Thread(() -> {
                    String message;
                    String message_descifrado;              
                    try {
                        while ((message = reader.readLine()) != null) {
                         
                            message_descifrado = RSAReceiver.decryptMessage(message, clavePrivada);                     
                        //    message_descifrado = new String(RSASender.cipher(message, clavePrivada));
                        //CAMBIAR A ChatClienteFX
                        /*Comentar esta linea antes de lanzar ChatClientConsola*/ChatClientFX.chatArea.appendText(message_descifrado + "\n");
                        //chat = message_descifrado + "\n";
                        //messageSent = message_descifrado + "\n";
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

    public void sendMessage(String message) {
        String message_cifrado;
        if (!message.isEmpty()) {
            //message_cifrado = new String(RSASender.cipher(nickname, clavePublica));
            message_cifrado = RSASender.encryptMessage(message,  clavePublica);
            out.println(message_cifrado);
        }
    }

    public void logout() {
        sendMessage("!logout");
        // Este botón puede quedar opcional, ya que los mensajes llegan automáticamente
        //chatArea.appendText("Chat actualizado... \n");
        
    }

     
}
