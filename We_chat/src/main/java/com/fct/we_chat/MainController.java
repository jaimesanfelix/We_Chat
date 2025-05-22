package com.fct.we_chat;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController implements DataReceiver{

    @FXML
    private TextField tf_usuario;
    public static String usuario;
    public String usuario2;


    @FXML
    void enviar(ActionEvent event) throws IOException {
        usuario = tf_usuario.getText();
        ScreenLoader.ScreenLoader("message-view.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(),usuario);
    }

    @FXML
    void enviarGlobal(ActionEvent event) throws IOException {
        ScreenLoader.ScreenLoader("message-view.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(),this.usuario2);
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
            System.out.println("Dato recibido: " + data);
            // Aquí puedes usar la variable en la interfaz gráfica
            this.usuario2 = (String) data;
        }

    }



}
