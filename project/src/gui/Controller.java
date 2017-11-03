/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import communication.Communication;
import java.io.IOException;
import javafx.fxml.Initializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Game;
/**
 *
 * @author Jonas
 */
public class Controller implements Initializable{
    private static Communication impl;
    private static Registry myRegistry;
    private static String username;
    
    private GameController gameController;
    @FXML
    private Stage stage;
    @FXML 
    private Label userName;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    @FXML 
    void joinPublic(ActionEvent event) throws IOException, RemoteException, InterruptedException{
        //kijken voor bestaande game et plaatsen anders nieuw spel starten
        //searching screen
                
                    
        //searchGame();
        int gameID = impl.getPublicGame(username);
                
        gameController = new GameController();
        gameController.redirectGame(gameID,impl,myRegistry);
        
        Parent root = FXMLLoader.load(getClass().getResource("/gui/gameView.fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        gameController.loadGame(gameID);
        window.show();
    }
    
    @FXML 
    void joinPrivate(ActionEvent event){
        
    }

    void redirectLobby(String username, Communication impl, Registry myRegistry) {
        this.username = username;
        this.impl = impl;
        this.myRegistry = myRegistry;
    }
    
    
}
