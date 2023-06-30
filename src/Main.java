import сontroller.UserController;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        int port = 15102;
        String keystorePath = "keystore.jks";
        String keystorePassword = "07111917";

        KeyStore keystore = KeyStore.getInstance("JKS");
        FileInputStream keyStoreFile = new FileInputStream(keystorePath);
        keystore.load(keyStoreFile , keystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

        try (ServerSocket server = sslServerSocketFactory.createServerSocket(port)){

            do {
                Thread userThread = new Thread(new UserController(server.accept()));
                userThread.start();
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}