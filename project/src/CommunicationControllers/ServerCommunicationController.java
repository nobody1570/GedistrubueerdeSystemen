package CommunicationControllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerCommunicationController extends UnicastRemoteObject implements InterfaceSController{
	private static final long serialVersionUID = 1L;
	List <PortServerImpl> servers;
	int currentImpl;
	
	int nextPort;
	
	
	public ServerCommunicationController() throws RemoteException {
		super();

		servers= new ArrayList<PortServerImpl>();
		nextPort=1099;
		currentImpl=0;
		System.out.println("serverComContr started");
	}

	
	

	@Override
	public void addServer(int port) throws RemoteException {
		// TODO Auto-generated method stub
		
		
		
		servers.add(new PortServerImpl(port));
		
		System.out.println("added new server on port: "+port);
		
		
	}

	
	
	//used in client to connect to one of the servers
	@Override
	public int getServerPort() throws RemoteException {
		
		//method to check for non-started games in servers--> picks game with highest amount of players
		
		PortServerImpl psi=getServerToConnectTo();
		
		System.out.println("directed client to server on port: "+psi.getPort());
		return psi.getPort();
	}



	private PortServerImpl getServerToConnectTo() throws RemoteException{
		// finds server with the game with the most waiting people --> if those are the same we use currentImpl to choose our server
		
		PortServerImpl chosen=null;
		
		
		//get servers with highest amount of waiting players
		
		
		if(!servers.get(currentImpl).getImpl().hasWaitingGame()) {
			
			nextCurrentImpl();
				
		}
			
		
		chosen=servers.get(currentImpl);
		
	
		
		return chosen;
	}




	private void nextCurrentImpl() {
		
		currentImpl++;
		currentImpl=currentImpl%servers.size();
		
	}




	//used in server to determine port to use
	@Override
	public synchronized int getNextServerPort() throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println("gave server port: "+nextPort);
		return nextPort++;
	}
	
	

}
