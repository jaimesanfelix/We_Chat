package com.fct.we_chat;

import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSASender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;

public class LoginController {

    private static final String DNSAWS = "localhost";
    @FXML
    private TextField tf_usuario;

    private Socket socket;
    private ObjectOutputStream salida;
    private Key clavePublica;

    private String usuario;

    @FXML
    void newUser(ActionEvent event) throws Exception{

        socket = new Socket(DNSAWS, 11000);
        salida = new ObjectOutputStream(socket.getOutputStream());
        clavePublica = KeysManager.getClavePublica();

        usuario = tf_usuario.getText();
        String u1 = usuario.substring(0, 1).toUpperCase();
        String nombreUsuario = u1 + usuario.substring(1);
        salida.writeObject(RSASender.cipher(nombreUsuario, clavePublica));

        WorkerCliente wc = new WorkerCliente(socket, nombreUsuario);
        wc.start();

        ScreenLoader.ScreenLoader("main-view.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    void conectar(){

    }
}
