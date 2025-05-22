package com.fct.we_chat;

import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.Key;
import java.util.ResourceBundle;

import com.fct.we_chat.Models.Mensaje;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MessageController extends ClienteSocket implements Initializable, DataReceiver {

    @FXML
    private TextField tf_mensaje;
    @FXML
    private TextField tf_usuario;
    private Mensaje usuario;
    public String usuario2;
    private ObjectOutputStream salida;
    private ObservableList<Mensaje> mensajes;
    private ObservableList<Mensaje> usuarios;
    
    private Key clavePublica;
    private Mensaje mensaje;
    @FXML
    private TableColumn<Mensaje, String> tc_mensaje;

    @FXML
    private TableColumn<Mensaje, String> tc_usuario;

    @FXML
    private TableView<Mensaje> tv_mensajes;

    @FXML
    private Button tb_enviar;
    String frase;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //usuario = super.getUsuario();
        mensajes = FXCollections.observableArrayList(mensaje);
        usuarios = FXCollections.observableArrayList(usuario);
        tc_mensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        tc_usuario.setCellValueFactory(new PropertyValueFactory<>("from"));
        
        tv_mensajes.setItems(mensajes);
        tv_mensajes.setItems(usuarios);
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
        String mens = tf_mensaje.getText();
        mensaje = new Mensaje(mens, "Jai");
        mensajes.add(mensaje);
    }

    @Override
    void contestar(String frase) throws Exception {
        inicializarMensaje();
        super.contestar(frase);
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
