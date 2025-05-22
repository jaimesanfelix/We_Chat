package com.fct.we_chat;

import java.util.Scanner;

public class ChatClientConsola {
    public static void main(String[] args) {
        ChatClient c = new ChatClient();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce tu usuario: ");
        String usuario = sc.nextLine();
        c.nickname = usuario;
        if (!c.nickname.isEmpty()) {
            try {
                c.connectToServer();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        String message = sc.nextLine();
        while (message != "!logout") {
            c.sendMessage(message);
            message = sc.nextLine();
        }
    }
}
