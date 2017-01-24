/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;


import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */
public class PdosGame extends Thread {
    private int mIdPdosGame;
    //private PdosPlayer mCreator; /* Not needed : the creator is the first player in the list. */
    private ArrayList <PdosPlayer>mListPlayer; /* ArrayList where players will be. */
    
    /*
        Return the number of player in the game.
    */
    public int getNumberOfPlayers(){
        return mListPlayer.size();
    }
        
    /*
        Return the id of the game.
    */
    public int getIdGame(){
        return this.mIdPdosGame;
    }
    
    public String getCreatorPseudonym(){
        PdosPlayer p = mListPlayer.get(0);
        return p.getPseudonym();
    }
    
    public void everybodyToss(){
        for(int i = 0; i < mListPlayer.size(); i++){
            mListPlayer.get(i).tossDices();
            mListPlayer.get(i).showDices();
        }
    }
    
    /*
        Constructor of the class.
            Needs a player to create it
                    and an index.
    */
    public PdosGame(PdosPlayer creator, int index){ /* Create a game with a main player and a index j */
        mListPlayer = new ArrayList();
        mIdPdosGame = index; /* Add index as the id of game */
        mListPlayer.add(creator);
    }
    
    public int askToJoin(PdosPlayer newP){
        /* To many players are in game */
        if(mListPlayer.size() >= 6){
            return -1;
        }
        
        mListPlayer.add(newP);
        return mListPlayer.size()-1;
    }
    
    private void sendTo(int numberPlayer, String message){
        if(numberPlayer >= 0 && numberPlayer < mListPlayer.size()){
            try {
                mListPlayer.get(numberPlayer).send(message);
            } catch (IOException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    private void broadcast(String message){
        for(int i = 0; i < mListPlayer.size(); i++){
            sendTo(i, message);
        }
    }
    
    private void ejectPlayer(int i){
        sendTo(i, "Vous n'êtes plus dans la partie.");
        mListPlayer.get(i).setInGame(false);
    }
    
    private void endGame(){
        broadcast("Un gros GG à " + this.getCreatorPseudonym() + " : victoire écrasante !");
        
        for(int i = mListPlayer.size() - 1; i >= 0; i--)
            ejectPlayer(i);
    }
    
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
            
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(mListPlayer.size() < 6);
        
    }
    
    @Override
    public void run(){
        sendTo(0, "Vous avez créer une partie.");
        
        waitingLoop();
        
        broadcast("La partie est pleine !");
        
        do{
            try {
                sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(true);
    }
}
