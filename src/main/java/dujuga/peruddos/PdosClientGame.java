/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */
public class PdosClientGame extends PdosGame {
    
    ArrayList <PdosPlayer> mListPlayer = new ArrayList();
    
    public PdosClientGame(PdosPlayer creator, int index) {
        super(creator, index);
        mListPlayer.add(creator);
    }
    
    @Override
    public void run(){
        int currentJ; /* variable contenant l'id du joueur en train de jouer */
        
        sendTo(0, "Vous avez créer une partie.");
        
        waitingLoop();
        
        if(!mListPlayer.isEmpty()){
            broadcast("La partie est pleine !");
            wakeUpAllForStart();
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for(int i = 0; i < mListPlayer.size(); i++){
                mListPlayer.get(i).initializePlayer();
            }
            
            /* variable contenant l'id du joueur courant */
            currentJ = (int) (Math.random() * mListPlayer.size());
            System.out.println("Premier tour");
            broadcast("La partie commence !");
            firstProposition(currentJ);
            
            wakeUpAll();
            endGame();
        }
        
        //unregister();
        System.out.println("Fin de partie.");
    }
    
    private void waitingLoop(){
        ServerSocket sockEcoute = null;    //Déclaration du serverSocket.
        Socket sockService = null;         //Déclaration du socket de service.
        boolean getClient = true;   //Permet de stopper l'écoute de nouveaux clients.
        /* Rappel des étapes d'une connexion : */
            /* Création sock écoute + bind */
            try{
                sockEcoute = new ServerSocket(18050);

                while(getClient){
                    try{
                        sockService = sockEcoute.accept();
                    }
                    catch(IOException ioe){
                        System.out.println("Erreur de création du socket service : " + ioe.getMessage());
                    }

                    mListPlayer.add(new PdosPlayer(sockService, mListPlayer.size(), this));
                    mListPlayer.get(mListPlayer.size()-1).start();
                    broadcast("Un joueur a rejoint la partie (" + mListPlayer.size() + "/6)");
                    
                    if(mListPlayer.size() >= 6){
                        getClient = false;
                        sleep(2000);
                    }
                }
            }
            catch(IOException ioe){
                System.out.println("Erreur de création du server socket : " + ioe.getMessage());
            } catch (InterruptedException ex) {
            Logger.getLogger(PdosClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
