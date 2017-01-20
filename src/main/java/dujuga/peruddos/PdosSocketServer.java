package dujuga.peruddos;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */

/*
    Games will be handed here.
*/
public class PdosSocketServer extends Thread {

    private Socket mSockService;
    private String pseudo;
    
    PdosSocketServer(Socket sockService) {
        mSockService = sockService;
    }
    
    private void ecoute(){
        try{
            DataInputStream iStream = new DataInputStream(mSockService.getInputStream());
            String message = iStream.readUTF();
            System.out.println(message);
        }
        catch(IOException ioe){
                System.out.println("Erreur lors de l'écoute: " + ioe.getMessage());
        }
    }
    
    
    
    private void infoClient(Socket sockService){        
        String errMessage;
        
        try{
            DataInputStream iStream = new DataInputStream(sockService.getInputStream());
            pseudo = iStream.readUTF();
            System.out.println("Pseudo du client : " + pseudo);
            String infoclient = iStream.readUTF();
            System.out.println(infoclient);
        }
        catch(IOException ioe){
                errMessage = ioe.getMessage();
                if(errMessage.compareTo("Connection reset") != 0)
                    System.out.println("Erreur lors de l'acceptation du client : " + ioe.getMessage());
                else {
                    System.out.println("Le client s'est déconnecté.");
                }
            }
            
    }
    
    /* Needs a socket to run (it's avoid blister ;) )*/
    public void run(){
        System.out.println("Création d'un thread.");
        boolean stop = false;

        do{
            infoClient(mSockService);
            stop = true;
        } while(!stop);
        
        /* TEST GAME */
        PdosPlayer p = new PdosPlayer(pseudo, mSockService, ip, 0);
        
        try {
            PdosGame g = new PdosGame(p, 0); /* Create Game */
        } catch (IOException ex) {
            Logger.getLogger(PdosSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
