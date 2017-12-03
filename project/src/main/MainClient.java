/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import communication.Communication;
import gui.Controller;
import gui.LoginController;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CommunicationControllers.InterfaceSController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Image;

/**
 *
 * @author Jonas
 */
public class MainClient extends Application {
    
    private Stage primaryStage;
    private static Communication impl;
    private static Registry myRegistry;
    private static Registry controlRegistry;
    private static final String localhost = "192.168.0.150";
    //private static final String localhost = "192.168.56.1";
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;  
        initRootLayout();
    }

    public void initRootLayout() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/gui/login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        //Parent root = FXMLLoader.load(getClass().getResource("/gui/login.fxml"));
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uno");
        primaryStage.setX(200);
        primaryStage.setY(50);
        primaryStage.setHeight(600.0);
        primaryStage.setWidth(800.0);
        controller.setStage(primaryStage);
        controller.setCommunication(impl, myRegistry);
        primaryStage.show();

        

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //verbinding met server opstellen
        // fire to localhost port 1099
        
        try {
        	
        	controlRegistry = LocateRegistry.getRegistry(localhost, 3000);
        	
        	InterfaceSController isc = (InterfaceSController) controlRegistry.lookup("C_S_Com_Controller");
        	
        	int port=isc.getServerPort();
        	
        	
        	
        	
            myRegistry = LocateRegistry.getRegistry(localhost, port);


            // search for CounterService
            impl = (Communication) myRegistry.lookup("CommunicationService");


            launch(args);
            System.out.println("logged out");
        }catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    
}
