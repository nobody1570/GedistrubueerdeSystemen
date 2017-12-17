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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import CommunicationControllers.InterfaceDBController;
import model.Card;
import model.Card.Colour;
import model.Game;
import model.User;


public class CommunicationImpl extends UnicastRemoteObject implements Communication{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private Database db;
	
    private List<Game> games;
    private Set<User> userList;
	private DatabaseCommunication db;
	SecureRandom random;

    public CommunicationImpl (DatabaseCommunication dBImpl) throws RemoteException{
        this.db = dBImpl;
        games = new ArrayList<Game>();
        userList = new HashSet<User>();
        random = new SecureRandom();

    }

    @Override
    public boolean createNewAccount(String username, String password) throws RemoteException{
    	
    	
        System.out.println("create user rmi");
        
       
        if (db.readUser(username) == null){
            System.out.println("user bestaat niet");
            
            //create salt
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            //System.out.println(chars);
            char[] charArray=chars.toCharArray();
            StringBuilder sb=new StringBuilder("");
            
            int max=chars.length();
            for(int i=0;i<21;i++) {
            	sb.append(charArray[random.nextInt(max)]);
            }
            String salt=sb.toString();
            
           // System.out.println(salt);
            
            //TODO hash password
            MessageDigest digest;
			try {
				
			digest = MessageDigest.getInstance("SHA-512");
			String text=password+salt;
			byte[] output=digest.digest(Base64.getDecoder().decode(text));
			System.out.println("digest succeeded");
			String pw=DatatypeConverter.printHexBinary(output);
            System.out.println(pw);
            
            User u = new User(db.getNextID(),username,salt,pw);
            db.createUser(u);
            System.out.println("user created");
        
            return true;
            } catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return false;
    }

    @Override
    public int login(String name, String pw) throws RemoteException {
        //controleer login in DB
    	
    	
    	
        System.out.println("login rmi");
        User u = db.readUser(name);
        
        //System.out.println(pw + " && "+ u.getPassword());
        
        
        //hashen paswoord met salt
        try {
			MessageDigest digest=MessageDigest.getInstance("SHA-512");
			String text=pw+u.getSalt_password();
			byte[] output=digest.digest(Base64.getDecoder().decode(text));
			pw=DatatypeConverter.printHexBinary(output);

            if (u != null && pw.equals(u.getPassword())){
                    userList.add(u);
                    //token returnen
                    return u.getId();
            }
            } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            return -1;
    }

    @Override
    public int getPublicGame(int userID) throws RemoteException, InterruptedException{
        //kijken voor plaats in de games
        User u = getUserByID(userID);
        int gameFound = -1;
        Game game;
        for (int i=0; i<games.size();i++){
            game = games.get(i);
            if (game.getAmountOfPlayers()<Game.MAX_USERS && !game.getStarted() && !game.getFinished()){
                //plaats gevonden add player

                game.addPlayer(u);
                gameFound = i;
                if(game.getAmountOfPlayers()==Game.MAX_USERS){
                    game.start();
                }
            }
        }
        if(gameFound<0){
            gameFound = games.size();
            Game g = new Game(gameFound);
            g.addPlayer(u);

            games.add(g);

        }
        System.out.println(games.size());
        System.out.println(games);
        
        return gameFound;
    }

    @Override
    public boolean logout(int userID) throws RemoteException {
        //disable token
        User u = getUserByID(userID);
        
        if (userList.contains(u)){
                userList.remove(u);
                return true;
        }
        return false;
    }

    @Override
    public List<User> getSpelersList(int gameID) throws RemoteException {	
        //get spelers in current game
        Game g = getGameByID(gameID);
        
        return g.getPlayers();
    }
    
    @Override
    public List<Integer> getSpelersHandSize(int gameID) throws RemoteException {
        Game g = getGameByID(gameID);
        List<Integer> handSizes = new ArrayList<>();
        for (User u: g.getPlayers()){
            handSizes.add(g.getHand(u).size());
        }
        return handSizes;
    }
    
    @Override
    public List<Card> getHand(int gameID, int userID){
        User u = getUserByID(userID);
        //lookup in DB op gameiD
        Game g = getGameByID(gameID);
        List<Card> hand = g.getHand(u);
        return hand;
    }

    @Override
    public synchronized List<Card> drawCard(int gameID, int userID) throws RemoteException {
        //get user by id
        User u = getUserByID(userID);
        //get game by id
        Game g = getGameByID(gameID);
        
        //perform turn
        if(g.performTurn(u, null)){
            //get playerhand
            List<Card> hand = g.getHand(u);
            notifyAll();
            return hand;
        }
        else {
            //zou nooit mogen gebeuren
            return null;
        }
        
    }

    @Override
    public synchronized Card getLatestPlayedCard(int userID, int gameID, int latestReceivedMove) throws RemoteException {
        System.out.println("server: getlatestCard");
        //get game, look in game for latest card
        Game g = getGameByID(gameID);
        Card c = null;
        try {
            
            wait();
            c = g.getLastCard();
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
        
    }
    
    @Override
    public Card getLatestPlayedCard(int gameID) throws RemoteException {
        System.out.println("server: getlatestCard");
        //get game, look in game for latest card
        Game g = getGameByID(gameID);
 
        return g.getLastCard();
        
    }
        
    @Override
    public synchronized boolean playCard(int userID, int gameID, Card card) throws RemoteException{
        System.out.println("server: playcard");
        //zoek game met id in db en return
        //controleer als card is playable adhv last played card
        //set last palyed card
        //notify all (getLastPlayedCard)
        boolean succes;
        Game g = getGameByID(gameID);
        User u = getUserByID(userID);
        succes = g.performTurn(u, card);
        notifyAll();
        return succes;
    }
    @Override
    public void setPrefered(int gameID, int userID, Colour c)throws RemoteException{
        Game g = getGameByID(gameID);
        User u = getUserByID(userID);
        g.SetPreferredColour(u, c);
    }
    
    @Override
    public boolean getStarted(int gameID) throws RemoteException {
        Game g = getGameByID(gameID);
        return g.getStarted();
    }
    
    public Game getGameByID(int gameID){
        for (Game g : games){
            if(g.getId()==gameID){
                return g;
            }
        }
        return null;
    }
    
    public User getUserByID(int userID){
        for (User u: userList){
            if(u.getId()==userID){
                return u;
            }
            
        }
        User u;
		try {
			u = db.readUser(userID);
		
        return u;
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
        	
			e.printStackTrace();
			return null;
		}
		
    }

    @Override
    public synchronized boolean myTurn(int gameID, int userID) throws RemoteException{
        System.out.println("myturn serverside");
        User u = getUserByID(userID);
        Game g = getGameByID(gameID);
        boolean yourTurn = true;
        //zoland je niet aan de beurt bent en het spel nog niet gedaan is
        while(!u.equals(g.getPlayers().get(g.getTurn())) && !g.getFinished()){
            
            System.out.println("speler: "+ u + "wacht op beurt: "+g.getTurn());
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("gedaan met wachten myturn: "+u+ "turn is nu: "+g.getTurn());
            
        }
        if(g.getFinished()){
                System.out.println("game gedaan");
                yourTurn = false;
            }
        return yourTurn;
    }

    @Override
    public Colour getCurrentColour(int gameID) throws RemoteException {
        Game g = getGameByID(gameID);
        return g.getCurrent();
    }

    @Override
    public int getLastMove(int gameID) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<User, Integer> getScore(int gameID)throws RemoteException {
        Game g = getGameByID(gameID);
        return g.getScore();
    }
    
    @Override
	public boolean playCardAllowed(int gameID, Card c) throws RemoteException {
		// TODO Auto-generated method stub
		
		Game g=getGameByID(gameID);
		
		return g.playCardAllowed(c);
	}

    @Override
    public synchronized boolean getFinished(int gameID) throws RemoteException {
        Game g = getGameByID(gameID);
        
        return g.getFinished();
    }

    @Override
    public synchronized boolean waitForNewCardPlayed(int gameID) throws RemoteException {
        Game g = getGameByID(gameID);
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(g.getFinished()){
            return false;
        }
        return true;
    }
    
    @Override
    public synchronized void endGame(int gameID, int userID) throws RemoteException {
        Game g = getGameByID(gameID);
        User u = getUserByID(userID);
        g.removePlayer(u);
        notifyAll();
    }

	@Override
	public boolean hasWaitingGame() throws RemoteException{
		// TODO Auto-generated method stub
		boolean waiting=false;
		for(Game g:games) {
			
			if(g.getAmountOfPlayers()<Game.MAX_USERS && !g.getFinished()&&!g.getStarted()) {
			
			waiting=true;	
				
			}
			
			
		}
		
		return waiting;
	}
    
    
   
   

    
}
