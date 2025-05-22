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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.fct.we_chat.model.Group;
import com.fct.we_chat.model.Message;
import com.fct.we_chat.model.User;
import com.fct.we_chat.model.UserByGroup;
import com.fct.we_chat.utils.HibernateUtil;
import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSAReceiver;
import com.fct.we_chat.utils.RSASender;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class ChatClient extends Application {
    public final int RSA_BLOCK_SIZE = 256; // Tamaño de bloque para RSA con una clave de 2048 bits
    public static PrintWriter out;
    public static ListView<String> userList = new ListView<String>(); // Lista de
    static private DataOutputStream dataOut;
    static private DataInputStream dataIn;

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
    public static String password;
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
            dataIn = new DataInputStream(socket.getInputStream());

            String nickname_cifrado = "";
            String password_cifrado = "";
            clavePublica = KeysManager.getClavePublica();
            clavePrivada = KeysManager.getClavePrivada();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            out = new PrintWriter(output, true);

            // Enviar nickname al servidor
            // Ciframos el nickname.

            nickname_cifrado = RSASender.encryptMessage(nickname, clavePublica);

            // Lo enviamos al servidor cifrado.
            // out.println(nickname_cifrado);
            dataOut.writeUTF(nickname_cifrado);

            // Enviamos password al servidor
            // ciframos el password
            password_cifrado = RSASender.encryptMessage(password, clavePublica);
            dataOut.writeUTF(password_cifrado);

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                String message;
                String message_descifrado;
                try {
                    // while ((message = reader.readLine()) != null) {
                    while ((message = dataIn.readUTF()) != null) {

                        message_descifrado = RSAReceiver.decryptMessage(message, clavePrivada);

                        if (message_descifrado != null) {

                            clientInstance.mostrar(message_descifrado);

                            System.out.println(message_descifrado);
                        }
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

    protected void saveUserToDatabase(String nickname, String email, String password) throws Exception {
        Key clavePublica = KeysManager.getClavePublica();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Registrar al usuario con el tiempo actual
            String connectionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // ciframos la contraseña.
            String password_cifrado = RSASender.encryptMessage(password, clavePublica);

            User user = new User(nickname, email, password_cifrado, connectionTime);

            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void sendMessage(String message) throws IOException {
        String message_cifrado;
        saveMessage(nickname, "0", message);
        if (!message.isEmpty()) {
            // message_cifrado = new String(RSASender.cipher(nickname, clavePublica));
            message_cifrado = RSASender.encryptMessage(message, clavePublica);
            dataOut.writeUTF(message_cifrado);
            // out.println(message_cifrado);
        }
    }

    public void sendMessageToUser(String user, String message) throws Exception {
        // String message;
        saveMessage(nickname, user, message);
        clavePublica = KeysManager.getClavePublica();
        // String message1 = messageField.getText();
        String message2 = "@" + user + " " + message;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        // out.println(message_cifrado);
        dataOut.writeUTF(message_cifrado);

    }

    public void sendMessageToGroup(String user, String message) throws Exception {
        // String message;
        saveMessage(nickname, user, message);
        clavePublica = KeysManager.getClavePublica();
        // String message1 = messageField.getText();
        //String message2 = "#grupo " + user + " " + message;
        String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message2 = message + ": "  + sentTime;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        // out.println(message_cifrado);
        dataOut.writeUTF(message_cifrado);

    }

    private void saveMessage(String nickname_from, String nickname_to, String message) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        int grupo_id;
        int user_to_id;
        int user_from_id;
        try {
            transaction = session.beginTransaction();
           
            // Registrar al usuario con el tiempo actual
            //String connectionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (!isGroup(nickname_to) && nickname_to != "0") {
                grupo_id = 0;
                user_to_id = getUserIdByNickname(nickname_to);
            }else if(isGroup(nickname_to)){
                grupo_id = getGroupIdByNickname(nickname_to);
                user_to_id = 0;
            }else{
                grupo_id = 0;
                user_to_id = 0;
            }

            user_from_id = getUserIdByNickname(nickname_from);
            String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            
            Message mensaje = new Message(user_to_id, user_from_id, message, grupo_id, sentTime);                
           
            session.save(mensaje);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    //Cargar los mensajes de los usuarios guardados en la tabla mensajes
    protected List<Message> loadMessages(int to, int from, int group) {
        List<Message> messages = new ArrayList<>();
        List<Message> loadedMessages = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Message> query;
            if (from == 0) {
                query = session.createQuery("FROM Message WHERE (user_to_id = :user_to_id AND group_id = :group_id) ORDER BY timestamp");    
                query.setParameter("user_to_id", to);
                query.setParameter("group_id", group);
            }else if(group > 0){
                query = session.createQuery("FROM Message WHERE (group_id = :group_id) ORDER BY timestamp");
                query.setParameter("group_id", group);
            }else{
                query = session.createQuery("FROM Message WHERE (user_from_id = :user_from_id AND user_to_id = :user_to_id AND group_id = :group_id) OR (user_from_id = :user_to_id AND user_to_id = :user_from_id AND group_id = :group_id) ORDER BY timestamp");
                query.setParameter("user_from_id", from);
                query.setParameter("user_to_id", to);
                query.setParameter("group_id", group);
            }
            messages = query.list();
            for (int i = 0; i < messages.size(); i++) {
                loadedMessages.add(messages.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return loadedMessages;
    }

    protected int getUserIdByNickname(String nickname) {
        Integer userId = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Integer> query = session.createQuery("SELECT u.id FROM User u WHERE u.nickname = :nickname", Integer.class);
            query.setParameter("nickname", nickname);
            userId = query.uniqueResult();
            if (userId == null){
                userId = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;

    }

    protected int getGroupIdByNickname(String nickname) {
        Integer groupId = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Integer> query = session.createQuery("SELECT u.id FROM Group u WHERE u.name = :name", Integer.class);
            query.setParameter("name", nickname);
            groupId = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupId;

    }

    protected String getNicknameById(int userId) {
        String nickname = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery("SELECT u.nickname FROM User u WHERE u.id = :userId", String.class);
            query.setParameter("userId", userId);
            nickname = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public void logout() throws IOException {
        sendMessage("!logout");
        // Este botón puede quedar opcional, ya que los mensajes llegan automáticamente
        // chatArea.appendText("Chat actualizado... \n");

    }

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    protected void sendFileToServer(File file) {
        if (file != null) {
            try {
                // RSASender.encryptMessage(message, clavePublica);

                // dataOut.writeUTF("FILE:");
                // dataOut.writeUTF(file.getName());
                dataOut.writeUTF("FILE:");
                dataOut.writeUTF(file.getName());

                dataOut.writeLong(file.length());

                // dataOut.writeUTF(RSASender.encryptMessage("FILE:", clavePublica));
                // dataOut.writeUTF(RSASender.encryptMessage(file.getName(), clavePublica));
                // dataOut.writeLong(file.length());

                System.out.println("Archivo enviado: " + file.getName() + "\n");

                FileInputStream fileIn = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                fileIn.close();
                System.out.println("Archivo enviado 2: " + file.getName() + "\n");
                // chatArea.appendText("Archivo enviado: " + file.getName() + "\n");
            } catch (IOException e) {
                System.out.println("Error enviando archivo: " + e.getMessage());
            } finally {
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

    protected void sendFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                // RSASender.encryptMessage(message, clavePublica);

                // dataOut.writeUTF("FILE:");
                // dataOut.writeUTF(file.getName());
                dataOut.writeUTF("FILE:");
                dataOut.writeUTF(file.getName());

                dataOut.writeLong(file.length());

                // dataOut.writeUTF(RSASender.encryptMessage("FILE:", clavePublica));
                // dataOut.writeUTF(RSASender.encryptMessage(file.getName(), clavePublica));
                // dataOut.writeLong(file.length());

                System.out.println("Archivo enviado: " + file.getName() + "\n");

                FileInputStream fileIn = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                fileIn.close();
                System.out.println("Archivo enviado 2: " + file.getName() + "\n");
                // chatArea.appendText("Archivo enviado: " + file.getName() + "\n");
            } catch (IOException e) {
                System.out.println("Error enviando archivo: " + e.getMessage());
            } finally {
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


    protected boolean validateLogin(String nickname, String password) throws Exception {
            Integer userId = 0;
            Key clavePublica = KeysManager.getClavePublica();
            //String password_cifrado = RSASender.encryptMessage(password, clavePublica);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<String> query = session.createQuery("SELECT u.password FROM User u WHERE u.nickname = :nickname", String.class);
                query.setParameter("nickname", nickname);
                //query.setParameter("password", password_cifrado);
                String password_cifrado_bd = query.uniqueResult();
                if (password_cifrado_bd == null)
                    return false;
                String password_descifrado_bd = RSAReceiver.decryptMessage(password_cifrado_bd, KeysManager.getClavePrivada());
                //userId = query.uniqueResult();
                if(password_descifrado_bd.equals(password)){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    protected static ArrayList<String> getGroupsByUser(String nickname) {

        ArrayList<String> grupos = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT id FROM User WHERE nickname = :nickname");
        query.setParameter("nickname", nickname);
        int user_id = (int)query.uniqueResult();
        //Con el id del usuario buscamos en todos los grupos que esté
        Query query2 = session.createQuery("SELECT group FROM UserByGroup WHERE user = :user");
        query2.setParameter("user", user_id);
        List<Integer> ids = query2.list();

        for (Integer id : ids) {
            Query query3 = session.createQuery("SELECT name FROM Group WHERE id = : id");
            query3.setParameter("id", id);
            String nombreGrupo = query3.uniqueResult().toString();
            grupos.add(nombreGrupo);
        }

        session.close();
                
        return grupos;

    } 

    protected static void saveGroup(String grupo, ObservableList<String> users){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        String dateCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            transaction = session.beginTransaction();
            Group grupo_bd = new Group(grupo, dateCreation);
            int grupoId = (int)session.save(grupo_bd);
            for (String nickname : users) {
                System.out.println(nickname);
                Query userId = session.createQuery("SELECT id FROM User WHERE nickname = :nickname");
                userId.setParameter("nickname", nickname);
                int user_id = (int)userId.uniqueResult();
                UserByGroup userByGroup = new UserByGroup(user_id, grupoId, dateCreation);
                session.save(userByGroup);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                e.printStackTrace();
                transaction.rollback();
            }
        }
    }

    //Comprobacion de si el nickname seleccionado es un usuario o un grupo
    protected boolean isGroup(String nickname) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT COUNT(*) FROM Group WHERE name = :name");
        query.setParameter("name", nickname);
        long count = (long) query.uniqueResult();
        session.close();
        return count > 0;
    }
        
}

