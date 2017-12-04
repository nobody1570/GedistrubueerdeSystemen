/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import communication.Communication;
/**
 *
 * @author Jonas
 */
import communication.CommunicationImpl;
import communication.DatabaseCommunication;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CommunicationControllers.InterfaceSController;
import CommunicationControllers.PortServerImpl;


public class MainServer {
	
	private static DatabaseCommunication DBImpl;
	private static InterfaceSController isc;
	private static Registry DBRegistry;
	private static Registry controlRegistry;
    private static final String localhost = "192.168.0.150";
    //private static final String localhost = "192.168.56.1";
	
    private void startServer() {
        try {
        	//connect to C_S_com_controller and get port number to use
        	
        	controlRegistry = LocateRegistry.getRegistry(localhost, 3000);
        	
        	isc = (InterfaceSController) controlRegistry.lookup("C_S_Com_Controller");
        	int port=isc.getNextServerPort();
        	
        	
        	
        		//connect to database on port 1500
        	DBRegistry = LocateRegistry.getRegistry(localhost, 1500);

            // search for DatabaseCommunication
            DBImpl = (DatabaseCommunication) DBRegistry.lookup("DatabaseService");
        		
        		
        	
        	
        	
                // create on port 1099
                Registry registry = LocateRegistry.createRegistry(port);
                // create a new service named CounterService
                registry.rebind("CommunicationService", new CommunicationImpl(DBImpl));
                
                
         //tell controller that server is online
                
                isc.addServer(port);
         System.out.println("system is ready\n port:"+port);
        } catch (Exception e) {
        e.printStackTrace();
        }


        
    }
    public static void main(String[] args) {

        MainServer main = new MainServer();
        main.startServer();
    }
}
