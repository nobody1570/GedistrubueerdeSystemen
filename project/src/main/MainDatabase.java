package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communication.CommunicationImpl;
import model.Database;

public class MainDatabase {
	
	

	 private void startServer() {
	        try {
	                // create on port 1500
	                Registry registry = LocateRegistry.createRegistry(1500);
	                // create a new service named CounterService
	                registry.rebind("DatabaseService", new Database());
	        } catch (Exception e) {
	        e.printStackTrace();
	        }


	        System.out.println("database is ready");
	    }
	    public static void main(String[] args) {

	        MainDatabase mainDB = new MainDatabase();
	        mainDB.startServer();
	    }
	
	

}
