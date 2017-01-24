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
import java.util.Enumeration;
import java.util.Hashtable;
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
    /* private int numberOfRoom = 0; Not needed anymore : ArrayList <PdosGame> myRooms.size(); */
    
    private int serverPort = 18000;
    String errMessage;
    private ArrayList <PdosPlayer> myClients = new ArrayList();
    private ArrayList <PdosGame> myRooms = new ArrayList();
    
    private boolean lockOnClient = false;
    private boolean lockOnRoom = false;
    
    public boolean askForClient(){
        if(lockOnClient){            
            return false;
        }
        else{
            /* if not, lock */
            lockOnClient = true;
            return true;
        }      
    }
    
    public boolean askForRoom(){
        if(lockOnRoom){            
            return false;
        }
        else{
            /* if not, lock */
            lockOnRoom = true;
            return true;
        }      
    }
        
    /* Return false if the pseudo is already taken */
    public int addClient(PdosPlayer newP){      
        
        int sauv = myClients.size();        /* int keep the initial number of players */
        int returned = myClients.size();    /* for know if the registration works */
        
        if(lockOnClient == false)           /* if there's no lock on the table, return error */
            return -1;
        
        myClients.add(newP);                /* if there is a lock, add the client to the table */
        returned = myClients.size(); 
        lockOnClient = false;               /* remove the lock */
        
        if(returned > sauv)                 /* if the size has increase, return the id in the table as the id */
            return returned;
        else
            return -1;                      /* if not, return a error */
    }
    
    public int addRoom(PdosPlayer creator){
        int sauv = myRooms.size();        /* int keep the initial number of players */
        int returned = myRooms.size();    /* for know if the registration works */
        
        if(lockOnRoom == false)           /* if there's no lock on the table, return error */
            return -1;
        
        /* Create Room */
        myRooms.add(new PdosGame(creator, returned));
        
        returned = myRooms.size(); 
        lockOnRoom = false;               /* remove the lock */
        
        System.out.println(sauv + " " + returned);
        
        if(returned > sauv)                 /* return the id */
            return returned-1;
        else
            return -1;                      /* if not, return a error */
    }
    
    /* browse the table for send on stdout names of players */
    public void showClients(){
        for(int i = 0; i < myClients.size(); i++){
            System.out.println(i + " : " +myClients.get(i).getPseudonym());
        }
    }
    
    public ArrayList<PdosGame> getRooms(){
        return myRooms;
    }
    
    /*Add a method to increment mainServ.nombreClient*/
    protected void delClient(){
        nombreClient--;
    }
    
    /* return the number of client */
    public int getNumberOfClient(){
        return myClients.size();
    }
    
    /* return the number of rooms in the server */
    public int getNumberOfRoom(){
        return myRooms.size();
    }
    
    /* Verify if there's already an instance of the application
        - return true if there is. 
        - return false either otherwise */
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
    
    /* This method listening for new connexion then redirect it to thread PdosPlayer. */
    private void socketHandler(){
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
    
    /* Announce on stdout the @IP and his port */
    private void declarationAtLaunch()throws UnknownHostException {
       String adresseipServeur  = InetAddress.getLocalHost().getHostAddress(); 
       System.out.println("Mon adresse est " + adresseipServeur + ":" + serverPort );
    }
       
    /* Main method */    
    public static void main(String[] args) {
        PdosServer mainServ = new PdosServer(); /* instance de la classe principale */
        if(mainServ.existingServer()){ /* Test si serveur deja existant*/
            System.out.println("Serveur deja existant.");
            System.exit(0);
        }
        System.out.println("Création du serveur.");
        try {
            mainServ.declarationAtLaunch();
            mainServ.socketHandler();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PdosServer.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
