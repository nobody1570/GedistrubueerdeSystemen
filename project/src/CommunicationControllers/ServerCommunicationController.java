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
	public void addServer(PortServerImpl psi) throws RemoteException {
		// TODO Auto-generated method stub
		
		servers.add(psi);
		
		System.out.println("added new server on port: "+psi.getPort());
		
		
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
		
		List <PortServerImpl> toChooseFrom=new ArrayList<PortServerImpl>();
		int max=0;
		int test;
		
		//get servers with highest amount of waiting players
		for(PortServerImpl psi: servers) {
			
			test=psi.getImpl().getMostPlayersWaitingForGameToStart();
			if (test==max)toChooseFrom.add(psi);
			if (test>max) {
				max=test;
				toChooseFrom.clear();
				toChooseFrom.add(psi);
				
			}
			
			
		}
		
		PortServerImpl chosen=null;
		Boolean found=false;
		
		//choose one of those servers to add a player
		while(!found) {
			
			if(toChooseFrom.contains(servers.get(currentImpl))) {
				
				chosen=servers.get(currentImpl);
				found=true;
			}
			
			//if it's not in the list --> check next (this should ensure an even spreading of the games over the servers)
			nextCurrentImpl();
			
		}
		
		
	
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
