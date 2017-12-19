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
import java.awt.image.BufferedImage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import model.Card;
import model.Card.Colour;
import model.Game;
import model.User;

public interface Communication extends Remote {
    
    public String login(String name, String password) throws RemoteException;
    public boolean login(String token) throws RemoteException;
    public boolean createNewAccount(String username, String password)throws RemoteException;
    
    public int getPublicGame(String token) throws RemoteException, InterruptedException;
    
    public boolean startGame(int gameID, boolean priv) throws RemoteException;
    public boolean getStarted(int gameID, boolean priv) throws RemoteException;
    
    public List<User> getSpelersList(int gameID, boolean priv) throws RemoteException;
    public List<Integer> getSpelersHandSize(int gameID, boolean priv) throws RemoteException;
    public List<Card> getHand(int gameID, String token, boolean priv) throws RemoteException;

    public List<Card> drawCard (int gameID, String token, boolean priv) throws RemoteException;
    public boolean playCard(String token, int gameID, Card card, boolean priv) throws RemoteException;
    public boolean playCardAllowed(int gameID, Card c, boolean priv)throws RemoteException;
    
    public void setPrefered(int gameID, String token, Colour c, boolean priv)throws RemoteException;
    public Colour getCurrentColour(int gameID, boolean priv) throws RemoteException;
    
    public int getLastMove (int gameID, boolean priv) throws RemoteException;
    public Card getLatestPlayedCard(int userID, int gameID, int latestReceivedMove, boolean priv) throws RemoteException;
    public Card getLatestPlayedCard(int gameID, boolean priv) throws RemoteException;
    public boolean myTurn(int gameID, String token, boolean priv) throws RemoteException;
    public boolean waitForNewCardPlayed(int gameID, boolean priv) throws RemoteException;
    
    public void endGame(int gameID, String token, boolean priv) throws RemoteException;
    public boolean getFinished(int gameID, boolean priv) throws RemoteException;
    public Map<User, Integer> getScore(int gameID, boolean priv)throws RemoteException;
    
    public String getCardback(String theme) throws RemoteException;
    
    public boolean logout(String token) throws RemoteException;
    
    //asks server what the amount of waiting players in the game with the most players is
	public boolean hasWaitingGame()throws RemoteException;

    public List<Game> getAllParticipatingGames(String token)throws RemoteException;

    public List<Game> getAllPrivateGames(String token)throws RemoteException;
    
    public int getCreatePrivateGame(String token, int maxUsers)throws RemoteException;
    
    public boolean joinPrivateGame(int gameID,String token) throws RemoteException;

    

    

    

	
}
