package fr.utc.sr03;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 Class inheriting the characteristics of threads, allowing
 the client to write a message on the console and send it to the server.
 */
public class ClientMessageSender extends Thread {
    public Socket communication;
    private String pseudo;

    //constructor
    public ClientMessageSender(Socket communication, String pseudo) {
        this.communication = communication;
        this.pseudo = pseudo;
    }

    @Override
    // Fonction permettant de faire tourner le thread
    public void run() {
        try {
            Scanner sc = new Scanner(System.in);
            DataOutputStream output = new DataOutputStream(communication.getOutputStream());

            while (Client.connectionActive) {
                //lecture du message
                String message = sc.nextLine();
                //envoi du message au thread server
                output.writeUTF(pseudo);
                output.writeUTF(message);
                if (message.equals("exit")) {
                    // Code pour envoyer un message de déconnexion au serveur
                    Client.connectionActive = false;
                }
            }
        } catch (IOException e) {
            if (Client.connectionActive)
                throw new RuntimeException(e);
        }
    }
}
