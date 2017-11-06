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
import model.Database;
import model.Game;
import model.User;

public class CommunicationImpl extends UnicastRemoteObject implements Communication{
	private Database db;
	private List<Game> games;
	private Set<User> userList;
	
	
	public CommunicationImpl () throws RemoteException{
            db = new Database();
            games = new ArrayList<Game>();
            userList = new HashSet<User>();
		
	}
	@Override
        public boolean createNewAccount(String username, String password) throws RemoteException{
            if (db.readUser(username) == null){
                User u = new User(db.getHighestID()+1,username,password,"");
                db.createUser(u);
                return true;
            }
            return false;
        }
        
        @Override
	public String login(String name, String pw) throws RemoteException {
            //controleer login in DB
            User u = db.readUser(name);
            
            
		if (u != null && pw.equals(u.getPassword())){
			userList.add(u);
                        //token returnen
			return "ok";
		}
		return "nok";
	}
        
	
	
	@Override
        public int getPublicGame(String name) throws RemoteException, InterruptedException{
            //kijken voor plaats in de games
            User u = db.readUser(name);
            int gameFound = -1;Game game;
            for (int i=0; i<games.size();i++){
                game = games.get(i);
                if (game.getAmountOfPlayers()<game.MAX_USERS){
                    //plaats gevonden add player
                    
                    game.addPlayer(u);
                    gameFound = i;
                    
                }
            }
            if(gameFound<0){
                gameFound = games.size();
                Game g = new Game(gameFound);
                g.addPlayer(u);
                
                games.add(g);
                
            }
            
            
            return gameFound;
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
        public List<Card> getHand(int gameID, int userID){
            User u = db.readUser(userID);
            //lookup in DB op gameiD
            Game game = getGameByID(int gameID);
            List<Card> hand = game.getHand(u);
            return null;
        }

    @Override
    public List<Card> drawCard(int gameID, int userID) throws RemoteException {
        //get user by id
        User u = db.readUser(userID);
        //get game by id
        Game g;
        g= getGameByID(int gameID);
        
        //perform turn
        g.performTurn(u, null);
        //get playerhand
        List<Card> hand = g.getHand(u);
        return hand;
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
    
    public Game getGameByID(int gameID){
        for (Game g : games){
            if(g.getId()==gameID){
                return g;
            }
        }
        return null;
    }

    
}
