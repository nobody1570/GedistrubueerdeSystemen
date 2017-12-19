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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Card;
import model.Game;
import model.User;

/**
 *
 * @author Jonas
 */
public class PrivateLobbyController implements Initializable{
    private ObservableList<Game> lobby = FXCollections.observableArrayList();
    @FXML public ListView<Game> lobbyView;
    private ObservableList<Game> ownGames = FXCollections.observableArrayList();
    @FXML public ListView<Game> ownGamesView;
    @FXML Button createPrivateGame;
    @FXML Button backToHome;
    @FXML Button reFresh;
    @FXML Button joinGame;
    @FXML 
    private Label usernameLabel;
    private int gameID;
    private boolean privateGame;
    
    private static String token;
    private static Communication impl;
    private static Registry myRegistry;
    private static String username;
    private GameController gameController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        usernameLabel.setText("Welcome "+username);
        privateGame = true;
        lobbyView.setItems(lobby);
        lobbyView.setCellFactory((ListView<Game> p) -> {
            ListCell<Game> cell = new ListCell<Game>(){
                
                @Override
                protected void updateItem(Game game, boolean bln) {
                    super.updateItem(game, bln);
                    if (game != null) {
                        
                        //User u = impl.getUserByToken(token);
                        setText("Game: "+game.getId()+ '\n'+game.getAmountOfPlayers() + "/"+game.getMaxUsers()+" Players");
                    }else {
                        setGraphic(null);
                    }
                }
                
            };
            
            return cell;
        });
        ownGamesView.setItems(ownGames);
        ownGamesView.setCellFactory((ListView<Game> p) -> {
            ListCell<Game> cell = new ListCell<Game>(){
                
                @Override
                protected void updateItem(Game game, boolean bln) {
                    super.updateItem(game, bln);
                    if (game != null) {
                        
                        try {
                            User user = null;
                            List<User> userList = impl.getSpelersList(game.getId(),privateGame);
                            for (User u: userList){
                                if(u != null && u.getToken().equals(token)){
                                    user = u;
                                }
                            }
                            if (game.getTurn() == game.getPlayers().indexOf(user)){
                                setText("Game: "+game.getId()+ "\n Your turn");
                            }else{
                                setText("Game: "+game.getId()+ "\n "+ game.getPlayers().get(game.getTurn()).getLogin() +" turn");
                            }
                        
                        } catch (RemoteException ex) {
                            Logger.getLogger(PrivateLobbyController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }else{
                        setGraphic(null);
                    }
                }
                
            };
            
            return cell;
        });
        ActionEvent e = new ActionEvent();
        try {
            refresh(e);
            //handView.getSelectionModel().getSelectionMode(Selectionmode.MULTIPLE);
            //users toevoegen
        } catch (IOException ex) {
            Logger.getLogger(PrivateLobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    } 
    
    @FXML
    public void refresh(ActionEvent event) throws IOException{
        
            List<Game> privateGames = impl.getAllPrivateGames(token);
            lobbyView.getItems().clear();
            lobbyView.getItems().setAll(privateGames);
            
            List<Game> ownPrivGames = impl.getAllParticipatingGames(token);
            ownGamesView.getItems().clear();
            ownGamesView.getItems().setAll(ownPrivGames);
            
    }
    @FXML
    public void joinPrivateGame(ActionEvent event)throws RemoteException, IOException, InterruptedException{
        Game g = null;
        if(lobbyView.getSelectionModel().isEmpty() && ownGamesView.getSelectionModel().isEmpty()){
            //nothing selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No game selected");
            alert.setHeaderText("Try clicking on a game and hit 'join' again");
            alert.setContentText("If you can't see any game, try refreshing, if you still can't see a game you can always make one yourself and wait for others to join!");

            alert.showAndWait();
            
        }else{
            if(!ownGamesView.getSelectionModel().isEmpty()){
                g = ownGamesView.getSelectionModel().getSelectedItem();
                //check if not finished
                /*if(impl.getFinished(gameID, privateGame)){
                    
                }*/
            }
            else{
                g = lobbyView.getSelectionModel().getSelectedItem();
                //need to join game check space and !started
            }
            //we got a game
            gameID = g.getId();
            
            if(impl.joinPrivateGame(gameID, token)){
                System.out.println("gameID: "+gameID);        
                gameController = new GameController();
                gameController.redirectGame(gameID,token,impl,myRegistry,username, true);

                Parent root = FXMLLoader.load(getClass().getResource("/gui/gameView.fxml"));
                Scene scene = new Scene(root);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(scene);
                //gameController.loadGame(gameID);
                window.show();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game not available");
                alert.setHeaderText("Game if full, game has already started without you, game has finished");
                alert.setContentText("try refreshing and joining a different game");

                alert.showAndWait();
                //check if game has finished if true show result
            }
        }
        
        
        
        
    }
    
    @FXML
    public void createPrivateGame(ActionEvent event) throws IOException{
        List<Integer> choices = new ArrayList<>();
        choices.add(2);
        choices.add(3);
        choices.add(4);

        ChoiceDialog<Integer> dialog;
        dialog = new ChoiceDialog<>(4, choices);
        dialog.setTitle("Creating private game");
        dialog.setHeaderText("What is the max amount of players?");
        dialog.setContentText("Players:");
        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(
            letter -> {
                try {
                    gameID = impl.getCreatePrivateGame(token, letter);
                } catch (RemoteException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
        );
        

        System.out.println("gameID: "+gameID);        
        gameController = new GameController();
        gameController.redirectGame(gameID,token,impl,myRegistry,username, true);
        gameController.setPrivateGame(true);
        Parent root = FXMLLoader.load(getClass().getResource("/gui/gameView.fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        //gameController.loadGame(gameID);
        window.show();
    }
    
    
    @FXML
    public void returnToHome(ActionEvent event) throws IOException{ 

        Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);

        window.show(); 
    }
    
    public void redirectPrivateLobby(String token, Communication impl, Registry myRegistry, String username) {
        this.impl = impl;
        this.myRegistry = myRegistry;    
        this.token = token;
        this.username = username;
        
        
    }
}

