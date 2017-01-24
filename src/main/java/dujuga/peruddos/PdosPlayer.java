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
    
    private static final int errInt = -3;
    private static final String errStr = "ERROR";
    
    private int mIdJoueur;          /* Id of the player */
    private Socket mSocket;         /* Socket to talk with client */
    private String mPseudo = null;  /* Pseudonym of the player */
    private String mIp = null;      /* Ip of the player */
    private ArrayList <PdosDice>mDes; /* Declares an ArrayList of PdosDice named mDes */
    private PdosServer mDaddy;      /* Main server */

    /**
     * Return the pseudonym of the player.
     * @return String
     */
    public String getPseudonym(){
        return mPseudo;
    }

    /**
     * Update the id of the player with the given id.
     * @param id 
     */
    public void setIdJoueur(int id){
        mIdJoueur = id;
    }

    /**
     * Return the ip and the port of the player.
     * @return String 
     */
    public String getIp(){
        return mIp;
    }

    /**
     * Verify that the player has dice.
     * @return boolean
     */
    public boolean hasDice(){
        return (mDes.size() > 0);
    }

    /**
     * Remove a PdosDice from the player.
     * @throws IOException 
     */
    public void loseDice() throws IOException{
        DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
        oStream.writeUTF("Vous avez perdu un dé.");
        mDes.remove(mDes.size()-1);
    }

    /**
     * [TEST]
     * Welcome player for join a room.
     * @throws IOException 
     */
    public void welcome() throws IOException{
        send("Vous avez rejoint une partie.");
    }

    /**
     * Listening the socket and return the string received.
     * @param notIp : specify if the content must be an Ip or not.
     * @return String
     */
    private String listen(boolean notIp){
        try {
            if(notIp)
                send("WAITFOR STR");
            else
                send("WAITFOR IP");
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        String message = "ERROR";
        System.out.println("[ME] I'm listening " + mIp);
        try{
            DataInputStream iStream = new DataInputStream(mSocket.getInputStream());
            message = iStream.readUTF();
            System.out.println("[ME] I've heard : \"" + message + "\"");
        }
        catch(IOException ioe){
                System.out.println("[ERR] ON LISTENING : " + ioe.getMessage());
        }

        return message;
    }

    /**
     * Listening the socket and return the int received.
     * @return 
     */
    private int listenInt(){
        try {
            send("WAITFOR INT");
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        int message = -2;
        System.out.println("[ME] I'm listening " + mIp);

        try{
            DataInputStream iStream = new DataInputStream(mSocket.getInputStream());
            message = iStream.readInt();
            System.out.println("[ME] I've heard : \"" + message + "\"");
        }
        catch(IOException ioe){
                System.out.println("[ERR] ON LISTENING INT : " + ioe.getMessage());
        }

        return message;
    }

    /**
     * Send the message given in parameter to the client.
     * @param message
     * @throws IOException 
     */
    private void send(String message) throws IOException{
            System.out.println("J'envoie " + message);
            DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
            oStream.writeUTF(message);
    }


    /**
     * Print a message given and wait for user to respond, return the answer as a String.
     * @param message
     * @return String
     */
    private String askEntry(String message){
        Scanner getFromUser = new Scanner(System.in);
        String returned = null;
        System.out.println(message);
        System.out.print("> ");
        returned = getFromUser.nextLine();

        return returned;
    }

    /**
     * Give a PdosDice to the player.
     */
    public void addDice(){
        mDes.add(new PdosDice());
    }

    /**
     * Toss all PdosDices of the player.
     */
    public void tossDices(){
        for(int i = 0; i < mDes.size(); i++){
            mDes.get(i).toss();
        }
    }

    /**
     * Show PdosDice of the player.
     */
    public void showDices(){
        System.out.print("The player " + mPseudo + " haves");
        for(int i = 0; i < mDes.size(); i++)
            System.out.print(" " + mDes.get(i).getValue());
        System.out.println(".");
    }

    private void showRoomsToClient(){
        ArrayList <PdosGame>listRoom = mDaddy.getRooms();
        PdosGame current;

        if(listRoom.isEmpty())
            try {
                send("Il n'y a pas de salle.");
            } catch (IOException ex) {
                Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        else{
            try {
                send("ID        Createur        Effectif");
            } catch (IOException ex) {
                Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }

            for(int i = 0; i < listRoom.size(); i++){
                current = listRoom.get(i);

                try {
                    send(current.getIdGame() + "        " + current.getCreatorPseudonym() + "      " + current.getNumberOfPlayers());
                } catch (IOException ex) {
                    Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    
    /**
     * Constructor.
     */
    public PdosPlayer(){

    }
    
    /**
     * Constructor with some new attributes.
     * @param sock      :   Socket
     * @param idJoueur  :   int
     * @param daddy     :   PdosServer
     */
    public PdosPlayer(Socket sock, int idJoueur, PdosServer daddy){
        mIdJoueur = idJoueur;
        mDes = new ArrayList();
        mSocket = sock;
        mDaddy = daddy;           

        for(int i = 0; i < 6; i++) /* Give six dices to the player */
            mDes.add(new PdosDice());
    }

    /**
     * Show the pseudonym and the ip of the client
     * @param sockService : Socket
     */
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

    /**
     * Shutdown the thread.
     */
    private void heIsGone(){
        if(mPseudo == null)
            mPseudo = "Unknown";

        if(mIp == null)
            mIp = "a place we did'nt know";

        System.err.println("[ERR] Client (" + mPseudo +") disconnected from " + mIp +".");
        this.stop();
    }

    /**
     * Regist the player to the PdosServer.
     * @throws IOException 
     */
    private void registPlayer() throws IOException{
        int cpt;
        do{
            /* Acquisition du pseudonyme : */
            send("Quel est ton pseudonyme ?");
            mPseudo = listen(true);

            /* Verify we can add client in the db */
            if(!mDaddy.askForClient()){
                try {
                    this.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cpt = mDaddy.addClient(this);
            if(cpt == -1)
                send("Echec de l'enregistrement : sélectionner un autre pseudonyme.");
        } while(cpt == -1);
        mIdJoueur = cpt;

        /* Acquisition de l'ip : */
        mIp = listen(false);
    }

    /**
     * Create a game and refers it to the PdosServer
     */
    private void createGame(){
        int cpt;
        try {
            do{
                if(!mDaddy.askForRoom()){
                    try {
                        this.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                cpt = mDaddy.addRoom(this);
                send("Demande de création de partie : " + cpt);
            } while(cpt == -1);


            send("Votre partie a été créée avec l'ID : " + cpt);
            send("Mise en veille.");
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        while(true){
            try {
                this.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Show the list of existing game then ask to the client if he wants to create/join or quit.
     * @throws IOException 
     */
    private void chooseGame() throws IOException{
        String message = null;
        int recept = -2;

        /* Show existing rooms to client */
        this.showRoomsToClient();

        do{
            send("[ 0 > Créer   |   ID > Rejoindre  |   -1 > Quitter ]");
            recept = listenInt();
        } while (recept < -1 || recept > mDaddy.getNumberOfRoom());

        switch(recept){
            case -1 :
                send("A bientôt !");
                send("END");
                System.out.println("[WAR] The user " + mPseudo + " has been ejected !");
                this.heIsGone();
                break;
            case 0 :
                createGame();
                break;
            default :
                send("Not handled for now");
                send("END");
                System.out.println("[WAR] The user " + mPseudo + " has been ejected !");
                this.heIsGone();
                break;
        }

    }

    /**
     * Main method.
     */
    public void run(){
        int cpt = -1;
        System.out.println("Création d'un joueur en cours.");
        String message = null;
        try {
            send("Nous allons tenter de vous créer un joueur.");
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Initialisation */
        try {       
            registPlayer();
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            chooseGame();
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }



            /*while(true){
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
            this.heIsGone();
            }
            }*/

    }
        
}
