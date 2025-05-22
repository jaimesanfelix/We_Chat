package com.fct.we_chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSAReceiver;
import com.fct.we_chat.utils.RSASender;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    // static HashMap<PrintWriter, String> listaClientes;
    private static HashMap<PrintWriter, String> listaClientes = new HashMap<>();
    public static Timestamp tiempoServidor;
    private static List<String> users = new ArrayList<>(); // ArrayList de usuarios
    private static Map<String, List<PrintWriter>> groupMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor de chat iniciado...");
        tiempoServidor = new Timestamp(System.currentTimeMillis());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String nickname;
        private String nickname_cifrado;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        Key clavePublica;
        Key clavePrivada;
        String[] listaComandos = { "!ping", "@user", "!userList", "!logout", "!userTime", "!serverTime",
                "!listaComandos" };
        Timestamp tiempoUsuario;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                clavePublica = KeysManager.getClavePublica();
                clavePrivada = KeysManager.getClavePrivada();
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try (
                    InputStream input = socket.getInputStream();
                    OutputStream output = socket.getOutputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    PrintWriter writer = new PrintWriter(output, true)) {
                this.out = writer;

                this.tiempoUsuario = new Timestamp(System.currentTimeMillis());

                // Leer el nickname del cliente
                nickname_cifrado = reader.readLine();

                nickname = RSAReceiver.decryptMessage(nickname_cifrado, clavePrivada);

                listaClientes.put(writer, nickname);

                users.add(nickname);

                synchronized (clientWriters) {
                    clientWriters.add(writer);
                }
                broadcast("[" + nickname + "] se ha unido al chat");
                // broadcast( nickname + " se ha unido al chat");

                String listaUsuarios = usersToString();
                broadcast("USERS:" + listaUsuarios);

                // Leer mensajes del cliente
                String message;
                String message_descifrado;
                while ((message = reader.readLine()) != null) {

                    message_descifrado = RSAReceiver.decryptMessage(message, clavePrivada);

                    if (message_descifrado.startsWith("!") || message_descifrado.startsWith("@")) {
                        ejecutarComandos(message_descifrado);
                    }else if(message_descifrado.startsWith("FILE:")){
                        receiveFile(message.substring(5));
                    }else{
                        broadcast(nickname + ": " + message_descifrado);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error con el cliente: " + e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    // broadcast( nickname + " ha salido del chat");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                broadcast(nickname + " ha salido del chat");
            }
        }

        // Método para convertir el ArrayList de usuarios en una cadena separada por
        // comas
        private String usersToString() {
            return String.join(",", users);
        }

        private void ejecutarComandos(String comando) throws Exception {

            String mensaje;
            // Eliminamos los espacios al inicio y al final de la frase
            comando = comando.trim();
            if (comando.startsWith("!ping")) {
                mensaje = comando.substring(comando.indexOf(" ") + 1);
                broadcast("**" + mensaje.toUpperCase() + "**");

            } else if (comando.startsWith("@")) {
                String user = comando.substring(1, comando.indexOf(" "));
                mensaje = comando.substring(comando.indexOf(" ") + 1);
                PrintWriter write = obtenerWriterPorNick(user);
                sendToClient(nickname + ": " + mensaje, write);
            } else if (comando.startsWith("!userTime")) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                mensaje = "Llevas conectado " + (timestamp.getTime() - tiempoUsuario.getTime()) / 1000.0 + " segundos";
                PrintWriter writer_user_actual = obtenerWriterPorNick(nickname);
                sendToClient(mensaje, writer_user_actual);
            } else if (comando.startsWith("!serverTime")) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                mensaje = "El servidor lleva activo " + (timestamp.getTime() - tiempoServidor.getTime()) / 1000.0
                        + " segundos";
                PrintWriter writer_user_actual = obtenerWriterPorNick(nickname);
                sendToClient(mensaje, writer_user_actual);
            } else if (comando.startsWith("!userList")) {
                mensaje = "";
                for (PrintWriter cliente : listaClientes.keySet()) {
                    mensaje += listaClientes.get(cliente) + ", ";
                }
                broadcast(mensaje.substring(0, mensaje.length() - 2));
            } else if (comando.startsWith("!listaComandos")) {
                String listaAEnviar = "";
                for (int i = 0; i < listaComandos.length; i++) {
                    listaAEnviar += listaComandos[i] + ", ";
                }
                broadcast(listaAEnviar.substring(0, listaAEnviar.length() - 2));
            } else if (comando.startsWith("!logout")) {
                PrintWriter writerUser = null;
                // String user = comando.substring(comando.indexOf(" ") + 1);
                System.out.println("-" + nickname + "-");
                for (PrintWriter cliente : listaClientes.keySet()) {
                    if (listaClientes.get(cliente).equals(nickname)) {
                        writerUser = cliente;
                        System.out.println("-" + listaClientes.get(cliente) + "-");
                    }
                }
                if (writerUser == null) {
                    PrintWriter writer_user_actual = obtenerWriterPorNick(nickname);
                    sendToClient("El usuario " + nickname + " no existe", writer_user_actual);
                } else {
                    broadcast("El usuario " + nickname + " va a ser eliminado");
                    // sendToClient("exit", writerUser);
                    listaClientes.remove(writerUser);
                    socket.close();
                }

            } else if (comando.startsWith("!addGrupo")) {
                HashMap<PrintWriter, String> usuariosGrupo = new HashMap<>();
                String user = comando.substring(1, comando.indexOf(" "));
                for (PrintWriter cliente : listaClientes.keySet()) {
                    PrintWriter writerUser = null;
                    if (listaClientes.get(cliente).equals(user)) {
                        PrintWriter write = obtenerWriterPorNick(user);
                        usuariosGrupo.put(write, user);
                    }
                    System.out.println(usuariosGrupo);
                }
            }

            else if (comando.startsWith("#grupo ")) {
                handleGroupMessage(comando);
            } else if (comando.startsWith("!crearGrupo ")) {
                handleCreateGroup(comando);
            }

            else {
                mensaje = "El comando " + comando + " es desconocido";
                PrintWriter writer_user_actual = obtenerWriterPorNick(nickname);
                sendToClient(mensaje, writer_user_actual);
            }

        }

        private void handleCreateGroup(String message) {
            String[] parts = message.split(" ", 2);
            if (parts.length < 2)
                return;

            String groupName = parts[1];
            synchronized (groupMap) {
                groupMap.putIfAbsent(groupName, new ArrayList<>());
                groupMap.get(groupName).add(out);
            }
            out.println("Grupo '" + groupName + "' creado y te has unido.");
        }

        private void handleGroupMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3)
                return;

            String groupName = parts[1];
            String groupMessage = parts[2];

            synchronized (groupMap) {
                if (groupMap.containsKey(groupName)) {
                    for (PrintWriter writer : groupMap.get(groupName)) {
                        writer.println("[Grupo " + groupName + "] " + nickname + ": " + groupMessage);
                    }
                } else {
                    out.println("El grupo '" + groupName + "' no existe.");
                }
            }
        }

        private void broadcast(String message) {
            String message_cifrado;
            System.out.println(message);

            message_cifrado = RSASender.encryptMessage(message, clavePublica);

            // message_cifrado = message;
            // message_descifrado = decryptMessage(message, clavePrivada);
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message_cifrado);
                }
            }
        }

        private void sendToClient(String message, PrintWriter clientWriter) {
            String message_cifrado;

            PrintWriter writer_user_actual = obtenerWriterPorNick(nickname);

            System.out.println("Enviando mensaje a un cliente: " + message);

            // Cifrar el mensaje utilizando la clave pública del cliente
            message_cifrado = RSASender.encryptMessage(message, clavePublica);

            // Enviar el mensaje cifrado de forma segura al cliente específico
            synchronized (clientWriter) {
                clientWriter.println(message_cifrado);
                writer_user_actual.println(message_cifrado);
            }
        }

        private void receiveFile(String fileName) {
            try {
                File file = new File("uploads/" + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dataIn.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
               
                // Verificar si el archivo es una imagen
                if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    broadcast("Imagen recibida: " + fileName + " [Ver en uploads]");
                } else {
                    broadcast("Archivo recibido: " + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static PrintWriter obtenerWriterPorNick(String nick) {
        for (Map.Entry<PrintWriter, String> entry : listaClientes.entrySet()) {
            if (entry.getValue().equals(nick)) {
                return entry.getKey();
            }
        }
        return null; // Si no se encuentra el nick
    }

     

}
