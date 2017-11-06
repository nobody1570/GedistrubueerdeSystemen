/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;
import gui.GameController;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import model.Card;
/**
 *
 * @author Jonas
 */
public class UpdateGameThread extends Thread{

	private GameController gc;
	private int gameID;
        private int userID;
        private Registry myRegistry; Communication impl;
        private int latestReceivedMove;
	private Boolean finished;

    public UpdateGameThread(GameController gc, int gameID,int userID, Registry myRegistry, Communication impl) {
	// TODO Auto-generated constructor stub
        this.gc = gc;
	this.gameID = gameID;
        this.userID = userID;
	this.myRegistry=myRegistry;
	this.impl=impl;
	this.finished = false;
        latestReceivedMove = -1;
    }

    public void run(){
        try {
            Card c, previous=null;
            while (!finished) {
                System.out.println("runloop");
                
                c = impl.getLatestPlayedCard(gameID);
                
                if(!c.equals(previous)){
                    previous = c;
                    finished = !impl.getStarted(gameID);    
                    gc.update();
                }
            
                sleep(500);
                
            }

        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}