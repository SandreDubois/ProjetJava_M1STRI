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
    
    private static final int errInt = -3;
    private static final String errStr = "ERROR";
    
    private String listen(){
        String message = errStr;
        //System.out.println("J'écoute.");
        try{
            DataInputStream iStream = new DataInputStream(sock.getInputStream());
            message = iStream.readUTF();
        }
        catch(IOException ioe){
                System.out.println("Erreur lors de l'écoute: " + ioe.getMessage());
        }
        
        return message;
    }
    
    /* send a message (in string) given to the socket mSocket. */
    private void send(String message) throws IOException{
            DataOutputStream oStream = new DataOutputStream(sock.getOutputStream());
            oStream.writeUTF(message);
    }
    
    /* send a message (in int) given to the socket mSocket. */
    private void send(int message) throws IOException{
            DataOutputStream oStream = new DataOutputStream(sock.getOutputStream());
            oStream.writeInt(message);
    }
 
    /* Waits for the user to write on stdin an return it as a String */
    private String askEntry(){
        Scanner getFromUser = new Scanner(System.in);
        String returned = errStr;
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    /* Prints a the message given in argument then wait for the user to write on stdin an return it as a String */
    private String askEntry(String message){
        Scanner getFromUser = new Scanner(System.in);
        String returned = errStr;
        System.out.println(message);
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    /* Waits for the user to write on stdin an return it as a int */
    private int askNumber(){
        Scanner getFromUser = new Scanner(System.in);
        int returned = errInt;
        System.out.print("> ");
        returned = getFromUser.nextInt();
        
        return returned;
    }
    
    /* Asks the pseudonym that the user wants then sends to the server the pseudonym  */
    private void getAndSendPseudonyme(Socket sockService){
        String pseudostr = errStr;
        try{
            send(askEntry("Veuillez entrer votre pseudo."));
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    /* Sends ip and port to the server  */
    private void sendIP(){
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = sock.getLocalPort() ;
            send(ip + ":" + port);
        } catch(IOException ioe){
            System.out.println("Erreur lors de l'envoie du pseudo : " + ioe.getMessage());
        }
    }
    
    /* Connect user to the server */
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
                send(askNumber());
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
