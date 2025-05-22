package com.fct.we_chat;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class MainController {

    @FXML
    private TextField tf_usuario;
    public static String usuario;


    @FXML
    void enviar(ActionEvent event) throws IOException {
        usuario = tf_usuario.getText();
        ScreenLoader.ScreenLoader("message-view.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    @FXML
    void enviarGlobal(ActionEvent event) throws IOException {
        ScreenLoader.ScreenLoader("message-view.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow());
    }


}
