package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CommunicationControllers.ServerCommunicationController;
import model.Database;

public class Main_C_S_Com_Controller {
	
	
	 void startCSComContr() {
	        try {
	        	
	        	
	                // create on port 3000!
	                Registry registry = LocateRegistry.createRegistry(3000);
	                // create a new service named CounterService
	                registry.rebind("C_S_Com_Controller", new ServerCommunicationController());
	        } catch (Exception e) {
	        e.printStackTrace();
	        }


	        System.out.println("C_S_Com_Controller is ready");
	    }
	    public static void main(String[] args) {

	    	Main_C_S_Com_Controller mainCSComController = new Main_C_S_Com_Controller();
	    	mainCSComController.startCSComContr();
	    }
	
	

}
