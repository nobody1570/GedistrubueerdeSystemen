package CommunicationControllers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceDBController extends Remote{
	
	public void addDatabase(int port)throws RemoteException;
	
	public void getWriteAccess(int port)throws RemoteException;
	
	public void releaseWriteAccess(int port)throws RemoteException;
	
	public void getReadAccess(int port)throws RemoteException;
	
	public void releaseReadAccess(int port)throws RemoteException;
	
	//Databases use this to determine their port
	public int getNextDatabasePort()throws RemoteException;

}
