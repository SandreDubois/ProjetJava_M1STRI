/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */
public class PdosGame extends Thread {
    
    private PdosServer mDaddy;
    private int mIdPdosGame;
    private ArrayList <PdosPlayer>mListPlayer; /* ArrayList where players will be. */
    
    /**
     * Try to add the new PdosPlayer. 
     * Compare the number of player already in the game.
     * @param newP is the PdosPlayer to add.
     * @return  the id in the game if the player has been add.
     *          -1 if not.
     */
    public int askToJoin(PdosPlayer newP){
        /* To many players are in game */
        if(mListPlayer.size() >= 6){
            return -1;
        }
        
        mListPlayer.add(newP);
        return mListPlayer.size()-1;
    }

    /**
     * Broadcast the message given in parameters to all connected players.
     * @param message 
     */
    private void broadcast(String message){
        for(int i = 0; i < mListPlayer.size(); i++){
            sendTo(i, message);
        }
    }

    /**
     * Check if the Thread of the PdosPlayer with the i index in the mListPlayer is alive.
     * If not, alert others players the i player is missing then remove it from the list.
     * If the player is the creater (index 0) :
     *      if there is no others players, stop the game.
     *      if there is others players, designates the 2th player as the new creator.
     * @param i : the index in the mListPlayer.
     */
    private void checkPlayer(int i){
        System.out.println("Check du joueur : " + mListPlayer.get(i).getPseudonym());
        if(!mListPlayer.get(i).isAlive()){
            broadcast("Le joueur " + mListPlayer.get(i).getPseudonym() + " est parti.");
            /* Suppression de la correspondance avec le créateur */
            ejectPlayer(i);
            System.out.println("Il reste : " + mListPlayer.size() + " joueurs.");
            
            if(!mListPlayer.isEmpty() && i == 0){
                broadcast("Le nouveau créateur est :" + this.getCreatorPseudonym());
            }
        }
    }
    
    /** 
     * Alert the player with the i index in the list that he is kicked.
     * Then kick him.
     * @param i : index of the PdosPlayer in the mListPlayer.
     */
    private void ejectPlayer(int i){
        if(i >= 0){
            sendTo(i, "Vous n'êtes plus dans la partie.");
            mListPlayer.get(i).setInGame(false);
            mListPlayer.remove(i);
        }
    }
    
    /**
     * Broadcast to every players in the party that the creator won then ejects every players. 
     */
    private void endGame(){
        broadcast("Un gros GG à " + this.getCreatorPseudonym() + " : victoire écrasante !");
        
        for(int i = mListPlayer.size() - 1; i >= 0; i--)
            ejectPlayer(i);
    }
    
    /**
     * Toss all dices in the game.
     */
    public void everybodyToss(){
        for(int i = 0; i < mListPlayer.size(); i++){
            mListPlayer.get(i).tossDices();
            mListPlayer.get(i).showDices();
        }
    }
    
    /**
     * @return the pseudonym of the creator in String.
     */
    public String getCreatorPseudonym(){
        PdosPlayer p = mListPlayer.get(0);
        return p.getPseudonym();
    }
    
    /**
     * @return the id of the game in int.
     */
    public int getIdGame(){
        return this.mIdPdosGame;
    }
    
    /**
     * @return the number of player in the game.
     */
    public int getNumberOfPlayers(){
        return mListPlayer.size();
    }
    
    /**
     * Constructor of the PdosGame.
     * Initialize the list of the PdosPlayer;
     * @param creator   : PdosPlayer who lead the party. Add it to the ArrayList of PdosPlayer.
     * @param index     : is the id of the game.
     * @param daddy     : is the server who host the game.
     */
    public PdosGame(PdosPlayer creator, int index, PdosServer daddy){ /* Create a game with a main player and a index j */
        mListPlayer = new ArrayList();
        mIdPdosGame = index; /* Add index as the id of game */
        mDaddy = daddy;
        mListPlayer.add(creator);
    }
    
    /**
     * Main method of the PdosGame.
     * Alert the PdosPlayer that he has start a game then waits for others players to join.
     * When the room is full, wakes up all Player and launch a game.
     * [TO REMOVE] Waits 10 seconds then designates the creator as the winner.
     * At least, alert the server that the game has ended and stop himself.
     */
    @Override
    public void run(){
        sendTo(0, "Vous avez créer une partie.");
        
        waitingLoop();
        
        if(!mListPlayer.isEmpty()){
            broadcast("La partie est pleine !");
            endGame();
            wakeUpAll();
        }
        
        try {
            sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        unregister();
        System.out.println("Fin de partie.");
    }
    
    /**
     * Send the message given in parameters to the PdosPlayer with the numberPlayer index in the ArrayList of PdosPlayer.
     * @param numberPlayer  : index, in int, of the player.
     * @param message       : message, in String, to send.
     */
    private void sendTo(int numberPlayer, String message){
        if(numberPlayer >= 0 && numberPlayer < mListPlayer.size()){
            mListPlayer.get(numberPlayer).send(message);
        }        
    }

    /**
     * Change the id of the game, set it at the value given in parameters.
     * @param i     : new value of the id, in int.
     */
    public void setIdGame(int i){
        mIdPdosGame = i;
    }
     
    /**
     * Remove the PdosGame in the ArrayList of the PdosGame of the server.
     */
    private void unregister(){
        int returned = -1;
        
        do{
            if(mDaddy.askForRoom())
                returned = mDaddy.delRoom(mIdPdosGame); 
            
            try{
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } while(returned == -1);
        System.out.println("La partie : " + mIdPdosGame + " a été retiré.");
    }
    
    /**
     * Loop where the PdosGame expected player to join. Check all seconds that connected player are connected.
     */
    private void waitingLoop(){
        int actually = 1;
        
        sendTo(0, "Attente de l'arrivée de nouveaux joueurs.");
        
        do{
            /* Si nouveau joueur */
            if(mListPlayer.size() > actually){
                sendTo(mListPlayer.size()-1, "Bienvenue dans la partie de " + this.getCreatorPseudonym());
                broadcast("Le joueur " + mListPlayer.get(actually).getPseudonym() + " a rejoint la partie.");
                actually = mListPlayer.size();
            }
            
            /* Si le PdosPlayer créateur n'est pas encore en activité */
            for(int i = 0; i < mListPlayer.size(); i++){
                checkPlayer(i);
                actually = mListPlayer.size();
            }
            
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(mListPlayer.size() < 6 && !mListPlayer.isEmpty());
        
    }

    /**
     * Invokes the wakeUp() method on all players.
     */
    private void wakeUpAll(){
        for(int i = 0; i < mListPlayer.size(); i++)
            wakeUp(i);
    }
    
    /**
     * Invokes the notifyMe() method of the ith player in the ArrayList of PdosPlayer. 
     * @param i     : index of the player, in int.
     */
    private void wakeUp(int i){
        mListPlayer.get(i).notifyMe();
    }
    
}
