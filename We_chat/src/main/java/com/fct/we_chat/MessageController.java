package com.fct.we_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    @FXML
    private TextField tf_mensaje;
    private String usuario;
    private Worker worker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuario = MainController.usuario;
    }


    @FXML
    void enviar(ActionEvent event) throws Exception {
        String mensaje = tf_mensaje.getText();
        try {
            worker = new Worker(Worker.socketCliente, Worker.listaClientes);
            worker.contestarUsuario(usuario, mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
