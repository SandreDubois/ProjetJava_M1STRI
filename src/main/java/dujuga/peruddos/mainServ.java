/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dujuga.peruddos;


import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 *
 * @author Alexis
 * In this file, will be implements the main Server of the game PERUDDOS.
 */
public class mainServ {
    private int nombreClient = 0; /* Variable qui sert à compter le nombre de client courant. */
    private int serverPort = 18000;
    
    private void gestionClient(Socket sockService){
        nombreClient++;
        
        try{
            DataInputStream iStream = new DataInputStream(sockService.getInputStream());
            int trois = iStream.readInt();
            System.out.println("Le serveur a reçu : " + trois);
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
                gestionClient(sockService);
                
            }
            
            /* Acceptation de connexion */
            /* Communication */
    }
    
    /* Fenêtre principale. */
    public static void main(String[] args) {
        mainServ mainServ = new mainServ(); /* instance de la classe principale */
        System.out.println("Création du serveur.");
        
        mainServ.gestionSocket();
        
    }
    
}
