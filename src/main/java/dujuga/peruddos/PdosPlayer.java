/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author quentin
 */
public class PdosPlayer extends Thread {
        private int mIdJoueur;
        private Socket mSocket;         /* Add the socket of the client */
        private String mPseudo;
        private int mNbDes;
        private String mIp;
        private ArrayList <PdosDice>mDes; /* Declares an ArrayList of PdosDice named mDes */
        private PdosServer mDaddy;
        //private boolean createur;         TBC : NOT NEEDED ;
        //private boolean estSpectateur;    TBC : NOT NEEDED ;
        
        public boolean hasDice(){
            if(mNbDes > 0){
                return true;
            }
            else{
                return false;
            }
        }
        
        public void loseDice() throws IOException{
            DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
            oStream.writeUTF("Vous avez perdu un dé.");
            mNbDes--;
        }
        
        public void welcome() throws IOException{
            send("Vous avez rejoint une partie.");
        }
        
        private String listen(){
            String message = "ERROR";
            System.out.println("J'écoute.");
            try{
                DataInputStream iStream = new DataInputStream(mSocket.getInputStream());
                message = iStream.readUTF();
                System.out.println("J'ai reçu : \"" + message + "\"");
            }
            catch(IOException ioe){
                    System.out.println("Erreur lors de l'écoute: " + ioe.getMessage());
            }

            return message;
        }

        /* for string */
        private void send(String message) throws IOException{
                System.out.println("J'envoie " + message);
                DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
                oStream.writeUTF(message);
        }

        /* for int */
        /*private void send(int message) throws IOException{
                System.out.println("J'envoie \"" + message + "\"");
                DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
                oStream.writeInt(message);
        }*/

        private String askEntry(String message){
            Scanner getFromUser = new Scanner(System.in);
            String returned = null;
            System.out.println(message);
            System.out.print("> ");
            returned = getFromUser.nextLine();

            return returned;
        }
        
        public void addDice(){
            mNbDes++;
        }
        
        public void tossDices(){
            for(int i = 0; i < mNbDes; i++){
                mDes.get(i).toss();
            }
        }
        
        /*
            Shows value of dices of stdout.
        */
        public void showDices(){
            System.out.print("The player " + mPseudo + " haves");
            for(int i = 0; i < mNbDes; i++)
                System.out.print(" " + mDes.get(i).getValue());
            System.out.println(".");
        }
             
        public PdosPlayer(Socket sock, int idJoueur, PdosServer daddy){
            mNbDes = 5;
            mIdJoueur = idJoueur;
            mDes = new ArrayList();
            mSocket = sock;
            mDaddy = daddy;           
            
            for(int i = 0; i < 6; i++) /* Give six dices to the player */
                mDes.add(new PdosDice());
        }
        
        public PdosPlayer(String pseudo, Socket sock, String ip, int idJoueur){
            mPseudo = pseudo;
            mNbDes = 5;
            mIdJoueur = idJoueur;
            mDes = new ArrayList();
            mSocket = sock;
            mIp = ip;
            
            for(int i = 0; i < 6; i++) /* Give six dices to the player */
                mDes.add(new PdosDice());
        }
        
        public PdosPlayer(String pseudo, Socket sock, int idJoueur){
            mPseudo = pseudo;
            mNbDes = 5;
            mIdJoueur = idJoueur;
            mDes = new ArrayList();
            mSocket = sock;
            mIp = null;
            
            for(int i = 0; i < 6; i++) /* Give six dices to the player */
                mDes.add(new PdosDice());
        }
        
        private void infoClient(Socket sockService){        
        String errMessage;
        String pseudo;
        
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
        
        public void run(){
            System.out.println("Création d'un joueur en cours.");
            String message = null;
            try {
                send("Nous allons tenter de vous créer un joueur.");
            } catch (IOException ex) {
                Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }        

            /* HERE exchange between server & client will be placed here. */
            
            /* Initialisation */
            try {       
                do{
                    /* Acquisition du pseudonyme : */
                    send("Quel est ton pseudonyme ?");
                    send("WAITFOR STR");
                    mPseudo = listen();
                
                    /* Verify we can add client in the db */
                    if(!mDaddy.askForClient()){
                        try {
                            this.wait(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } while(!mDaddy.addClient(mPseudo));
                
                mDaddy.showClients();
                
                /* Acquisition de l'ip : */
                send("Quel est ton ip ?");
                send("WAITFOR IP");
                mIp = listen();
            } catch (IOException ex) {
                Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /* Verify numberOfGames */
            if(mDaddy.getNumberOfRoom() == 0){
                do{
                    try {
                        send("There is no room.");
                        send("Do you want to create one ? (YES/NO)");
                        send("WAITFOR STR");
                        message = listen();
                    } catch (IOException ex) {
                        Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (message.compareTo("YES") != 0 && message.compareTo("NO") != 0);
                
                if(message.compareTo("YES") == 0){
                    /* create a server */
                }
                else{
                    try {
                        send("You should not be here.");
                        send("BAM!");
                        send("END");
                        /* quit */
                        System.out.println("[WAR] The user " + mPseudo + " has been ejected !");
                        this.stop();
                    } catch (IOException ex) {
                        Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
            }
            
            while(true){
                try {
                    message = askEntry("Message pour " + mPseudo);
                    send(message);
                    
                    if(message.compareTo("WAITFOR INT") == 0){
                        listen();
                    }
                    else if(message.compareTo("WAITFOR STR") == 0){
                        listen();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
}
