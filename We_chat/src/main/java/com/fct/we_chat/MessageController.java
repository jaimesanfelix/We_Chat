package com.fct.we_chat;

import com.fct.we_chat.Models.Mensaje;
import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSASender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.ObjectOutputStream;
import java.net.*;
import java.security.Key;
import java.util.ResourceBundle;

public class MessageController extends ClienteSocket implements Initializable {

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
    private TableColumn<ClienteSocket, String> tc_usuario;

    @FXML
    private TableView<Mensaje> tv_mensajes;

    @FXML
    private Button tb_enviar;
    String frase;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuario = MainController.usuario;
        mensajes = FXCollections.observableArrayList(mensaje);
        tc_mensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        tv_mensajes.setItems(mensajes);
        tb_enviar.setOnAction(e -> {
            try {
                frase = tf_mensaje.getText();
                contestar(frase);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        //new Thread(() -> conect.conectar(ClienteSocket.socket)).start();
    }


    void inicializarMensaje(){
        mensaje = new Mensaje();
        mensaje.setMensaje(tf_mensaje.getText());
        mensajes.add(mensaje);
    }

    @Override
    void contestar(String frase) throws Exception {
        inicializarMensaje();
        super.contestar(frase);
    }
}
