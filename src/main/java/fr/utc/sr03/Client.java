package fr.utc.sr03;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Client {
    static public Socket communication;

    public Client(String host, int port) throws IOException {
        Socket communication = new Socket (host, port);
    }
    public static void main(String[] args) throws IOException {
        communication = new Socket ("localhost", 10080);
        System.out.println("Connecté");
        InputStream input = communication.getInputStream();
        OutputStream output = communication.getOutputStream();
        output.write("Hello server".getBytes());

        byte[] buffer = new byte[1000];
        input.read(buffer);
        String message = new String(buffer);
        System.out.println(message);
    }
}
