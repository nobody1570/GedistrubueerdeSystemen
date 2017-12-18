package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CommunicationControllers.InterfaceDBController;
import CommunicationControllers.InterfaceSController;
import communication.CommunicationImpl;
import model.Database;

public class MainDatabase {
	private static Registry controlRegistry;
    private static final String localhost = Constants.Constants.localhost;
    private static InterfaceDBController idb;
	

	 void startDatabase() {
	        try {
	        	
	        	controlRegistry = LocateRegistry.getRegistry(localhost, 3001);
	        	
	        	idb = (InterfaceDBController) controlRegistry.lookup("S_D_Com_Controller");
	        	
	        	int port=idb.getNextDatabasePort();
	        	
	                // create on port
	                Registry registry = LocateRegistry.createRegistry(port);
	                // create a new service named CounterService
	                
	                Database db=new Database(idb,port);
	                registry.rebind("DatabaseService", db);
	                
	                idb.addDatabase(port);
	                
	                db.init();
	                
	                
	        } catch (Exception e) {
	        e.printStackTrace();
	        }


	        System.out.println("database is ready");
	    }
	    public static void main(String[] args) {

	        MainDatabase mainDB = new MainDatabase();
	        mainDB.startDatabase();
	    }
	
	

}
