package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CommunicationControllers.DatabaseCommunicationController;
import CommunicationControllers.ServerCommunicationController;
import model.Database;

public class Main_S_D_Com_Controller {
	
	
	 void startCSComContr() {
	        try {
	        	
	        	
	                // create on port 3000!
	                Registry registry = LocateRegistry.createRegistry(3001);
	                // create a new service named CounterService
	                registry.rebind("S_D_Com_Controller", new DatabaseCommunicationController());
	        } catch (Exception e) {
	        e.printStackTrace();
	        }


	        System.out.println("S_D_Com_Controller is ready");
	    }
	    public static void main(String[] args) {

	    	Main_S_D_Com_Controller mainCSComController = new Main_S_D_Com_Controller();
	    	mainCSComController.startCSComContr();
	    }
	
	

}
