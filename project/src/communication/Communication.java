/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author Jonas
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Card;

public interface Communication extends Remote {
    
    String login(String name, String password) throws RemoteException;
    public boolean createNewAccount(String username, String password)throws RemoteException;
    
    int getPublicGame(String name) throws RemoteException, InterruptedException;

    
    
    String getSpelersList(int gameID) throws RemoteException;

    public List<Card> getHand(int gameID, String userName) throws RemoteException;

    public Card drawCard (String userName, int gameID) throws RemoteException;
    public Card getLatestPlayedCard(String userName, int gameID, int latestReceivedMove) throws RemoteException;
    
    public void playCard(String userName, int gameID, Card card) throws RemoteException;

    String logout(String name) throws RemoteException;

    

	
}
