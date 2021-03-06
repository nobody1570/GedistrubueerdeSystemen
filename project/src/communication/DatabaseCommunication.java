package communication;

import java.awt.image.BufferedImage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import model.Game;
import model.User;

public interface DatabaseCommunication extends Remote {

	public void closeConnection() throws RemoteException;

	public void createUser(User u) throws RemoteException;

	public User readUser(String login) throws RemoteException;

	//met userid
	public User readUser(int i) throws RemoteException;
	
	//met token
	public User readTokenUser(String token)throws RemoteException;

	public void updateUser(User u) throws RemoteException;

	public int getHighestID() throws RemoteException;
        
        public int getHighestGameID() throws RemoteException;

	public void deleteUser(User u) throws RemoteException;

	public void deleteUser(int i) throws RemoteException;

	public void saveGame(Game g) throws RemoteException;
        
	public Game readGame(int gameID) throws RemoteException;

	public void deleteGame(int gameID) throws RemoteException;
        
        public String readCardback(String theme) throws RemoteException;

	public void nonPropagateCreateUser(User u)throws RemoteException;

	public void nonPropagateUpdateUser(User u)throws RemoteException;

	public void nonPropagateDeleteUser(int i)throws RemoteException;

	public int getNextID()throws RemoteException;
        
        public void addScore(User u, int gameScore)throws RemoteException;
	
	public void nonPropagateaddScore(User u, int gameScore) throws RemoteException;

        public List<Game> getParticipatingGames(String token) throws RemoteException;
    
        public List<Game> getPrivateGames() throws RemoteException;

        public boolean joinGame(int gameID, String token)throws RemoteException;


}
