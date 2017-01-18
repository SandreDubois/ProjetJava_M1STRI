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
    /* Needs a socket to run (it's avoid blister ;) )*/
    public void run(){
        System.out.println("Création d'un thread.");
        String errMessage;
        boolean stop = false;

        do{
            try{
                DataInputStream iStream = new DataInputStream(mSockService.getInputStream());
                int trois = iStream.readInt();
                System.out.println("Le serveur a reçu : " + trois);
            }
            catch(IOException ioe){
                errMessage = ioe.getMessage();
                if(errMessage.compareTo("Connection reset") != 0)
                    System.out.println("Erreur lors de l'acceptation du client : " + ioe.getMessage());
                else {
                    System.out.println("Le client s'est déconnecté.");
                }
                stop = true;
            }
        } while(!stop);
    }
}
