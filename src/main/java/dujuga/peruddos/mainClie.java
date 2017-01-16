/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author Alexis
 */
public class mainClie {

    private String adresse = "127.0.0.1";
    
    private void envoieInt(Socket sockService, int chiffre){
        try{
            DataOutputStream oStream = new DataOutputStream(sockService.getOutputStream());
            oStream.writeInt(chiffre);
        }
        catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie d'un chiffre : " + ioe.getMessage());
        }
              
    }
    
    private void gestionSocket(){
        Socket sock = null;
        try{
            sock = new Socket(adresse, 18000);
        }
        catch(IOException ioe){
            System.out.println("Erreur lors de la connexion : " + ioe.getMessage());
        }
        
        envoieInt(sock, 3);
    }
    public static void main(String[] args) {
        mainClie mainClie = new mainClie();
        System.out.println("Début client.");
        mainClie.gestionSocket();
    }
    
}
