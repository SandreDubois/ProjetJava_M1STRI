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
    
    private PdosServer mDaddy;
    private int mIdPdosGame;
    private ArrayList <PdosPlayer>mListPlayer; /* ArrayList where players will be. */
    
    
    private int mNumber = 1;
    private int mValor = 1;
    
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
        newP.setGame(this);
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
            //ejectPlayer(i);
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
        //String msg = mListPlayer.get(i).getPseudonym();
        mListPlayer.get(i).setInGame(false);
        mListPlayer.get(i).setGame(null);
        mListPlayer.remove(i);
        //broadcast("Le joueur " + msg + " s'est déconnecté.");
    }
    
    /**
     * Broadcast to every players in the party that the creator won then ejects every players. 
     */
    private void endGame(){
        broadcast("Un gros GG à " + mListPlayer.get(0).getPseudonym() + " : victoire écrasante !");
        
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
    
    private boolean outbid(int index){
        int nbDice = 0;
        int valorDice = 0;
        boolean loseConnection = false;
        
        do{
            do{
                try {
                    nbDice = mListPlayer.get(index).listenInt("Enchère - Veuillez donner un nombre de dés : ");
                } catch (IOException ex) {
                    System.out.print("Il y avait " + mListPlayer.size());
                    loseConnection = true;
                }
                if(nbDice < 1 && !loseConnection)
                        sendTo(index, "Veuillez donner une valeur supérieure à 0.");
            } while(nbDice < 1 && !loseConnection);

            System.out.println("On sort de la première boucle");
            
            do{
                try {
                    valorDice = mListPlayer.get(index).listenInt("Enchère - Veuillez donner une valeur de dés : ");
                } catch (IOException ex) {
                    System.out.print("Il y avait " + mListPlayer.size());
                    loseConnection = true;
                }
                if((valorDice < 1 || valorDice > 6) && !loseConnection)
                    sendTo(index, "Veuillez donner une valeur comprise dans [1,6].");
            } while((nbDice < 1 || valorDice > 6) && !loseConnection);
            
            
        } while(!(nbDice > mNumber || valorDice > mValor) && !loseConnection);
        
        if(!loseConnection){
            mNumber = nbDice;
            mValor = valorDice;
            broadcast(mListPlayer.get(index).getPseudonym() + " a parié : " + mNumber + " " + mValor);
        }
        
        return loseConnection;
    }
    
    private void liar(int index){
        int jCurrent = index;
        int jPrec = index - 1;
        int cptDice = 0;
        
        if(jCurrent == 0)
            jPrec = mListPlayer.size() - 1;
        
        broadcast(mListPlayer.get(jCurrent).getPseudonym() + " a dénoncé " + mListPlayer.get(jPrec).getPseudonym() + ".");
        
        for(int i = 0; i < mListPlayer.size(); i++){
            cptDice += mListPlayer.get(i).getThatDice(mValor);
        }
        
        broadcast("Il y a " + cptDice + " dés de valeur " + mValor + ".");
        
        if(cptDice >= mNumber){
            broadcast(mListPlayer.get(jPrec).getPseudonym() + " n'a pas menti !");
            broadcast(mListPlayer.get(jCurrent).getPseudonym() + " perd un dé !");
            mListPlayer.get(jCurrent).loseDice();
            firstProposition(jCurrent);
        }
        else{
            broadcast(mListPlayer.get(jPrec).getPseudonym() + " a bel et bien menti !");
            broadcast(mListPlayer.get(jPrec).getPseudonym() + " perd un dé !");
            mListPlayer.get(jPrec).loseDice();
            firstProposition(jPrec);
        }
            
    }
    
    private void exactly(int index){
        int cptDice = 0;
        
        broadcast(mListPlayer.get(index).getPseudonym() + " tente un tout pile !");
        
        for(int i = 0; i < mListPlayer.size(); i++){
            cptDice += mListPlayer.get(i).getThatDice(mValor);
        }
        
        broadcast("Il y a " + cptDice + " dés de valeur " + mValor + ".");
        
        if(cptDice == mNumber){
            broadcast(mListPlayer.get(index).getPseudonym() + " a réussi son tout pile ! Tu gagnes un dé !");
            mListPlayer.get(index).addDice();
        }
        else{
            broadcast("Bien essayé " + mListPlayer.get(index).getPseudonym() + ", mais c'est raté ! Tu perds un dé.");
            mListPlayer.get(index).loseDice();
        }
            
    }
    
    private void ping(int index) throws IOException{
        sendTo(index, "PING");
        mListPlayer.get(index).listen(2);
    }
    
    private void loseConnectionDuringGame(int index, boolean firstP){
        int nextPlayer = index;
        
        ejectPlayer(index);
        
        if(nextPlayer == mListPlayer.size())
            nextPlayer = 0;
        
        if(firstP)
            firstProposition(nextPlayer);
        else
            proposition(nextPlayer);
        
    }
    
    /**
     * Handle the proposition and the turn of the first player.
     * @param index     : index of the current player.
     */
    private void firstProposition(int index){
        int sommDice = 0;
        String resp = null;        
        mNumber = 0;
        mValor = 0;
        int nextPlayer;
        boolean loseConnection = false;
                
        everybodyToss(); /* Mélange de tous les dés */
        
        /* Vérification si le joueur a toujours des dés */
        if(didThePlayerLose(index) && index == mListPlayer.size()){
            index = 0;
        }
        System.out.println(index);
        broadcast(" - Tour de " + mListPlayer.get(index).getPseudonym() + " - "); /* annonce générale */
        
        /* Verify there is only one player left */
        if(mListPlayer.size() != 1){
            /* Envoie du nombre de dés restant */
            for(int i = 0; i < mListPlayer.size(); i++)
                sommDice += mListPlayer.get(i).getNumberOfDices();

            sendTo(index, "Il reste : " + sommDice + " dés.");

            loseConnection = outbid(index);

            if(!loseConnection){
                nextPlayer = index + 1;

                if(index == mListPlayer.size()-1)
                    nextPlayer = 0;

                proposition(nextPlayer);
            }
            else
                loseConnectionDuringGame(index, true);
        }
    }
    
    private void proposition(int index){
        int sommDice = 0;
        String resp = "0";
        int valorDice = 0;
        int nbDice = 0;
        int nextPlayer;
        int precPlayer;
        boolean loseConnection = false;
        
        /* Vérification si le joueur a toujours des dés */
        if(didThePlayerLose(index) && index == mListPlayer.size()){
            index = 0;
        }
        
        broadcast(" - Tour de " + mListPlayer.get(index).getPseudonym() + " - "); /* annonce générale */

        /* Verify there is only one player left */
        if(mListPlayer.size() != 1){
            /* Envoie du nombre de dés restant */
            for(int i = 0; i < mListPlayer.size(); i++)
                sommDice += mListPlayer.get(i).getNumberOfDices();

            this.sendTo(index, "Il reste : " + sommDice + " dés.");

            precPlayer = index - 1;
            nextPlayer = index + 1;

            if(index == mListPlayer.size()-1)
                nextPlayer = 0;
            if(index == 0)
                precPlayer =  mListPlayer.size()-1;

            do{
                sendTo(index, "Que voulez-vous faire ? ( 1: Surenchérir/ 2: Menteur/ 3: Tout pile.");

                try {
                    resp = mListPlayer.get(index).listen(2);
                } catch (IOException ex) {
                    //TBC
                    System.out.println("DECONNEXION !");
                    loseConnection = true; /* il faut sortir de cette méthode */
                }            

                switch(resp){
                    case "1": /* Surenchère */
                        outbid(index);
                        proposition(nextPlayer);
                        break;
                    case "2": /* Menteur */
                        liar(index);
                        break;
                    case "3": /* Tout pile */
                        exactly(index);
                        firstProposition(index);
                        break;
                    default: /* rien */
                        System.out.println("On passe dans le default.");
                        break;
                }

            } while(resp.compareTo("1") != 0 && resp.compareTo("2") != 0 && resp.compareTo("3") != 0 && loseConnection == false && !didAPlayerWin());

            if(loseConnection){
                loseConnectionDuringGame(index, false);
            }
        }
    }
    
    /**
     * Verify if the player placed in index place has lose.
     * @param index     : index of the PdosPlayer.
     * @return          : if the player has lose.
     */
    private boolean didThePlayerLose(int index){
        if(index < mListPlayer.size()){
            boolean returned = (mListPlayer.get(index).hasDice() && mListPlayer.get(index).isAlive());
            
            if(!returned){
                broadcast(mListPlayer.get(index).getPseudonym() + " a perdu et est éliminé.");
                this.ejectPlayer(index);
            }
            
            return returned;
        }
        return false;
    }
    
    private boolean didAPlayerWin(){
        return (mListPlayer.size() == 1);
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
        int currentJ; /* variable contenant l'id du joueur en train de jouer */
        
        sendTo(0, "Vous avez créer une partie.");
        
        waitingLoop();
        
        if(!mListPlayer.isEmpty()){
            broadcast("La partie est pleine !");
            wakeUpAllForStart();
            try {
                wait(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /* There's some players */
            
            /* variable contenant l'id du joueur courant */
            currentJ = (int) (Math.random() * mListPlayer.size());
            System.out.println("Premier tour");
            broadcast("La partie commence !.");
            firstProposition(currentJ);
            
            wakeUpAll();
            endGame();
        }
        
        unregister();
        System.out.println("Fin de partie.");
    }
    
    /**
     * Send the message given in parameters to the PdosPlayer with the numberPlayer index in the ArrayList of PdosPlayer.
     * @param numberPlayer  : index, in int, of the player.
     * @param message       : message, in String, to send.
     */
    private void sendTo(int numberPlayer, String message) {
        if(numberPlayer >= 0 && numberPlayer < mListPlayer.size()){
            try{
                mListPlayer.get(numberPlayer).send(message);
            } catch (IOException ex) {
                ejectPlayer(numberPlayer);
            }
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
                sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(mListPlayer.size() < 6 && !mListPlayer.isEmpty());
        
    }

    public synchronized void notifyMe(){
        this.notify();
    }
    
    /**
     * Invokes the wakeUp() method on all players.
     */
    private void wakeUpAll(){
        for(int i = 0; i < mListPlayer.size(); i++)
            wakeUp(i);
    }
    
    private void wakeUpAllForStart(){
        for(int i = 0; i < mListPlayer.size(); i++)
            mListPlayer.get(i).notifyAsk();
    }
    
    /**
     * Invokes the notifyMe() method of the ith player in the ArrayList of PdosPlayer. 
     * @param i     : index of the player, in int.
     */
    private void wakeUp(int i){
        mListPlayer.get(i).notifyMe();
    }
    
}
