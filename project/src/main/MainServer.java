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

import CommunicationControllers.InterfaceDBController;
import CommunicationControllers.InterfaceSController;
import CommunicationControllers.PortServerImpl;


public class MainServer {
	
	private static DatabaseCommunication DBImpl;
	private static InterfaceSController isc;
	
	private static InterfaceDBController idb;
	private static Registry DBRegistry;
	private static Registry controlRegistry;
	private static Registry databaseControlRegistry;
    private static final String localhost = Constants.Constants.localhost;
    //private static final String localhost = "192.168.56.1";
	
    void startServer() {
        try {
        	//connect to C_S_com_controller and get port number to use
        	
        	controlRegistry = LocateRegistry.getRegistry(localhost, 3000);
        	
        	isc = (InterfaceSController) controlRegistry.lookup("C_S_Com_Controller");
        	int port=isc.getNextServerPort();
        	
        	
        	//connect to dbcontroller on port 3001
        	
        	databaseControlRegistry = LocateRegistry.getRegistry(localhost,3001);
        	
        	idb=(InterfaceDBController)databaseControlRegistry.lookup("S_D_Com_Controller");
        	
        	//connect to database on port 1500
        	int databaseport=idb.getNextDatabase();
        	
        	DBRegistry = LocateRegistry.getRegistry(localhost, databaseport);
        	
        	

            // search for DatabaseCommunication
            DBImpl = (DatabaseCommunication) DBRegistry.lookup("DatabaseService");
        	
                // create on port 1099
                Registry registry = LocateRegistry.createRegistry(port);
                // create a new service named CounterService
                registry.rebind("CommunicationService", new CommunicationImpl(DBImpl));
                
                
         //tell controller that server is online
                
                isc.addServer(port);
         System.out.println("system is ready\n port:"+port+"\n Uses database: "+databaseport);
        } catch (Exception e) {
        e.printStackTrace();
        }


        
    }
    public static void main(String[] args) {

        MainServer main = new MainServer();
        main.startServer();
    }
}
