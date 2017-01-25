/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */
public class PdosScanner extends Thread {
    public int returnedInt = -3;
    public String returnedStr = "NONE";
    private boolean mForStr;
    private boolean OK = true;
    
    private static final String errStr = "ERROR";
    
    public PdosScanner(boolean forStr){
        mForStr = forStr;
    }
    
    /* Waits for the user to write on stdin an return it as a String */
    private String askEntry() {
        
        Scanner getFromUser = new Scanner(System.in);
        String returned = errStr;
        PdosScanner p = new PdosScanner(true);
        
        System.out.print("> ");
        returned = getFromUser.nextLine();
        p.start();
        
        try {
            sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PdosClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        p.stopIt();
        System.out.println("-");
                
        return returned;
    }
    
    /* Waits for the user to write on stdin an return it as a int */
    private int askNumber() {
        Scanner getFromUser = new Scanner(System.in);
        int returned = -2;
        //System.out.print("> ");
        returned = getFromUser.nextInt();
        //System.in.reset();
        /*while(getFromUser.hasNextInt())
            getFromUser.nextInt();*/
        return returned;
    }

    public void stopIt(){
        OK = false;
    }
    
    public void run(){
        try {
            sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(PdosScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("DEBUT THREAD");
        /*try {*/
            if(mForStr)
                askEntry();
            else
                returnedInt = askNumber();
            
            System.out.println("IN THREAD : " + returnedStr);
            
            do{
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PdosScanner.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while(OK);
        /*} catch (IOException ex) {
            Logger.getLogger(PdosScanner.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        System.out.println("FIN THREAD");
    }   
}
