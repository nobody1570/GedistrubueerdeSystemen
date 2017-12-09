package CommunicationControllers;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communication.DatabaseCommunication;

public class SimplePortDatabaseImpl {
	
	static final String localhost=Constants.Constants.localhost;
	
	int port;
	DatabaseCommunication dc;
	

	public SimplePortDatabaseImpl(int port) {
		// TODO Auto-generated constructor stub
		
		this.port=port;
		
		Registry DBRegistry;
		try {
			DBRegistry = LocateRegistry.getRegistry(localhost, port);
		

        // search for DatabaseCommunication
        dc = (DatabaseCommunication) DBRegistry.lookup("DatabaseService");
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the dc
	 */
	public DatabaseCommunication getDc() {
		return dc;
	}
	
	
}
