package com.fct.we_chat;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ScreenLoader {
    public static void ScreenLoader(String viewPath, Stage stage, Object data) throws IOException {
       // Parent view = FXMLLoader.load(ScreenLoader.class.getResource(viewPath));

        FXMLLoader loader = new FXMLLoader(ScreenLoader.class.getResource(viewPath));
        Parent view = loader.load(); // Cargar la vista FXML

        // Obtener el controlador del nuevo formulario
        Object controller = loader.getController();

        // Llamar al método setData si el controlador implementa DataReceiver
        if (controller instanceof DataReceiver) {
            ((DataReceiver) controller).setData(data);
        }

        Scene view1Scene = new Scene(view);
        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
    }
}
