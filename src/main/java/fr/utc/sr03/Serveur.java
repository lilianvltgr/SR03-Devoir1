package fr.utc.sr03;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Serveur {
    static public ServerSocket connection;
    static public Socket communication;
    public static void main( String[] args) throws IOException {
        connection = new ServerSocket(10080);
        communication = connection.accept();



        InputStream input = communication.getInputStream();
        OutputStream output = communication.getOutputStream();
        while(true) {
            byte[] buffer = new byte[1000];
            input.read(buffer);
            String message = new String(buffer);
            System.out.println(message);
            output.write("hello client".getBytes());
        }
    }
}
