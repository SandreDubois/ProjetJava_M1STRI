/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dujuga.peruddos;


import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
<<<<<<< HEAD
import java.io.InputStreamReader;
import static java.lang.System.in;

=======
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
>>>>>>> origin/Version1


/**
 *
 * @author Alexis
 * In this file, will be implements the main Server of the game PERUDDOS.
 */
public class mainServ {
    private int nombreClient = 0; /* Variable qui sert à compter le nombre de client courant. */
    private int serverPort = 18000;
    String errMessage;
    
    /*Add a method to increment mainServ.nombreClient*/
    protected void delClient(){
        nombreClient--;
    }
     
    
    private void gestionClient(Socket sockService){
        nombreClient++;
        
        try{
            DataInputStream iStream = new DataInputStream(sockService.getInputStream());
            String pseudo = iStream.readUTF();
            System.out.println("Pseudo du client : " + pseudo);
            String infoclient = iStream.readUTF();
            System.out.println(infoclient);
        }
        catch(IOException ioe){
                System.out.println("Erreur lors de l'acceptation du client : " + ioe.getMessage());
        }
            
    }
    
    private void gestionSocket(){
        /* TBC "= null" for sockEcoute & sockService */
        ServerSocket sockEcoute = null;    //Déclaration du serverSocket.
        Socket sockService = null;         //Déclaration du socket de service.
        boolean getClient = true;   //Permet de stopper l'écoute de nouveaux clients.
        /* Rappel des étapes d'une connexion : */
            /* Création sock écoute + bind */
            try{
                sockEcoute = new ServerSocket(serverPort);
            }
            catch(IOException ioe){
                System.out.println("Erreur de création du server socket : " + ioe.getMessage());
                /* DEVRAIT-ON GERER DES LOGS ? */
            }
            
            /* CLIENT - Création sock client + bind */
            /* CLIENT - Demande de Connexion */
            
            while(getClient){
                try{
                    sockService = sockEcoute.accept();
                }
                catch(IOException ioe){
                    System.out.println("Erreur de création du socket service : " + ioe.getMessage());
                }
                
                /* CREER UN THREAD POUR LA GESTION DU CLIENT */
                //Without thread :gestionClient(sockService);
                /* With thread : */
                PdosSocketServer sock = new PdosSocketServer(sockService);
                nombreClient++;
                sock.start();       /* Le client est redirigé vers un thread/socket de gestion */
                
                
            }
            
            /* Acceptation de connexion */
            /* Communication */
    }
    
    private void declaration()throws UnknownHostException {
    
       String adresseipServeur  = InetAddress.getLocalHost().getHostAddress(); 
          System.out.println("Mon adresse est " + adresseipServeur + ":" + serverPort );
         
       
    }
       
    /* Fenêtre principale. */
    
    public static void main(String[] args) {
        mainServ mainServ = new mainServ(); /* instance de la classe principale */
        System.out.println("Création du serveur.");
        try {
            mainServ.declaration();
        } catch (UnknownHostException ex) {
            Logger.getLogger(mainServ.class.getName()).log(Level.SEVERE, null, ex);
        }
        mainServ.gestionSocket();
        
    }
    
}
