package CommunicationControllers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceSController extends Remote{
	
	
	public void addServer(int port)throws RemoteException;
	
	//used in client to connect to one of the servers
	public int getServerPort() throws RemoteException;
	
	//used in server to determine port to use
	public int getNextServerPort()throws RemoteException;
	

}
