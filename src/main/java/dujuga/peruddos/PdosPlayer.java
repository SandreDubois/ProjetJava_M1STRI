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
    private boolean mWait = false;
    private boolean mJoueurOk = true;
    private boolean serverWakeMe = false;

    private boolean inGame = false;
    
    /**
     * Give a PdosDice to the player.
     */
    public void addDice(){
        mDes.add(new PdosDice());
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
     * Show the list of existing game then ask to the client if he wants to create/join or quit.
     * @throws IOException 
     */
    private int chooseGame() throws IOException{
        String message = null;
        int recept = -2;
        boolean OK = false;
        int cptry = 0;

        /* Show existing rooms to client */
        this.showRoomsToClient();

        do{
            send("[ -1 > Créer   |   ID > Rejoindre  |   -2 > Quitter ]");
            recept = listenInt();
        } while (recept < -2 || recept > mDaddy.getNumberOfRoom());

        switch(recept){
            
            case -2 :   //User wants to quit.
                send("A bientôt !");
                send("END");
                System.out.println("[WAR] The user " + mPseudo + " has been ejected !");
                this.heIsGone();
                break;
                
            case -1 :   //User wants to create a game.
                createGame();
                break;
            default :   //User join a game.
                send("Tentative de connexion...");
                do {
                    if(mDaddy.joinGame(this, recept) == -1){
                        cptry++;
                    }
                    else{
                        OK = true;
                        setInGame(true);
                    }
                    
                    if(!OK && cptry == 5){
                        OK = true;
                        send("Impossible de rejoindre la partie.");
                    }
                        
                } while(!OK);
                break;
        }        
        return recept;
    }
    
    /**
     * Create a game and refers it to the PdosServer
     */
    private void createGame(){
        int cpt;
        do{
            if(!mDaddy.askForRoom()){
                try {
                    this.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cpt = mDaddy.addRoom(this);
            /* TO_ADD : Stop after somes try */
        } while(cpt == -1);
        
        mDaddy.launchGame(cpt);
    }
    
    /**
     * Return the ip and the port of the player.
     * @return String 
     */
    public String getIp(){
        return mIp;
    }
    
    /**
     * Return the pseudonym of the player.
     * @return String
     */
    public String getPseudonym(){
        return mPseudo;
    }

    /**
     * Verify that the player has dice.
     * @return boolean
     */
    public boolean hasDice(){
        return (mDes.size() > 0);
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
        this.mJoueurOk = false;
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
            heIsGone();
        }

    }
    
    /**
     * Listening the socket and return the string received.
     * @param notIp : specify if the content must be an Ip or not.
     * @return String
     */
    private String listen(boolean notIp){
        if(notIp)
            send("WAITFOR STR");
        else
            send("WAITFOR IP");
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
        send("WAITFOR INT");
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
     * Remove a PdosDice from the player.
     * @throws IOException 
     */
    public void loseDice() throws IOException{
        DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
        oStream.writeUTF("Vous avez perdu un dé.");
        mDes.remove(mDes.size()-1);
    }
    
    /**
     * Notify the thread.
     */
    public synchronized void notifyMe(){
        notify();
        serverWakeMe = true;
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
                    heIsGone();
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
     * Main method.
     */
    @Override
    public void run(){
        int cpt = -1;
        System.out.println("Création d'un joueur en cours.");
        String message = null;
        send("Nous allons tenter de vous créer un joueur.");

        /* Initialisation */
        try {       
            registPlayer();
        } catch (IOException ex) {
            System.out.println("Enregistrement impossible.");
            heIsGone();
        }
        
        if(mJoueurOk)
            while(serverHandler() == -2 && mJoueurOk);

    }
    
    /**
     * Send the message given in parameter to the client.
     * @param message
     * @throws IOException 
     */
    public void send(String message) {
            System.out.println("J'envoie " + message);
            DataOutputStream oStream;
            try {
                oStream = new DataOutputStream(mSocket.getOutputStream());
                oStream.writeUTF(message);
            } catch (IOException ex) {
                System.out.println("[FAT] Player is gone.");
                heIsGone();
            }
    }
    
    /**
     * Maintains communations between server and client, and between PdosGame and client when he has choosen a game.
     * @return 
     */
    private synchronized int serverHandler(){
        int cpt = -2;
        
        try {
            cpt = chooseGame();
        } catch (IOException ex) {
            Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* trois cas : le joueur veut quitter, il veut créer, il veut rejoindre */
        switch(cpt){
            case -2:
                break;
            case -1: 
                try {
                    //Creator handle game :
                    do{
                        wait(1000);
                        send("PING");
                    } while(!serverWakeMe && mJoueurOk);
                    
                } catch (InterruptedException ex) {
                    System.out.println("Erreur lors de l'attente.");
                    heIsGone();
                }
                
                break;
            default: //Join a game :
                do{
                    try {
                        wait(1000);
                        send("PING");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PdosPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while(inGame == true);
                break;
        }
        
        return -2;
    }
    
    /**
     * Update the id of the player with the given id.
     * @param id 
     */
    public void setIdJoueur(int id){
        mIdJoueur = id;
    }
    
    /**
     * Change the attribute inGame at i.
     * @param i : boolean.
     */
    public void setInGame(boolean i){
        inGame = i;
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
    
    /**
     * Send the client all rooms hosted by the server.
     */
    private void showRoomsToClient(){
        ArrayList <PdosGame>listRoom = mDaddy.getRooms();
        PdosGame current;

        if(listRoom.isEmpty())
            send("Il n'y a pas de salle.");
        else{
            send("ID        Effectif        Createur");
            
            for(int i = 0; i < listRoom.size(); i++){
                current = listRoom.get(i);
                send(current.getIdGame() + "      " + current.getNumberOfPlayers() + "/6        " + current.getCreatorPseudonym());
                
            }
        }
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
     * [TEST]
     * Welcome player for join a room.
     * @throws IOException 
     */
    public void welcome() throws IOException{
        send("Vous avez rejoint une partie.");
    }

}
