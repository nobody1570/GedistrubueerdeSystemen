package CommunicationControllers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.User;

public interface InterfaceDBController extends Remote {

	public void addDatabase(int port) throws RemoteException;

	public void getWriteAccess(int port) throws RemoteException;

	public void releaseWriteAccess(int port) throws RemoteException;

	public void getReadAccess(int port) throws RemoteException;

	public void releaseReadAccess(int port) throws RemoteException;

	// Databases use this to determine their port
	public int getNextDatabasePort() throws RemoteException;

	public int getNextDatabase() throws RemoteException;
	
	public int getNewUserID()throws RemoteException;
	
	//voor in server --> alle poortnummers van databases opvragen
	public List<Integer> getAllDatabasesPorts()throws RemoteException;
}
