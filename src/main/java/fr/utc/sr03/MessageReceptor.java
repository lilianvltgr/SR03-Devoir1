package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MessageReceptor extends Thread{
    private Socket client;
    public MessageReceptor(Socket client){this.client = client;}

    @Override
    public void run(){
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());
                while(true){
                    String pseudo = input.readUTF();
                    String message = input.readUTF();
                    //send the message to every client
                    System.out.println("message de " + pseudo +": "+message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            }

}
