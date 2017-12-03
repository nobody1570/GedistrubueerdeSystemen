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


public class MainServer {
	
	private static DatabaseCommunication DBImpl;
	private static Registry myRegistry;
    private static final String localhost = "192.168.0.150";
    //private static final String localhost = "192.168.56.1";
	
    private void startServer() {
        try {
        	
        	
        		//connect to database on port 1500
        	myRegistry = LocateRegistry.getRegistry(localhost, 1500);

            // search for DatabaseCommunication
            DBImpl = (DatabaseCommunication) myRegistry.lookup("DatabaseService");
        		
        		
        	
        	
        	
        	
                // create on port 1099
                Registry registry = LocateRegistry.createRegistry(1099);
                // create a new service named CounterService
                registry.rebind("CommunicationService", new CommunicationImpl(DBImpl));
        } catch (Exception e) {
        e.printStackTrace();
        }


        System.out.println("system is ready");
    }
    public static void main(String[] args) {

        MainServer main = new MainServer();
        main.startServer();
    }
}
