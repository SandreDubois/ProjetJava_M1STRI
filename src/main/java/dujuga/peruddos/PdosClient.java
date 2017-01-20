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
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author Dujuga
 */
public class PdosClient {
    /* Créer une méthode permettant le changement de l'adresse par l'utilisateur. */
    private String adresse = "127.0.0.1";
    private String pseudostr;
       
    /* Envoie du pseudo du client  */
    private void envoiePseudo(Socket sockService){
        try{
            Scanner pseudo = new Scanner(System.in);
            System.out.println("Veuillez rentrer votre pseudo : ");
            pseudostr = pseudo.nextLine();
            DataOutputStream oStream = new DataOutputStream(sockService.getOutputStream());
            oStream.writeUTF(pseudostr);
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    
    
    /* Envoie de l'IP et du port du client  */
    private void envoieIP(Socket sockService){
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = sockService.getLocalPort() ;
            DataOutputStream oStream = new DataOutputStream(sockService.getOutputStream());
            oStream.writeUTF(ip+":"+port);
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    private void accueil(){
        System.out.println("++++++++++++++++++++++++++++++++++++++++");        
        System.out.println("+++++++++++++++ PERUDDOS +++++++++++++++");
        System.out.println("++++++++++++++++++++++++++++++++++++++++");        
        System.out.println("            Bonjour "+pseudostr+ "!");
    }
    
    /* Connecte le client au serveur */
    private void gestionSocket() {
        Socket sock = null;
        try{
            sock = new Socket(adresse, 18000);
            System.out.println("Connexion réussi au serveur.");
        }
        catch(IOException ioe){
            System.out.println("Erreur lors de la connexion : " + ioe.getMessage());
        }
        
        envoiePseudo(sock);
        envoieIP(sock);
        accueil();
    }
    
    public static void main(String[] args) {
        PdosClient mainClie = new PdosClient();
        System.out.println("Début client.");
        mainClie.gestionSocket();
    }
    
}
