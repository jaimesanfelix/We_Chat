package com.fct.we_chat;

import java.io.IOException;
import java.util.Scanner;

/**
 * ChatClientConsola is a console-based chat client that extends ChatClient.
 */
public class ChatClientConsola extends ChatClient {
    /**
     * The main method to start the console-based chat client.
     * @param args the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        ChatClientConsola clienteConsola = new ChatClientConsola();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce tu usuario: ");
        String usuario = sc.nextLine();
        
        nickname = usuario;
        if (!nickname.isEmpty()) {
            try {
                connectToServer(clienteConsola);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        String message = sc.nextLine();
        while (message != "!logout") {
            sendMessage(message);
            message = sc.nextLine();
        }
    }

    /**
     * Displays a message in the console.
     * @param message the message to display
     */
    @Override
    void mostrar(String message) {
        System.out.println(message); 
    }
}
