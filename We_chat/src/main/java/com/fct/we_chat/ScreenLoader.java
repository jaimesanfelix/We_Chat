package com.fct.we_chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenLoader {
    public static void ScreenLoader(String viewPath, Stage stage) throws IOException {
        Parent view = FXMLLoader.load(ScreenLoader.class.getResource(viewPath));
        Scene view1Scene = new Scene(view);
        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
    }
}
