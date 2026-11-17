package org.example.chatclient;

import org.example.chatclient.enums.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Network ourInstance;
    private final Controller controller;
    private Socket socket;
    private DataOutputStream out;
    private final int port = 1220;
    private final String host = "localhost";

    public static Network getInstance(Controller controller) {
        if (ourInstance == null && controller != null) {
            ourInstance = new Network(controller);
        }
        return ourInstance;
    }

    private Network(Controller controller) {
        this.controller = controller;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());

            Thread readThread = new Thread(this::readMessage);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            controller.printMessage("Помилка підключення до сервера (" + host + ":" + port + "). Перевірте, чи запущено сервер.");
        }
    }

    public void readMessage() {
        String line;
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            while ((line = in.readUTF()) != null) {
                if (line.startsWith(Command.LOGIN_OK.getValue())) {
                    controller.login(true);
                } else if (line.startsWith(Command.LOGIN_FAIL.getValue())) {
                    controller.login(false);
                } else if (line.startsWith(Command.REG_OK.getValue())) {
                    controller.registerResult(true);
                } else if (line.startsWith(Command.REG_FAIL.getValue())) {
                    controller.registerResult(false);
                } else if (line.startsWith(Command.USER_LIST.getValue())) {
                    String usersPart = line.substring(Command.USER_LIST.getValue().length()).trim();
                    String[] users = usersPart.isEmpty() ? new String[0] : usersPart.split(" ");
                    controller.updateOnlineUsers(users);
                } else {
                    controller.printMessage(line);
                }
            }
        } catch (IOException e) {
            controller.printMessage("З'єднання з сервером завершено.");
        }
    }

    public void sendMessage(String msg) {
        try {
            if (out != null) {
                out.writeUTF(msg);
                out.flush();
                System.out.println("Sent: " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
