/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import communication.Communication;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Card;
import model.Game;

/**
 * FXML Controller class
 *
 * @author Jonas
 */
public class GameController implements Initializable {
    private static Communication impl;
    private static Registry myRegistry;
    private static String userName;

    //game info
    private static int gameID;
    @FXML
    private Group hand = new Group() ;
    
    @FXML
    private Stage stage;
    @FXML 
    private Label username;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
    @FXML
    public void returnToLobby(ActionEvent event) throws IOException{     
        Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);

        window.show();
    }

    public void redirectGame(int gameID, Communication impl, Registry myRegistry) {
        this.gameID = gameID;
        this.impl = impl;
        this.myRegistry = myRegistry;      
    }
    
    public void loadGame(int gameID) throws RemoteException{
        impl.getSpelersList(gameID);
        
        //get hand via comm
        
        hand = (Group) impl.getHand(gameID,userName);
/*
        hand.getChildren().clear() ;

         for ( int card_index  =  0 ;
                   card_index  <  5 ;
                   card_index  ++ )
         {
            Card new_card = card_deck.get_card() ;

            double card_position_x = 40 + ( Card.CARD_WIDTH + 20 ) * card_index ;
            double card_position_y = 50 ;

            new_card.set_card_position( card_position_x, card_position_y ) ;

            hand.getChildren().add( new_card ) ;
         }*/
         
         //get last played card
         
         //
        
    }
}
