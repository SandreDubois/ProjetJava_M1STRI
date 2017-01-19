package dujuga.peruddos;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author Alexis
 */
public class PdosSocketServer extends Thread {

    private Socket mSockService;
    
    PdosSocketServer(Socket sockService) {
        mSockService = sockService;
    }
    
    private void infoClient(Socket sockService){        
        String errMessage;
        try{
            DataInputStream iStream = new DataInputStream(sockService.getInputStream());
            String pseudo = iStream.readUTF();
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
    }
}
