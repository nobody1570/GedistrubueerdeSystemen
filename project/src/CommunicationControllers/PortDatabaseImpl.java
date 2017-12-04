package CommunicationControllers;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communication.DatabaseCommunication;

public class PortDatabaseImpl {
	
	static final String localhost=Constants.Constants.localhost;
	
	int port;
	DatabaseCommunication dc;
	
	int reading;
	boolean writeAsked;
	boolean writing;

	public PortDatabaseImpl(int port) {
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
		
		writeAsked=false;
		writing=false;
	}

	/**
	 * @return the reading
	 */
	public int getReading() {
		return reading;
	}

	/**
	 * @return the writeAsked
	 */
	public boolean isWriteAsked() {
		return writeAsked;
	}

	/**
	 * @return the writing
	 */
	public boolean isWriting() {
		return writing;
	}
	
	public synchronized void addReader() {
		
		while(writeAsked||writing) {
			
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		reading++;
		
		notifyAll();
		
	}
	
	public synchronized void removeReader() {
		
		reading--;
		
		notifyAll();
		
	}
	
public synchronized void addWriter() {
		
		while(writeAsked) {
			
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		writeAsked=true;
		
		while(reading>0||writing) {
			
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeAsked=false;
		writing=true;
		
		notifyAll();
		
	}


	public synchronized void removeWriter() {
	
	writing=false;
	
	notifyAll();
	
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	
}
