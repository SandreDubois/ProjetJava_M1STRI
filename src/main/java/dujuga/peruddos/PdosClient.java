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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dujuga
 */
public class PdosClient {
    /* Créer une méthode permettant le changement de l'adresse par l'utilisateur. */
    private String adresse = "127.0.0.1";
    Socket sock = null;
    
    private String listen(){
        String message = "ERROR";
        //System.out.println("J'écoute.");
        try{
            DataInputStream iStream = new DataInputStream(sock.getInputStream());
            message = iStream.readUTF();
            //System.out.println("J'ai reçu : \"" + message + "\"");
        }
        catch(IOException ioe){
                System.out.println("Erreur lors de l'écoute: " + ioe.getMessage());
        }
        
        return message;
    }
    
    /* for string */
    private void send(String message) throws IOException{
            //System.out.println("J'envoie \"" + message + "\"");
            DataOutputStream oStream = new DataOutputStream(sock.getOutputStream());
            oStream.writeUTF(message);
    }
    
    /* for int */
    private void send(int message) throws IOException{
            //System.out.println("J'envoie \"" + message + "\"");
            DataOutputStream oStream = new DataOutputStream(sock.getOutputStream());
            oStream.writeInt(message);
    }
 
    private String askEntry(){
        Scanner getFromUser = new Scanner(System.in);
        String returned = null;
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    private String askEntry(String message){
        Scanner getFromUser = new Scanner(System.in);
        String returned = null;
        System.out.println(message);
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    /* Envoie du pseudo du client  */
    private void envoiePseudo(Socket sockService){
        String pseudostr = null;
        try{
            send(askEntry("Veuillez entrer votre pseudo."));
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    /* Envoie de l'IP et du port du client  */
    private void sendIP(){
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = sock.getLocalPort() ;
            send(ip + ":" + port);
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    /* Connecte le client au serveur */
    private void socketHandler() throws IOException {
        boolean cont = true;
        String message = null;
        int chiffre = 5;
        
        try{
            sock = new Socket(adresse, 18000);
            System.out.println("Connexion réussi au serveur.");
        }
        catch(IOException ioe){
            System.out.println("Erreur lors de la connexion : " + ioe.getMessage());
        }
                
        /* Boucle de dialogue */
        do{
            message = listen();
            if(message.compareTo("WAITFOR INT") == 0){
                send(chiffre);
            }
            else if(message.compareTo("WAITFOR STR") == 0){
                send(askEntry());
            }
            else if(message.compareTo("WAITFOR IP") == 0){
                sendIP();
            }
            else if(message.compareTo("END") == 0){
                cont = false;
            }
            else
                System.out.println("[SERVEUR] " + message);
        } while(cont); 
        
    }
    
    public static void main(String[] args) {
        PdosClient mainClie = new PdosClient();
        System.out.println("Début client.");
        
        try {
            mainClie.socketHandler();
        } catch (IOException ex) {
            Logger.getLogger(PdosClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
