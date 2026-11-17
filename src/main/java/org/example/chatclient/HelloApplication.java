package org.example.chatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.chatclient.enums.Command;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        stage.setTitle("Мережевий чат ПКІС (Лаб 04-09)");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Network net = Network.getInstance(null);
        if (net != null) {
            net.sendMessage(Command.FINISH.getValue());
            net.disconnect();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
