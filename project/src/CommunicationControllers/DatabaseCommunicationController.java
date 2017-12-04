package CommunicationControllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.User;

public class DatabaseCommunicationController extends UnicastRemoteObject implements InterfaceDBController {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	Map <Integer,PortDatabaseImpl>databases;
	List <PortDatabaseImpl>databasesList;
	
	Map<Integer,Integer> UserID_port;
	Map<String,Integer> UserLogin_port;
	
	
	int currentToWriteTo;
	
	int nextPort;
	

	public DatabaseCommunicationController() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
		
		databases=new HashMap<>();
		databasesList=new ArrayList<>();
		
		UserID_port=new HashMap<>();
		UserLogin_port=new HashMap<>();
		
		currentToWriteTo=0;
		nextPort=1500;
		
	}

	@Override
	public void addDatabase(int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		PortDatabaseImpl database=new PortDatabaseImpl(port);
		
		databases.put(port,database);
		databasesList.add(database);
		
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

	@Override
	public int getDatabaseForNextNewUser() throws RemoteException {
		// TODO Auto-generated method stub
		
		int port=getNextDatabase();
		return port;
	}

	private int getNextDatabase() {
		// TODO Auto-generated method stub
		
		currentToWriteTo++;
		
		currentToWriteTo=currentToWriteTo%databases.size();
		
		int next=databasesList.get(currentToWriteTo).getPort();
		
		return next;
	}

	@Override
	public int getDatabaseWhereUserIsSaved(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDatabaseWhereUserIsSaved(String login) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void logUserInController(User u,int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		UserID_port.put(u.getId(), port);
		UserLogin_port.put(u.getLogin(), port);
		
	}

	@Override
	public int getNewUserID() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
}
