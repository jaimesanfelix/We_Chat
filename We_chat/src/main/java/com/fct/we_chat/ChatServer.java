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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.fct.we_chat.model.Message;
import com.fct.we_chat.utils.HibernateUtil;
import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSAReceiver;
import com.fct.we_chat.utils.RSASender;



public class ChatServer {
    private static final int PORT = 12345;
    private static final String UPLOADS_DIR = "uploads"; // Carpeta donde se guardarán los archivos
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Set<ClientHandler> clients = new HashSet<>();
    //private static HashMap<PrintWriter, String> listaClientes;
    private static HashMap<PrintWriter, String> listaClientes = new HashMap<>();
    private static HashMap<ClientHandler, String> listaClients = new HashMap<>();
    public static Timestamp tiempoServidor;
    private static List<String> users = new ArrayList<>(); // ArrayList de usuarios
    private static Map<String, List<PrintWriter>> groupMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor de chat iniciado...");
        tiempoServidor = new Timestamp(System.currentTimeMillis());

        // Crear la carpeta "uploads" si no existe
        File uploadDir = new File(UPLOADS_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();

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
        private String password;
        private String password_cifrado;
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
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true);
                ClientHandler client = new ClientHandler(socket);
                this.out = writer;
                this.dataOut = dataOut;

                this.tiempoUsuario = new Timestamp(System.currentTimeMillis());

                // Leer el nickname del cliente
                //nickname_cifrado = reader.readLine();
                nickname_cifrado = dataIn.readUTF();

                nickname = RSAReceiver.decryptMessage(nickname_cifrado, clavePrivada);


                // Leer el password del cliente
                //nickname_cifrado = reader.readLine();
                password_cifrado = dataIn.readUTF();

                password = RSAReceiver.decryptMessage(password_cifrado, clavePrivada);


             //   saveUserToDatabase(nickname,password);

                listaClientes.put(writer, nickname);

                listaClients.put(this, nickname);

                users.add(nickname);

                synchronized (clientWriters) {
                    clientWriters.add(writer);
                }

                synchronized (clients) {
                    clients.add(this);
                }


                broadcast("[" + nickname + "] se ha unido al chat",null);
                // broadcast( nickname + " se ha unido al chat");

                String listaUsuarios = usersToString();
                broadcast("USERS:" + listaUsuarios,null);

                // Leer mensajes del cliente
                String message;
                String message_descifrado;
                // while ((message = reader.readLine()) != null) {
                while ((message = dataIn.readUTF()) != null) {

                    if (message.startsWith("FILE:")) {
                        String fileName = dataIn.readUTF();
                        long fileSize = dataIn.readLong();

                        byte[] buffer = new byte[4096];
                        try (FileOutputStream fileOut = new FileOutputStream("uploads/" + fileName)) {
                            int bytesRead;
                            while (fileSize > 0 && (bytesRead = dataIn.read(buffer, 0,
                                    (int) Math.min(buffer.length, fileSize))) != -1) {
                                fileOut.write(buffer, 0, bytesRead);
                                fileSize -= bytesRead;
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            System.out.println("Error: " + fileName + " error: " + e.getMessage());
                        }
                        broadcast("[" + nickname + "] envió un archivo: " + fileName,fileName);

                    }

                    message_descifrado = RSAReceiver.decryptMessage(message, clavePrivada);
                    if (message_descifrado != null) {
                        if (message_descifrado.startsWith("!") || message_descifrado.startsWith("@")) {
                            ejecutarComandos(message_descifrado);
                        } else if (message_descifrado.startsWith("FILE:")) {
                            receiveFile(message.substring(5));
                        } else {
                            broadcast(nickname + ": " + message_descifrado,null);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error con el cliente o cliente desconectado: " + e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    // broadcast( nickname + " ha salido del chat");
                    socket.close();
                    System.out.println("Conexión cerrada con el cliente.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                broadcast(nickname + " ha salido del chat",null);
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
                broadcast("**" + mensaje.toUpperCase() + "**",null);

            } else if (comando.startsWith("@")) {
                String user = comando.substring(1, comando.indexOf(" "));
                mensaje = comando.substring(comando.indexOf(" ") + 1);
                PrintWriter write = obtenerWriterPorNick(user);
                //ClientHandler client = obtenerClientPorNick(user);
                ClientHandler client = buscarPorNickname(user);
                //sendToClient(nickname + ": " + mensaje, write);
                sendToClientHandler(mensaje, client);
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
                broadcast(mensaje.substring(0, mensaje.length() - 2),null);
            } else if (comando.startsWith("!listaComandos")) {
                String listaAEnviar = "";
                for (int i = 0; i < listaComandos.length; i++) {
                    listaAEnviar += listaComandos[i] + ", ";
                }
                broadcast(listaAEnviar.substring(0, listaAEnviar.length() - 2),null);
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
                    broadcast("El usuario " + nickname + " va a ser eliminado",null);
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

        private void broadcast(String message, String fileName) {
            String message_cifrado;
            System.out.println(message);

            message_cifrado = RSASender.encryptMessage(message, clavePublica);

            /* 
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                        writer.println(message_cifrado);
                }
            }
            */
            synchronized (clients) {
                for (ClientHandler client: clients) {
                    try {
                        client.dataOut.writeUTF(message_cifrado);
                        if (fileName != null) {
                            String archivo_cifrado = RSASender.encryptMessage("FILE:" + fileName, clavePublica);
                            client.dataOut.writeUTF(archivo_cifrado);

                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
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


        private void sendToClientHandler(String message, ClientHandler client) throws IOException {
            String message_cifrado;

            ClientHandler client_user_actual = obtenerClientPorNick(nickname);

            System.out.println("Enviando mensaje a un cliente: " + message);

            // Cifrar el mensaje utilizando la clave pública del cliente
            message_cifrado = RSASender.encryptMessage(message, clavePublica);

            client_user_actual.dataOut.writeUTF(message_cifrado);
            client.dataOut.writeUTF(message_cifrado);

            // Enviar el mensaje cifrado de forma segura al cliente específico
        /*     synchronized (clientWriter) {
                clientWriter.println(message_cifrado);
                writer_user_actual.println(message_cifrado);
            } */
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
                    broadcast("Imagen recibida: " + fileName + " [Ver en uploads]",fileName);
                } else {
                    broadcast("Archivo recibido: " + fileName, fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveMessage(String nickname_from, String nickname_to, String message) {

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
               
                // Registrar al usuario con el tiempo actual
                //String connectionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                
                int user_to_id = getUserIdByNickname(nickname_to);
                int user_from_id = getUserIdByNickname(nickname_from);

                Message mensaje = new Message(user_to_id, user_from_id, message);                
               
                session.save(mensaje);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }


        }

        private int getUserIdByNickname(String nickname) {
            Integer userId = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Integer> query = session.createQuery("SELECT u.id FROM User u WHERE u.nickname = :nickname", Integer.class);
                query.setParameter("nickname", nickname);
                userId = query.uniqueResult();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userId;

        }


    /*    private void saveUserToDatabase(String nickname, String password) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
               
                // Registrar al usuario con el tiempo actual
                String connectionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //ciframos la contraseña.
                String password_cifrado = RSASender.encryptMessage(password, clavePublica);

                User user = new User(nickname, password_cifrado, connectionTime);
               
                session.save(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
    */

    }

    public static PrintWriter obtenerWriterPorNick(String nick) {
        for (Map.Entry<PrintWriter, String> entry : listaClientes.entrySet()) {
            if (entry.getValue().equals(nick)) {
                return entry.getKey();
            }
        }
        return null; // Si no se encuentra el nick
    }

    public static ClientHandler obtenerClientPorNick(String nick) {
        for (Map.Entry<ClientHandler, String> entry : listaClients.entrySet()) {
            if (entry.getValue().equals(nick)) {
                return entry.getKey();
            }
        }
        return null; // Si no se encuentra el nick
    }

    public static ClientHandler buscarPorNickname(String nickname) {
        for (Map.Entry<ClientHandler, String> entry : listaClients.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(nickname)) {
                return entry.getKey(); // Devuelve el ClientHandler asociado al nickname
            }
        }
        return null; // Si no se encuentra, retorna null
    }


}
