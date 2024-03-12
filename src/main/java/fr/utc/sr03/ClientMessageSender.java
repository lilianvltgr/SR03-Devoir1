package fr.utc.sr03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMessageSender extends Thread{
    private Socket communication;
    private String pseudo;

    //constructor
    public ClientMessageSender(Socket communication, String pseudo){
        this.communication = communication;
        this.pseudo = pseudo;
    }

    @Override
    public void run(){
            try {
                Scanner sc = new Scanner(System.in);
                DataOutputStream output = new DataOutputStream(communication.getOutputStream());

                while (true) {
                    //lecture du message
                    String message = sc.next();
                    //envoi du message au thread server
                    output.writeUTF(pseudo);
                    output.writeUTF(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}
