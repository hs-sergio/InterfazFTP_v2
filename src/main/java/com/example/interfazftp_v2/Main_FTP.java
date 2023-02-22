package com.example.interfazftp_v2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main_FTP extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main_FTP.class.getResource("GUI_FTP.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 660);
        stage.setTitle("Cliente FTP!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}