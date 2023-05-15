import Controller.UserController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(15102)){

            do {
                //new UserController(server.accept()).run();
                Thread userThread = new Thread(new UserController(server.accept()));
                userThread.start();

            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}