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
}
