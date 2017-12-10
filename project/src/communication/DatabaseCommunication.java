package communication;

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

	public User readUser(int i) throws RemoteException;

	public void updateUser(User u) throws RemoteException;

	public int getHighestID() throws RemoteException;

	public void deleteUser(User u) throws RemoteException;

	public void deleteUser(int i) throws RemoteException;

	public void saveGame(Game g) throws RemoteException;

	public Game readGame(int gameID) throws RemoteException;

	public void deleteGame() throws RemoteException;

	public void nonPropagateCreateUser(User u)throws RemoteException;

	public void nonPropagateUpdateUser(User u)throws RemoteException;

	public void nonPropagateDeleteUser(int i)throws RemoteException;

}
