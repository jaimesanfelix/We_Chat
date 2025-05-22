package com.fct.we_chat;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;

public class ServidorSocket {
    
    private static final int PORT=11000;
    public static Timestamp tiempoServidor;
    private static HashMap<Socket, String> listaClientes = new HashMap<>();


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket;
        Socket clientSocket;
        MessageController messageController;
        ObjectInputStream entrada;
        
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server iniciado y escuchando en el puerto "+ PORT);
        System.out.println("Server esperando clientes...");
        tiempoServidor = new Timestamp(System.currentTimeMillis());
        while (true) {
            clientSocket = serverSocket.accept();
            listaClientes.put(clientSocket, null);
            Worker w = new Worker(clientSocket, listaClientes);
            //messageController = new MessageController(w);
            System.out.println("LISTA CLIENTES");
            System.out.println("lista:" + listaClientes);
            w.start();
            System.out.println("start");
        }
        
    }
}