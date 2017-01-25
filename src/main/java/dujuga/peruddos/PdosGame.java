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
    private PdosServer mDaddy;
    
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
    
    public void setIdGame(int i){
        mIdPdosGame = i;
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
    public PdosGame(PdosPlayer creator, int index, PdosServer daddy){ /* Create a game with a main player and a index j */
        mListPlayer = new ArrayList();
        mIdPdosGame = index; /* Add index as the id of game */
        mDaddy = daddy;
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
            mListPlayer.get(numberPlayer).send(message);
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
    
    private void wakeUpAll(){
        for(int i = 0; i < mListPlayer.size(); i++)
            wakeUp(i);
    }
    
    private void wakeUp(int i){
        mListPlayer.get(i).notifyMe();
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
            
            if(!mListPlayer.get(0).isAlive()){
                broadcast("Le créateur est parti.");
                if(mListPlayer.isEmpty()){
                    endGame();
                    unregister();
                }
                else{
                    broadcast("Le nouveau créateur est :" + this.getCreatorPseudonym());
                }
            }
            
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(mListPlayer.size() < 6);
        
    }
    
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
    
    @Override
    public void run(){
        sendTo(0, "Vous avez créer une partie.");
        
        waitingLoop();
        
        broadcast("La partie est pleine !");
        
        try {
            sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        endGame();
        wakeUpAll();
        unregister();
    }
}
