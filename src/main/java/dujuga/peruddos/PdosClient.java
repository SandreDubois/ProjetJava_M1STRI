/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import static java.lang.Thread.sleep;
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
    
    String mMessage = "PDOSNULL";
    
    private static final int errInt = -3;
    private static final String errStr = "NONE";
    
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
    private void sendInt(int message) throws IOException{
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
        System.out.println(message);
        return askEntry();
    }
    
    /* Waits for the user to write on stdin an return it as a int */
    private int askNumber(){
        Scanner getFromUser = new Scanner(System.in);
        int returned = errInt;
        System.out.print("> ");
        returned = getFromUser.nextInt();
        
        return returned;
    }
    
    private int askNumber(String message){
        System.out.println(message);
        return askNumber();
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
    
    private int initSocket(){
        int OK = 0;
        
        do{
            adresse = askEntry("Veuillez entrer l'adresse ip du serveur.");
            try{
                sock = new Socket(adresse, 18000);
                System.out.println("Connexion réussi au serveur.");
                OK = 1;
            }
            catch(IOException ioe){
                System.out.println("Connexion impossible : " + ioe.getMessage());
                
                OK = askEntry("Réessayer ? [YES/NO]").compareTo("YES");    
                System.out.println(OK);
            }
        } while(OK == 0);
        
        return OK;
    }
    
    /* Connect user to the server */
    private void socketHandler() throws IOException {
        boolean cont = true;
        String message = null;
        int chiffre = 5, cptNone = 0;
        
                        
        /* Boucle de dialogue */
        do{
            message = listen();
            if(message.compareTo("WAITFOR INT") == 0){
                sendInt(askNumber());
                cptNone = 0;
            }
            else if(message.compareTo("WAITFOR STR") == 0){
                send(askEntry());
                cptNone = 0;
            }
            else if(message.compareTo("WAITFOR PRO") == 0){
                send(askEntry());
                cptNone = 0;
                
            }
            else if(message.compareTo("WAITFOR IP") == 0){
                sendIP();
                cptNone = 0;
            }
            else if(message.compareTo("END") == 0 || cptNone > 5){
                cont = false;
                cptNone = 0;
            }
            else if(message.compareTo("PING") == 0){
                send("return ping");
                cptNone = 0;
            }
            else if(message.compareTo("NONE") == 0){
                cptNone++;
            }
            else
                System.out.println("[SERVEUR] " + message);
            
        } while(cont); 
        
    }
        
    public static void main(String[] args) {
        PdosClient mainClie = new PdosClient();
        System.out.println("Début client.");
        
        if(mainClie.initSocket() == 1){
            try {
                mainClie.socketHandler();
            } catch (IOException ex) {
                System.out.println("Oops.");
            }
        }
        System.out.println("Bonne soirée !");
    }
    
}
