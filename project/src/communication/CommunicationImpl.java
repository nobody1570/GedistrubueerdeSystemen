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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Card;
import model.Game;
import model.User;

public class CommunicationImpl extends UnicastRemoteObject implements Communication{
	
	private Set<Game> games;
	private Set<String> userList;
	
	
	public CommunicationImpl () throws RemoteException{
		games = new HashSet<Game>();
		userList = new HashSet<String>();
		
	}
	@Override
        public boolean createNewAccount(String username, String password) throws RemoteException{
            if (!userList.contains(username)){
                userList.add(username);
                return true;
            }
            return false;
        }
        
        @Override
	public String login(String name, String pw) throws RemoteException {
            //controleer login in DB
		if (userList.contains(name)){
			
                        //token returnen
			return "ok";
		}
		return "nok";
	}
        
	
	
	@Override
        public int getPublicGame(String name) throws RemoteException, InterruptedException{
            //kijken voor plaats in de games
            /*games.forEach( game ->{
                if (game.users.size()<game.MAX_USERS){
                    //plaats gevonden add player
                    User u = db.getUser(name);
                    game.addPlayer(u);
                }
            });*/
            
            
            return 0;
        }

	
	
	@Override
	public String logout(String name) throws RemoteException {
            //disable token
		if (userList.contains(name)){
			userList.remove(name);
			return "ok";
		}
		return "logout failed!";
	}

	@Override
	public String getSpelersList(int gameID) throws RemoteException {	
            //get spelers in current game
		return userList.toString();
	}
	@Override
        public List<Card> getHand(int gameID, String userName){
            //lookup in DB op gameiD
            Game game = new Game();
            List<Card> hand = game.getHand(userName);
            return null;
        }

    @Override
    public Card drawCard(String userName, int gameID) throws RemoteException {
        //controleer als speler aan beurt is
        //neem kaart van stapel (beurt +)
        //return kaart
        //volgende speler is aan zet
        Card c = new Card();
        return c;
    }

    @Override
    public synchronized Card getLatestPlayedCard(String userName, int gameID, int latestReceivedMove) throws RemoteException {
        //get game, look in game for latest card
        Game g = new Game();
        Card c = new Card();
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
        
    }
        
    @Override
    public synchronized void playCard(String name, int GameID, Card card) throws RemoteException{
        //zoek game met id in db en return
        //controleer als card is playable adhv last played card
        //set last palyed card
        //notify all (getLastPlayedCard)
            
    }

    
}
