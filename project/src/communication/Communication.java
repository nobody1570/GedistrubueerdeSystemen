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
import java.util.Map;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Card;
import model.Card.Colour;
import model.User;

public interface Communication extends Remote {
    
    int login(String name, String password) throws RemoteException;
    public boolean createNewAccount(String username, String password)throws RemoteException;
    
    int getPublicGame(int userID) throws RemoteException, InterruptedException;

    public boolean getStarted(int gameID) throws RemoteException;
    
    public List<User> getSpelersList(int gameID) throws RemoteException;
    public List<Integer> getSpelersHandSize(int gameID) throws RemoteException;
    public List<Card> getHand(int gameID, int userID) throws RemoteException;

    public List<Card> drawCard (int gameID, int userID) throws RemoteException;
    public boolean playCard(int userID, int gameID, Card card) throws RemoteException;
    public boolean playCardAllowed(int gameID, Card c)throws RemoteException;
    
    public void setPrefered(int gameID, int userID, Colour c)throws RemoteException;
    public Colour getCurrentColour(int gameID) throws RemoteException;
    
    public int getLastMove (int gameID) throws RemoteException;
    public Card getLatestPlayedCard(int userID, int gameID, int latestReceivedMove) throws RemoteException;
    public Card getLatestPlayedCard(int gameID) throws RemoteException;
    public boolean myTurn(int gameID, int userID) throws RemoteException;
    public boolean waitForNewCardPlayed(int gameID) throws RemoteException;
    
    public void endGame(int gameID, int userID) throws RemoteException;
    public boolean getFinished(int gameID) throws RemoteException;
    public Map<User, Integer> getScore(int gameID)throws RemoteException;
    
    public boolean logout(int userID) throws RemoteException;
    
    //asks server what the amount of waiting players in the game with the most players is
	public boolean hasWaitingGame()throws RemoteException;

    

    

    

	
}
