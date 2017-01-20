/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;


import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alexis
 */
public class PdosGame {
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
        Return  true if the player is accepted ;
                false if not.
    */
    public boolean addPlayer(PdosPlayer newPlayer) throws IOException{
        if(this.getNumberOfPlayers() < 6){ /* If there is less than 6 players in the game */
            mListPlayer.add(newPlayer);
            newPlayer.welcome();
            return true;
        }
        else
            return false;
    }
    
    /*
        Return the id of the game.
    */
    public int getId(){
        return this.mIdPdosGame;
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
    public PdosGame(PdosPlayer creator, int index) throws IOException{ /* Create a game with a main player and a index j */
        mListPlayer = new ArrayList();
        this.addPlayer(creator);
        mIdPdosGame = index; /* Add index as the id of game */
    }
}
