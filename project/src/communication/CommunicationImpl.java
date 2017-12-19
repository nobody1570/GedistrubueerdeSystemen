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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import CommunicationControllers.InterfaceDBController;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;


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
	String chars;
	char[] charArray;

    public CommunicationImpl (DatabaseCommunication dBImpl) throws RemoteException{
        this.db = dBImpl;
        games = new ArrayList<Game>();
        userList = new HashSet<User>();
        random = new SecureRandom();
        chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        charArray=chars.toCharArray();
    }

    @Override
    public boolean createNewAccount(String username, String password) throws RemoteException{
    	
    	
        System.out.println("create user rmi");
        
       
        if (db.readUser(username) == null){
            System.out.println("user bestaat niet");
            
            //create salt
            
            //System.out.println(chars);
            
            StringBuilder sb=new StringBuilder("");
            
            int max=chars.length();
            int length=21;
            if(password.length()%4==0)length++;
            for(int i=0;i<length;i++) {
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
			//System.out.println("digest succeeded");
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
    public String login(String name, String pw) throws RemoteException {
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
            		userList.remove(u);
                    userList.add(u);
                    
                    //token returnen
                    
                    String token;
                    User duplicateToken=null;
                    //generateToken
                    
                    StringBuilder sb=new StringBuilder("");
                    int size=charArray.length;
                    for(int i=0;i<30;i++) {
                    	sb.append(charArray[random.nextInt(size)]);
                    	
                    	
                    	}
                    
                    
                    
                    token=sb.toString()+System.currentTimeMillis();
                    
                    u.setToken(token);
                    u.setTimestamp(System.currentTimeMillis());
                    
                    //update in database
                    
                    db.updateUser(u);
                    
                    return token;
                    }
            
            } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            return null;
    }
    
    
    @Override
    //returns true if token is still valid and logs user in--> else false
	public boolean login(String token) throws RemoteException {
		// TODO Auto-generated method stub
    	
    	User u=getUserByToken(token);
    	userList.remove(u);
    	u=getUserByToken(token);
    	
    	if(u==null) return false;
    	if(u.getTimestamp()<(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(1)))return false;
    	
    	userList.add(u);
    	
		return true;
	}
    

    @Override
    public int getPublicGame(String token) throws RemoteException, InterruptedException{
        //kijken voor plaats in de games
        User u = getUserByToken(token);
        
        int gameFound = -1;
        
        
        //checken of user meer dan 24 uur online was --> in dat geval mag hij geen nieuwe game starten
        
        if(u.getTimestamp()>(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(1))){
        
        
        Game game;
        for (int i=0; i<games.size();i++){
            game = games.get(i);
            if (game.getAmountOfPlayers()<game.getMaxUsers() && !game.getStarted() && !game.getFinished() && !game.isPrivateGame()){
                //plaats gevonden add player

                game.addPlayer(u);
                gameFound = i;
                if(game.getAmountOfPlayers()==game.getMaxUsers()){
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
        }
        return gameFound;
       
    }

    @Override
    public synchronized boolean startGame(int gameID, boolean priv) throws RemoteException{
        Game g = getGameByID(gameID, priv);
        boolean start = g.getStarted();
        //niet begonnen en met meer dan 1 => start
        if(!start && g.getAmountOfPlayers()>1){
            start = true;
            g.start();
            
        }
        return start;
    }
    @Override
    public boolean logout(String token) throws RemoteException {
        //disable token
        User u = getUserByToken(token);
        
        if (userList.contains(u)){
                userList.remove(u);
                u.setTimestamp(0L);
                db.updateUser(u);
                return true;
        }
        return false;
    }

    @Override
    public List<User> getSpelersList(int gameID, boolean priv) throws RemoteException {
        System.out.println("zoeken naar spelerslijst voor game");
        //get spelers in current game
        Game g = getGameByID(gameID, priv);
        List<User>players=g.getPlayers();
        
        for (User u:players) {
        	
        	u.setPassword("");
        	u.setSalt_password("");
        	u.setSalt_token("");
        	u.setToken("");
        	u.setTimestamp(0L);
        }
        return players;
    }
    
    @Override
    public List<Integer> getSpelersHandSize(int gameID, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
        List<Integer> handSizes = new ArrayList<>();
        for (User u: g.getPlayers()){
            handSizes.add(g.getHand(u).size());
        }
        return handSizes;
    }
    
    @Override
    public List<Card> getHand(int gameID, String token, boolean priv) throws RemoteException{
        User u = getUserByToken(token);
        //lookup in DB op gameiD
        Game g = getGameByID(gameID, priv);
        List<Card> hand = g.getHand(u);
        return hand;
    }

    @Override
    public synchronized List<Card> drawCard(int gameID, String token, boolean priv) throws RemoteException {
        //get user by id
        User u = getUserByToken(token);
        //get game by id
        Game g = getGameByID(gameID, priv);
        
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
    public synchronized Card getLatestPlayedCard(int userID, int gameID, int latestReceivedMove, boolean priv) throws RemoteException {
        System.out.println("server: getlatestCard");
        //get game, look in game for latest card
        Game g = getGameByID(gameID, priv);
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
    public Card getLatestPlayedCard(int gameID, boolean priv) throws RemoteException {
        System.out.println("server: getlatestCard");
        //get game, look in game for latest card
        Game g = getGameByID(gameID, priv);
 
        return g.getLastCard();
        
    }
        
    @Override
    public synchronized boolean playCard(String token, int gameID, Card card, boolean priv) throws RemoteException{
        System.out.println("server: playcard");
        //zoek game met id in db en return
        //controleer als card is playable adhv last played card
        //set last palyed card
        //notify all (getLastPlayedCard)
        boolean succes;
        Game g = getGameByID(gameID, priv);
        User u = getUserByToken(token);
        succes = g.performTurn(u, card);
        notifyAll();
        return succes;
    }
    @Override
    public void setPrefered(int gameID, String token, Colour c, boolean priv)throws RemoteException{
        Game g = getGameByID(gameID, priv);
        User u = getUserByToken(token);
        g.SetPreferredColour(u, c);
    }
    
    @Override
    public boolean getStarted(int gameID, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
        return g.getStarted();
    }
    
    public Game getGameByID(int gameID, boolean priv)throws RemoteException{
        for (Game g : games){
            if(g.getId()==gameID && g.isPrivateGame() == priv){
                return g;
            }
        }
        //indien private en niet in cache => zoeken in DB
        if(priv){
            Game g = db.readGame(gameID);
            games.add(g);
            System.out.println("game is priv opzoeken in db voor in cache: "+g);
            return g;
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
    
    public User getUserByToken(String token){
        for (User u: userList){
            if(u.getToken().equals(token)){
                return u;
            }
            
        }
        User u;
		try {
			u = db.readTokenUser(token);
		
        return u;
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
        	
			e.printStackTrace();
			return null;
		}
		
    }

    @Override
    public synchronized boolean myTurn(int gameID, String token, boolean priv) throws RemoteException{
        System.out.println("myturn serverside");
        User u = getUserByToken(token);
        Game g = getGameByID(gameID, priv);
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
                if(!g.getChangedScores()) {		
	                	g.setChangedScores(true);		
	                	Map<User,Integer> scoreMap=g.getScore();		
	                	List <User> users=new ArrayList(scoreMap.keySet());		
	                			
	                	for(User user:users) {		
	                				
	                		db.addScore(user, scoreMap.get(user));		
	                	}		
	                }
                yourTurn = false;
            }
        return yourTurn;
    }

    @Override
    public Colour getCurrentColour(int gameID, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
        return g.getCurrent();
    }

    @Override
    public int getLastMove(int gameID, boolean priv) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<User, Integer> getScore(int gameID, boolean priv)throws RemoteException {
        Game g = getGameByID(gameID, priv);
        return g.getScore();
    }
    
    @Override
	public boolean playCardAllowed(int gameID, Card c, boolean priv) throws RemoteException {
		// TODO Auto-generated method stub
		
		Game g=getGameByID(gameID, priv);
		
		return g.playCardAllowed(c);
	}

    @Override
    public synchronized boolean getFinished(int gameID, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
        
        return g.getFinished();
    }

    @Override
    public synchronized boolean waitForNewCardPlayed(int gameID, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
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
    public synchronized void endGame(int gameID, String token, boolean priv) throws RemoteException {
        Game g = getGameByID(gameID, priv);
        User u = getUserByToken(token);
        g.removePlayer(u);
        if(priv){
            db.deleteGame(gameID);
        }
        
        notifyAll();
    }

	@Override
	public boolean hasWaitingGame() throws RemoteException{
		// TODO Auto-generated method stub
		boolean waiting=false;
		for(Game g:games) {
			
			if(g.getAmountOfPlayers()<g.getMaxUsers() && !g.getFinished()&&!g.getStarted()) {
			
			waiting=true;	
				
			}
			
			
		}
		
		return waiting;
	}

    @Override
    public String getCardback(String theme) throws RemoteException {
        System.out.println("in getcardback");
        String path = db.readCardback(theme);
        
        System.out.println("test kom ik hier wel?");

        /*ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", "");
        BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(img, 0, 0, null);
     bg.dispose();;*/
        return path;
    }
    /*
    **return list of all games where user participates in
    */
    @Override
    public List<Game> getAllParticipatingGames(String token) throws RemoteException {
        //naar db gaan kijken voor alle games waarin pers zit
        List<Game> participatingGames = db.getParticipatingGames(token);
        return participatingGames;
    }

    @Override
    public List<Game> getAllPrivateGames(String token) throws RemoteException {
        //hier bijhouden welke games gemaakt of in db kijken naar alle games? of 20niet volle games?
        List<Game> participatingGames = db.getPrivateGames();
        
        return participatingGames;
    }

    @Override
    public int getCreatePrivateGame(String token, int maxUsers)throws RemoteException{
        //naar db gaan om priv game aan te maken en game terug sturen, dan game adden in games en gameID terugsturen
        int highestGameID = db.getHighestGameID()+1;
        Game game = new Game(highestGameID, maxUsers);
        game.addPlayer(getUserByToken(token));
        games.add(game);
        db.saveGame(game);
        System.out.println("game id: "+game.getId());
        return game.getId();
    }
    
    @Override
    public boolean joinPrivateGame(int gameID, String token)throws RemoteException{
        //check if game is in cache, otherwise put it in cache
        User u = getUserByToken(token);
        Game g = getGameByID(gameID, true);
        //try joining
        boolean joined = false;
        //er reeds in 
        if(g.getPlayers().contains(u)){
            System.out.println("player in game in cache");
            joined = true;
        }else{
            System.out.println("player niet in game kijk of erin kan");
            if(g.getAmountOfPlayers()<g.getMaxUsers() && !g.getFinished()){
                System.out.println("probeer erin te steken"+ g.getAmountOfPlayers()+"/"+g.getMaxUsers());
                joined = db.joinGame(gameID,token);
                
                if(joined ){
                    //aanpassen versie in cache
                    games.remove(g);
                    g = db.readGame(gameID);
                    games.add(g);
                    if(g.getAmountOfPlayers()==g.getMaxUsers()){
                         g.start();
                         
                    }
                   
                }
            }
        }
        return joined;
    }
   
   

    
}