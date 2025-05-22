package com.fct.we_chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController extends ClienteSocket{

    @FXML
    private TextField tf_usuario;
    private String usuario;

    @FXML
    void newUser(ActionEvent event) throws Exception {
        usuario = tf_usuario.getText();
        super.newUser(event, usuario);
    }
}
