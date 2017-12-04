package CommunicationControllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class DatabaseCommunicationController extends UnicastRemoteObject implements InterfaceDBController {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	Map <Integer,PortDatabaseImpl>databases;
	
	int currentToWriteTo;
	
	int nextPort;
	

	public DatabaseCommunicationController() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
		currentToWriteTo=0;
		int nextPort=1500;
		
	}

	@Override
	public void addDatabase(int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		databases.put(port,new PortDatabaseImpl(port));
		
		System.out.println("added new database on port: "+port);
		
		
	}

	@Override
	public void getWriteAccess(int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		databases.get(port).addWriter();
		
	}
	
	

	@Override
	public void getReadAccess(int port) throws RemoteException {
		// TODO Auto-generated method stub
		databases.get(port).addReader();
	}

	

	@Override
	public void releaseWriteAccess(int port) throws RemoteException {
		// TODO Auto-generated method stub
		databases.get(port).removeWriter();
		
	}

	@Override
	public void releaseReadAccess(int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		databases.get(port).removeReader();
		
	}
	
	@Override
	public synchronized int getNextDatabasePort() throws RemoteException {
		// TODO Auto-generated method stub
		return nextPort++;
	}
}
