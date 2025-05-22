package com.fct.we_chat;

import com.fct.we_chat.Models.Mensaje;
import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSASender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private ObservableList<Mensaje> mensajes;
    private Key clavePublica;
    private Mensaje mensaje;
    @FXML
    private TableColumn<Mensaje, String> tc_mensaje;

    @FXML
    private TableColumn<LoginController, String> tc_usuario;

    @FXML
    private TableView<Mensaje> tv_mensajes;

    @FXML
    private Button tb_enviar;
    Socket socket = new Socket();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuario = MainController.usuario;
        tb_enviar.setOnAction(e -> {
            try {
                enviar();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        //new Thread(() -> conectar(tf_mensaje)).start();
    }

    void inicializarMensaje(){
        mensaje = new Mensaje();
        mensaje.setMensaje(tf_mensaje.getText());
        mensajes = FXCollections.observableArrayList(mensaje);
        tc_mensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        tv_mensajes.setItems(mensajes);
    }


    void enviar() throws Exception {
        do {
            inicializarMensaje();
            salida = new ObjectOutputStream(socket.getOutputStream());
            clavePublica = KeysManager.getClavePublica();
            salida.writeObject(RSASender.cipher(mensaje.getMensaje(), clavePublica));
            //Platform.runLater(() -> tf_mensaje.appendText("mensaje" + "\n"));
        } while (!mensaje.getMensaje().contains("exit"));


    }
}
