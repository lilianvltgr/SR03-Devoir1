package fr.utc.sr03;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

/**
 * classe Client permettant d'effectuer une connexion
 * auprès du serveur et lancer les deux threads de communication
 * ClientMessageReceptor pour intercepter les messages venant du serveur
 * et ClientMessageSender permettant de récupérer les messages saisis
 * par l’utilisateur et de les transmettre au serveur
 */

public class Client {
    static public Socket communication;
    static volatile boolean connectionActive = true;

    public Client(String host, int port) throws IOException {
        Socket communication = new Socket(host, port);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in); // scanner pour entrée console

        // Connexion serveur
        communication = new Socket("localhost", 10080); // socket créée
        System.out.println("Connecté");
        System.out.println("Ecrivez votre pseudo");


        // Instanciations des pipes
        DataInputStream input = new DataInputStream(communication.getInputStream()); // pipe entrée texte
        DataOutputStream output = new DataOutputStream(communication.getOutputStream()); // pipe sortie texte

        // Création du pseudo + connection au chat
        String pseudo;
        while ((pseudo = sc.next()) == null) {
            System.out.println("Ecrivez votre pseudo");
        }
        output.writeUTF(pseudo); // ecriture pipe sortie pseudo créé
        System.out.println("Vous êtes connectés");

        //New message sender thread
        ClientMessageSender threadMessageSender = new ClientMessageSender(communication, pseudo);
        threadMessageSender.start();

        //New message receptor thread
        ClientMessageReceptor threadMessageReceptor = new ClientMessageReceptor(communication);
        threadMessageReceptor.start();
        while (connectionActive) {
            //while the communication is active, the main programm waits
        }
        System.out.println("Déconnection en cours");
        sleep(2000);
        communication.close();
    }
}