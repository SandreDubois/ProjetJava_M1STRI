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
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import static java.lang.System.in;
import java.util.ArrayList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;


/**
 *
 * @author Alexis
 * In this file, will be implements the main Server of the game PERUDDOS.
 */
public class PdosServer {
    private int nombreClient = 0; /* Variable qui sert à compter le nombre de client courant. */
    private int numberOfRoom = 0;
    private int serverPort = 18000;
    String errMessage;
        
    /*Add a method to increment mainServ.nombreClient*/
    protected void delClient(){
        nombreClient--;
    }
    
    public int getNumberOfClient(){
        return nombreClient;
    }
    
    public int getNumberOfRoom(){
        return numberOfRoom;
    }
    
    /* Methode pour savoir si un serveur existe deja*/
    private boolean existingServer(){
        Socket sock = null;
        try{
            sock = new Socket("127.0.0.1", 18000);
            return true;
        }
        catch(IOException ioe){
            return false;
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
                    PdosPlayer player = new PdosPlayer(sockService, nombreClient, this);
                    player.start();
                }
            }
            catch(IOException ioe){
                System.out.println("Erreur de création du server socket : " + ioe.getMessage());
                /* DEVRAIT-ON GERER DES LOGS ? */
            }
            
    }
    
    private void declaration()throws UnknownHostException {
    
       String adresseipServeur  = InetAddress.getLocalHost().getHostAddress(); 
          System.out.println("Mon adresse est " + adresseipServeur + ":" + serverPort );
    
    }
       
    /* Fenêtre principale. */
    
    public static void main(String[] args) {
        PdosServer mainServ = new PdosServer(); /* instance de la classe principale */
        if(mainServ.existingServer()){ /* Test si serveur deja existant*/
            System.out.println("Serveur deja existant.");
            System.exit(0);
        }
        System.out.println("Création du serveur.");
        try {
            mainServ.declaration();
            mainServ.gestionSocket();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PdosServer.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
