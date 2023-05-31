import Controller.UserController;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(15102)){

            do {
                Thread userThread = new Thread(new UserController(server.accept()));
                userThread.start();
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}