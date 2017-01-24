/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dujuga.peruddos;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis
 */
public class PdosScanner extends Thread {
    public int returnedInt = -2;
    public String returnedStr = null;
    private boolean mForStr;
    private boolean OK = true;
    
    public PdosScanner(boolean forStr){
        mForStr = forStr;
    }
    
    /* Waits for the user to write on stdin an return it as a String */
    private String askEntry(){
        Scanner getFromUser = new Scanner(System.in);
        String returned = "ERROR";
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    /* Prints a the message given in argument then wait for the user to write on stdin an return it as a String */
    private String askEntry(String message){
        Scanner getFromUser = new Scanner(System.in);
        String returned = "ERROR";
        System.out.println(message);
        System.out.print("> ");
        returned = getFromUser.nextLine();
        
        return returned;
    }
    
    /* Waits for the user to write on stdin an return it as a int */
    private int askNumber(){
        Scanner getFromUser = new Scanner(System.in);
        int returned = -2;
        System.out.print("> ");
        returned = getFromUser.nextInt();
        
        return returned;
    }

    public void stopIt(){
        OK = false;
    }
    
    @Override
    public void run(){
         if(mForStr)
             returnedStr = askEntry();
         else
             returnedInt = askNumber();
         
         do{
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(PdosScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
         } while(OK);
    }   
}
