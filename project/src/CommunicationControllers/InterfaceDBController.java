package CommunicationControllers;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.User;

public interface InterfaceDBController extends Remote {

	public void addDatabase(int port) throws RemoteException;

	public void getWriteAccess(int port) throws RemoteException;

	public void releaseWriteAccess(int port) throws RemoteException;

	public void getReadAccess(int port) throws RemoteException;

	public void releaseReadAccess(int port) throws RemoteException;

	// Databases use this to determine their port
	public int getNextDatabasePort() throws RemoteException;

	// get portnumber of database that the new oblect should be written to
	public int getDatabaseForNextNewUser() throws RemoteException;

	public int getDatabaseWhereUserIsSaved(int id) throws RemoteException;

	public int getDatabaseWhereUserIsSaved(String login) throws RemoteException;

	public void logUserInController(User u,int port) throws RemoteException;
	
	public int getNewUserID()throws RemoteException;
}
