package com.fct.we_chat;

import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSASender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.Key;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    @FXML
    private TextField tf_mensaje;
    private String usuario;
    private ObjectOutputStream salida;
    private Key clavePublica;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuario = MainController.usuario;
    }

    @FXML
    void enviar(ActionEvent event) throws Exception {
        String mensaje = tf_mensaje.getText();
        do {
            System.out.println(mensaje);
            salida = new ObjectOutputStream(WorkerCliente.socket.getOutputStream());
            clavePublica = KeysManager.getClavePublica();
            salida.writeObject(RSASender.cipher(mensaje, clavePublica));
        } while (!mensaje.contains("exit"));


    }
}
