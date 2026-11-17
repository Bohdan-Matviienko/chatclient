package org.example.chatclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.chatclient.enums.Command;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField msgField;

    @FXML
    private ListView<String> userListView;

    @FXML
    private Label onlineCountLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Network.getInstance(this).connect();

        // Клік по імені користувача у списку автоматично вставляє префікс приватного повідомлення (Лаб 08-09)
        if (userListView != null) {
            userListView.setOnMouseClicked(event -> {
                String selectedUser = userListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null && !selectedUser.equals(loginField.getText())) {
                    msgField.setText(Command.PRIVATE.getValue() + " " + selectedUser + " ");
                    msgField.requestFocus();
                    msgField.positionCaret(msgField.getText().length());
                }
            });
        }
    }

    @FXML
    public void onLogInClick() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();
        if (login.isEmpty() || pass.isEmpty()) {
            printMessage("Введіть логін та пароль!");
            return;
        }
        String msg = Command.LOGIN.getValue() + " " + login + " " + pass;
        Network.getInstance(this).sendMessage(msg);
    }

    @FXML
    public void onRegisterClick() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();
        if (login.isEmpty() || pass.isEmpty()) {
            printMessage("Для реєстрації введіть новий логін та пароль!");
            return;
        }
        String msg = Command.REGISTER.getValue() + " " + login + " " + pass;
        Network.getInstance(this).sendMessage(msg);
    }

    public void login(boolean log) {
        Platform.runLater(() -> {
            if (log) {
                this.loginField.setEditable(false);
                this.passwordField.setEditable(false);
                this.btnLogin.setDisable(true);
                if (this.btnRegister != null) {
                    this.btnRegister.setDisable(true);
                }
                printMessage("Ви приєдналися до чату як " + loginField.getText());
            } else {
                printMessage("Неправильний логін чи пароль (або користувач вже в мережі)");
            }
        });
    }

    public void registerResult(boolean success) {
        Platform.runLater(() -> {
            if (success) {
                printMessage("Реєстрація успішна! Тепер натисніть «Ввійти».");
            } else {
                printMessage("Помилка реєстрації: такий логін вже зайнятий.");
            }
        });
    }

    public void updateOnlineUsers(String[] users) {
        Platform.runLater(() -> {
            if (userListView != null) {
                userListView.getItems().setAll(users);
            }
            if (onlineCountLabel != null) {
                onlineCountLabel.setText("Онлайн (" + users.length + "):");
            }
        });
    }

    public synchronized void printMessage(String msg) {
        Platform.runLater(() -> chatArea.appendText(msg + "\n"));
    }

    @FXML
    public void sendMessage() {
        String msg = msgField.getText().trim();
        if (msg.isEmpty()) {
            return;
        }
        msgField.setText("");
        Network.getInstance(this).sendMessage(msg);
    }
}
