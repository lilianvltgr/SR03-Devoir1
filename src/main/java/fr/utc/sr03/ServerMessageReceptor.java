package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ServerMessageReceptor extends Thread{
    private Socket client;
    public ServerMessageReceptor(Socket client){this.client = client;}

    @Override
    public void run(){
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());
                while(true){
                    String pseudo = input.readUTF();
                    String message = input.readUTF();
                    Server.sendMessagesToCLients(message, pseudo);

                    //send the message to every client
                    //System.out.println("message de " + pseudo +": "+message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            }

}
