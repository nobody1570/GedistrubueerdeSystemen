package CommunicationControllers;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communication.Communication;

public class PortServerImpl{
	
	//implementation with port--> So we know the port the implementation is running on
	
	

	private static final String localhost = Constants.Constants.localhost;;
    
	
	int port;
	Communication impl;
	public PortServerImpl(int port) {
		super();
		this.port = port;
		
		//get implemention on port
		
		Registry reg;
		try {
			reg = LocateRegistry.getRegistry(localhost, port);
		


        // search for CounterService
        impl = (Communication)reg .lookup("CommunicationService");
        
        
        
		
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
	 * @return the impl
	 */
	public Communication getImpl() {
		return impl;
	}
	
	

}
