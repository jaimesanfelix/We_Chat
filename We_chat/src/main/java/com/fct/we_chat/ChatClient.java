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
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import com.fct.we_chat.model.Group;
import com.fct.we_chat.model.Message;
import com.fct.we_chat.model.NicknamesByUser;
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
    public static String username;
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

            String username_cifrado = "";
            String password_cifrado = "";
            clavePublica = KeysManager.getClavePublica();
            clavePrivada = KeysManager.getClavePrivada();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            out = new PrintWriter(output, true);

            // Enviar username al servidor
            // Ciframos el username.

            username_cifrado = RSASender.encryptMessage(username, clavePublica);

            // Lo enviamos al servidor cifrado.
            // out.println(username_cifrado);
            dataOut.writeUTF(username_cifrado);

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

    // Añadimos un nickname al modelo NicknamesByUser
    protected void changeNickname(String target_user, String nickname, String user_connected) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // user -> hemos de obtener el target_user_id del usuario
            int target_user_id = getUserIdByUsername(target_user);
            int user_connected_id = getUserIdByUsername(user_connected);
            NicknamesByUser nicknamebyuser = new NicknamesByUser(nickname, target_user_id, user_connected_id);
            session.save(nicknamebyuser);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            System.out.println("Otro error inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }

    }

    protected void saveUserToDatabase(String username, String email, String password) throws Exception {
        Key clavePublica = KeysManager.getClavePublica();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Registrar al usuario con el tiempo actual
            String connectionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // ciframos la contraseña.
            String password_cifrado = RSASender.encryptMessage(password, clavePublica);

            User user = new User(username, email, password_cifrado, connectionTime);

            session.save(user);
            transaction.commit();

        } catch (ConstraintViolationException e) {
            if (transaction != null)
                transaction.rollback();
            System.out.println("¡Error! Ya existe un usuario con el mismo nombre o correo.");
            // Puedes también obtener el nombre de la restricción si lo necesitas:
            System.out.println("Restricción violada: " + e.getConstraintName());

        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            System.out.println("Otro error inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void sendMessage(String message) throws IOException {
        String message_cifrado;
        if (!message.equals("!logout")) {
            saveMessage(username, "0", message);
        }
        if (!message.isEmpty()) {
            // message_cifrado = new String(RSASender.cipher(username, clavePublica));
            message_cifrado = RSASender.encryptMessage(message, clavePublica);
            dataOut.writeUTF(message_cifrado);
            // out.println(message_cifrado);
        }
    }

    public void sendMessageToUser(String user, String message) throws Exception {
        // String message;
        saveMessage(username, user, message);
        clavePublica = KeysManager.getClavePublica();
        String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // String message1 = messageField.getText();
        String message2 = "@" + user + " " + message + ": " + sentTime;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        // out.println(message_cifrado);
        dataOut.writeUTF(message_cifrado);

    }

    public void sendMessageToGroup(String groupName, String message, String user) throws Exception {
        // String message;
        saveMessage(username, groupName, message);
        clavePublica = KeysManager.getClavePublica();
        // String message1 = messageField.getText();
        String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message2 = "#grupo " + groupName + " " + user + ": " + message + ": " + sentTime;
        String message_cifrado = RSASender.encryptMessage(message2, clavePublica);
        // out.println(message_cifrado);
        dataOut.writeUTF(message_cifrado);
    }

    protected int getUserRealIdByNickname(String user, String user_connected) {
        String userReal = user;
        Integer user_id;
        int user_real_id = getUserIdByUsername(user);
        int user_connected_id = getUserIdByUsername(user_connected);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                    "SELECT target_user_id FROM NicknamesByUser WHERE user_connected_id =:user_connected_id AND nickname = :user",
                    Integer.class);
            query.setParameter("user", user);
            query.setParameter("user_connected_id", user_connected_id);
            user_id = (int) query.uniqueResult();
            if (user_id == null) {
                return user_real_id;
            } else {
                return user_id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user_real_id;

    }

    private void saveMessage(String username_from, String username_to, String message) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        int grupo_id;
        int user_to_id;
        int user_from_id;
        // A la hora de grabar los mensajes, los tenemos que grabar con los id_reales

        try {
            transaction = session.beginTransaction();

            // Registrar al usuario con el tiempo actual
            // String connectionTime =
            // LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd
            // HH:mm:ss"));
            if (!isGroup(username_to) && username_to != "0") {
                grupo_id = 0;
                user_to_id = getUserIdByUsername(username_to);

                // user_to_id = getUserRealIdByNickname(username_to,username);

            } else if (isGroup(username_to)) {
                grupo_id = getGroupIdByUsername(username_to);
                user_to_id = 0;
            } else {
                grupo_id = 0;
                user_to_id = 0;
            }

            user_from_id = getUserIdByUsername(username_from);
            String sentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Message mensaje = new Message(user_to_id, user_from_id, message, grupo_id, sentTime);

            session.save(mensaje);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Cargar los mensajes de los usuarios guardados en la tabla mensajes
    protected List<Message> loadMessages(int to, int from, int group, String fromDate, String toDate) {
        List<Message> messages = new ArrayList<>();
        List<Message> loadedMessages = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Message> query;
            String filtroFecha = "";

            if (fromDate != null) {
                filtroFecha = " AND timestamp BETWEEN :fromDate AND :toDate ";
            }

            if (from == 0) {
                query = session.createQuery(
                        "FROM Message WHERE (user_to_id = :user_to_id AND group_id = :group_id) " +  filtroFecha + " ORDER BY timestamp", Message.class);
                query.setParameter("user_to_id", to);
                query.setParameter("group_id", group);
                if (fromDate != null) {
                    query.setParameter("fromDate", fromDate);
                    query.setParameter("toDate", toDate);
                }
            } else if (group > 0) {
                query = session.createQuery("FROM Message WHERE (group_id = :group_id) " +  filtroFecha + " ORDER BY timestamp", Message.class);
                query.setParameter("group_id", group);
                if (fromDate != null) {
                    query.setParameter("fromDate", fromDate);
                    query.setParameter("toDate", toDate);
                }
            } else {
                query = session.createQuery(
                        "FROM Message WHERE (user_from_id = :user_from_id AND user_to_id = :user_to_id AND group_id = :group_id) OR (user_from_id = :user_to_id AND user_to_id = :user_from_id AND group_id = :group_id) " +  filtroFecha + " ORDER BY timestamp", Message.class);
                query.setParameter("user_from_id", from);
                query.setParameter("user_to_id", to);
                query.setParameter("group_id", group);
                if (fromDate != null) {
                    query.setParameter("fromDate", fromDate);
                    query.setParameter("toDate", toDate);
                }
            }
            messages = query.list();
            loadedMessages.addAll(messages);
            /*for (int i = 0; i < messages.size(); i++) {
                loadedMessages.add(messages.get(i));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return loadedMessages;
    }

    protected Boolean HasNickname(String user) {

        int target_user_id = getUserIdByUsername(user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT nickname FROM NicknamesByUser WHERE target_user_id = :target_user_id",
                    String.class);
            query.setParameter("target_user_id", target_user_id);
            List<String> nicknames = query.list();
            if (nicknames.size() > 0)
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    protected List<String> getNicknamesByUser(String user) {
        List<String> nicknames = new ArrayList<>();
        int user_connected_id = getUserIdByUsername(user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT nickname FROM NicknamesByUser WHERE user_connected_id = :user_connected_id",
                    String.class);
            query.setParameter("user_connected_id", user_connected_id);
            nicknames = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nicknames;
    }

    protected static String getUserRealByNickname(String user, String user_connected) {
        String userReal = user;
        Integer user_id;
        // int target_user_id = getUserIdByUsername(user);
        int user_connected_id = getUserIdByUsername(user_connected);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Integer> query = session.createQuery(
                    "SELECT target_user_id FROM NicknamesByUser WHERE user_connected_id =:user_connected_id AND nickname = :user",
                    Integer.class);
            query.setParameter("user", user);
            query.setParameter("user_connected_id", user_connected_id);
            user_id = query.uniqueResult();
            if (user_id == null) {
                return userReal;
            } else {
                userReal = getUsernameById(user_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userReal;

    }

    protected static String getNicknameByUserName(String user, String user_connected) {
        String nickname = null;
        nickname = user;
        int target_user_id = getUserIdByUsername(user);
        int user_connected_id = getUserIdByUsername(user_connected);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT nickname FROM NicknamesByUser WHERE user_connected_id =:user_connected_id AND target_user_id = :target_user_id",
                    String.class);
            query.setParameter("target_user_id", target_user_id);
            query.setParameter("user_connected_id", user_connected_id);
            nickname = query.uniqueResult();
            if (nickname == null) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nickname;

    }

    protected static int getUserIdByUsername(String username) {
        Integer userId = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Integer> query = session.createQuery("SELECT u.id FROM User u WHERE u.username = :username",
                    Integer.class);
            query.setParameter("username", username);
            userId = query.uniqueResult();
            if (userId == null) {
                userId = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;

    }

    protected int getGroupIdByUsername(String username) {
        Integer groupId = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Integer> query = session.createQuery("SELECT u.id FROM Group u WHERE u.name = :name", Integer.class);
            query.setParameter("name", username);
            groupId = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupId;

    }

    protected static String getUsernameById(int userId) {
        String username = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery("SELECT u.username FROM User u WHERE u.id = :userId",
                    String.class);
            query.setParameter("userId", userId);
            username = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
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

    protected void sendFileToServer(File file, String user) {
        if (file != null) {
            try {
                // RSASender.encryptMessage(message, clavePublica);

                if (user == null) {
                    dataOut.writeUTF("FILE:");
                } else {
                    dataOut.writeUTF("FILE@:" + user + ":");
                }

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

    protected boolean validateLogin(String username, String password) throws Exception {
        Integer userId = 0;
        Key clavePublica = KeysManager.getClavePublica();
        // String password_cifrado = RSASender.encryptMessage(password, clavePublica);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = session.createQuery("SELECT u.password FROM User u WHERE u.username = :username",
                    String.class);
            query.setParameter("username", username);
            // query.setParameter("password", password_cifrado);
            String password_cifrado_bd = query.uniqueResult();
            if (password_cifrado_bd == null)
                return false;
            String password_descifrado_bd = RSAReceiver.decryptMessage(password_cifrado_bd,
                    KeysManager.getClavePrivada());
            // userId = query.uniqueResult();
            if (password_descifrado_bd.equals(password)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    protected static int getIdGroupByName(String name) {
        int grupo_id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Integer> query = session.createQuery("SELECT id FROM Group WHERE name = :name");
        query.setParameter("name", name);
        grupo_id = query.uniqueResult();
        session.close();
        return grupo_id;

    }

    protected static List<String> getUsersByGroup(String nameGroup){
        List<String> miembros = new ArrayList<>();
        int grupo_id = getIdGroupByName(nameGroup);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Integer> query = session.createQuery("SELECT user FROM UserByGroup WHERE group_id = :grupo_id");
        query.setParameter("grupo_id", grupo_id);
        List<Integer> ids = query.list();
        for (Integer id : ids) {
            String nomUser = getUsernameById(id);
            miembros.add(nomUser);
        }

        session.close();
        return miembros;

    }

    protected static List<Group> getGroupsByUser2(String username) {
        List<Group> grupos = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT id FROM User WHERE username = :username");
        query.setParameter("username", username);
        int target_user_id = (int) query.uniqueResult();
        // Con el id del usuario buscamos en todos los grupos que esté
        Query query2 = session.createQuery("SELECT group FROM UserByGroup WHERE user = :user");
        query2.setParameter("user", target_user_id);
        List<Integer> ids = query2.list();

        for (Integer id : ids) {
            Query query3 = session.createQuery("SELECT name FROM Group WHERE id = :id");
            query3.setParameter("id", id);
            String nombreGrupo = (String) query3.uniqueResult();
            String dateCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Group grupo1 = new Group(nombreGrupo, dateCreation);
            grupos.add(grupo1);
        }

        session.close();

        return grupos;

    }

    protected static ArrayList<String> getGroupsByUser(String username) {

        ArrayList<String> grupos = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT id FROM User WHERE username = :username");
        query.setParameter("username", username);
        int target_user_id = (int) query.uniqueResult();
        // Con el id del usuario buscamos en todos los grupos que esté
        Query query2 = session.createQuery("SELECT group FROM UserByGroup WHERE user = :user");
        query2.setParameter("user", target_user_id);
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


    protected static void saveOrUpdateGroup(String groupName, ObservableList<String> users) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        String dateCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        try {
            transaction = session.beginTransaction();
    
            // Verificar si el grupo ya existe
            Query<Group> groupQuery = session.createQuery("FROM Group WHERE name = :name", Group.class);
            groupQuery.setParameter("name", groupName);
            Group grupo_bd = groupQuery.uniqueResult();
    
            if (grupo_bd == null) {
                // Crear nuevo grupo
                grupo_bd = new Group(groupName, dateCreation);
                session.save(grupo_bd);
            }
    
            int grupoId = grupo_bd.getId();
    
            // Limpiar usuarios actuales del grupo si deseas "actualizar completamente"
            Query deleteQuery = session.createQuery("DELETE FROM UserByGroup WHERE id = :groupId");
            deleteQuery.setParameter("groupId", grupoId);
            deleteQuery.executeUpdate();
    
            for (String nickname : users) {
                // Aquí debes implementar correctamente esta función según tu lógica
               
                String userReal = getUserRealByNickname(nickname, username);
                Query<Integer> userIdQuery = session.createQuery("SELECT id FROM User WHERE username = :username", Integer.class);
                userIdQuery.setParameter("username", userReal);
                Integer targetUserId = userIdQuery.uniqueResult();
    
                if (targetUserId != null) {
                    UserByGroup userByGroup = new UserByGroup(targetUserId, grupoId, dateCreation);
                    session.save(userByGroup);
                } else {
                    System.out.println("No se encontró el usuario: " + userReal);
                }
            }
    
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    

    protected static void saveGroup(String grupo, ObservableList<String> users) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        String dateCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            transaction = session.beginTransaction();
            Group grupo_bd = new Group(grupo, dateCreation);
            int grupoId = (int) session.save(grupo_bd);
            for (String nickname : users) {
                System.out.println(nickname);

                // String userReal = getNicknameByUserName(nickname, username);
                String userReal = getUserRealByNickname(nickname, username);

                Query userId = session.createQuery("SELECT id FROM User WHERE username = :username");
                userId.setParameter("username", userReal);
                int target_user_id = (int) userId.uniqueResult();
                UserByGroup userByGroup = new UserByGroup(target_user_id, grupoId, dateCreation);
                session.saveOrUpdate(userByGroup);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                e.printStackTrace();
                transaction.rollback();
            }
        }
    }

    // Comprobacion de si el username seleccionado es un usuario o un grupo
    protected boolean isGroup(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT COUNT(*) FROM Group WHERE name = :name");
        query.setParameter("name", username);
        long count = (long) query.uniqueResult();
        session.close();
        return count > 0;
    }

}
