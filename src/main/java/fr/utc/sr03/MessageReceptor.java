package fr.utc.sr03;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MessageReceptor extends Thread{
    private Socket client;
    public MessageReceptor(Socket client){this.client = client;}

    @Override
    public void run(){

            try {
                InputStream ins=client.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }




    }

}
