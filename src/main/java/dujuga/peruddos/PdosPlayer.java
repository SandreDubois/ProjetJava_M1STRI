/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author quentin
 */
public class PdosPlayer {
        private int mIdJoueur;
        private Socket mSocket;         /* Add the socket of the client */
        private String mPseudo;
        private int mNbDes;
        private String mIp;
        private ArrayList <PdosDice>mDes; /* Declares an ArrayList of PdosDice named mDes */
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
            DataOutputStream oStream = new DataOutputStream(mSocket.getOutputStream());
            oStream.writeUTF("Vous avez rejoint une partie.");
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
        
}
