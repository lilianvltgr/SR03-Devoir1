package fr.utc.sr03;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.InputStream;
import java.io.OutputStream;

public class Client {
    static public Socket communication;

    public Client(String host, int port) throws IOException {
        Socket communication = new Socket(host, port);
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        communication = new Socket("localhost", 10080);
        System.out.println("Connecté");
        System.out.println("Ecrivez votre pseudo");
        String pseudo = sc.next();
        DataInputStream input = new DataInputStream(communication.getInputStream());
        DataOutputStream output = new DataOutputStream(communication.getOutputStream());

        //Création du pseudo + connection au chat
        output.writeUTF(pseudo);
        System.out.println("Vous êtes connectés");

        //Ecoute (avec un thread?) + affichage des messages (avec un autre thread?)
        while (true) {
            //lecture du message
            String message = sc.next();
            //envoi du message au thread server
            output.writeUTF(pseudo);
            output.writeUTF(message);
        }
    }
}