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
	
	
	int currentDatabaseToConnectTo;
	
	int nextPort;
	
	//voor method getNewUserID()
	//Als false--> zoekt alle databases af naar hoogste id
	Boolean checkedDBs;
	int nextUserID;

	public DatabaseCommunicationController() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		
		
		databases=new HashMap<>();
		databasesList=new ArrayList<>();
		
		UserID_port=new HashMap<>();
		UserLogin_port=new HashMap<>();
		
		currentDatabaseToConnectTo=0;
		nextPort=1500;
		checkedDBs=false;
		nextUserID=-1;
		
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
	public int getNextDatabase() throws RemoteException {
		// TODO Auto-generated method stub
		
		currentDatabaseToConnectTo++;
		
		currentDatabaseToConnectTo=currentDatabaseToConnectTo%databases.size();
		
		int next=databasesList.get(currentDatabaseToConnectTo).getPort();
		
		return next;
	}



	@Override
	public int getNewUserID() throws RemoteException {
		// TODO Auto-generated method stub
		
		if(!checkedDBs) {int max=0;
		int test;
		for(PortDatabaseImpl pdi:databasesList) {
			
			pdi.addReader();
			
			//hier lezen
			test= pdi.getDc().getHighestID();
			
			pdi.removeReader();
			
			if(test>max) {
				
				
				max=test;
			}
			
		}
		
		nextUserID=max;
		}
		
		return ++nextUserID;
	}
	
	
	

	@Override
	public List<Integer> getAllDatabasesPorts() throws RemoteException {
		// TODO Auto-generated method stub
		
		List <Integer> portList=new ArrayList<>();
		
		for(PortDatabaseImpl pdi:databasesList) {
			
			portList.add(pdi.getPort());
		}
		
		return portList;
	}
}
