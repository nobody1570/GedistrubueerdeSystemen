/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import communication.Communication;
import communication.CommunicationImpl;
import communication.UpdateGameThread;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Card;
import model.Card.Colour;
import model.User;

/**
 * FXML Controller class
 *
 * @author Jonas
 */
public class GameController implements Initializable {
    private static Communication impl;
    private static Registry myRegistry;
    private static String username;
    
    private static String season;
    //game info
    private static int gameID;
    private static String token;
    private boolean gameFinished;
    
    private ObservableList<Card> hand = FXCollections.observableArrayList();
    private ObservableList<User> players = FXCollections.observableArrayList();
    private ObservableList<Integer> handSizes = FXCollections.observableArrayList();

    @FXML public ImageView lastCardI;
    @FXML public ListView<Card> handView;
    @FXML public ListView<User> userView;
    @FXML public ListView<Integer> handsizeView;
    
    @FXML public Label lastCard;
    public Image cardBack;
    @FXML public ImageView cardBackView;
    @FXML public Button draw;
    @FXML public Button playCard;
    @FXML public Button returnToLobby;
    @FXML Button load;
    @FXML
    private Stage stage;
    @FXML 
    private Label usernameLabel;
    
    Random random = new Random();
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        draw.setVisible(false);
        playCard.setVisible(false);
        //cardBack = new Image("/image/CARDBACK.png");
        season = new String();
        LocalDate localDate = LocalDate.now();
        if(localDate.getMonthValue() == 12 || localDate.getMonthValue() == 1 ||localDate.getMonthValue() == 2){
            season = "Winter";          
        }else{
            if(localDate.getMonthValue() == 10 || localDate.getMonthValue() == 11){
                season = "Haloween";
            }else{
                season = "Classic";
            }
        }
        
        
        gameFinished = false;
        //handView.setItems(hand);
        handView.setItems(hand);
        handView.setCellFactory((ListView<Card> p) -> {
            ListCell<Card> cell = new ListCell<Card>(){
                
                @Override
                protected void updateItem(Card card, boolean bln) {
                    super.updateItem(card, bln);
                    if (card != null) {
                        Image i = new Image("image/"+card.getColour()+"_"+card.getNumber()+".png",100,175,true,true);
                        ImageView imgView = new ImageView(i);
                        setGraphic(imgView);
                        
                    }
                }
                
            };
            
            return cell;
        });
        //handView.getSelectionModel().getSelectionMode(Selectionmode.MULTIPLE);
        //users toevoegen
        userView.setItems(players);
        userView.setCellFactory((ListView<User> p) -> {
            ListCell<User> cell = new ListCell<User>(){
                
                @Override
                protected void updateItem(User t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t != null) {
                        setText(t.getLogin()+ ":");
                        
                    }
                }
                
            };
            
            return cell;
        });
        handsizeView.setItems(handSizes);       
    } 
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
    @FXML
    public void returnToLobby(ActionEvent event) throws IOException{ 
        //pop-up om te vragen of ze zeker zijn.
    	if(!impl.getFinished(gameID)){
            Alert alert = new Alert(AlertType.CONFIRMATION, "Leaving in the middle of a game will be counted as a loss.", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Leave to lobby");
            alert.setHeaderText("Are you sure you want to leave to the lobby?");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {

                Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
                Scene scene = new Scene(root);
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(scene);

                window.show();
            }
        }
        else{
            Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
            Scene scene = new Scene(root);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);

            window.show();
        
        }
    	
        
        
        
    }
    
    
    @FXML
    public void loadGame(ActionEvent event)throws IOException, RemoteException, InterruptedException{
        usernameLabel.setText(username);
        String cardbackString = impl.getCardback(season);
        System.out.println(cardbackString);
        cardBack = new Image(cardbackString);
        cardBackView.setImage(cardBack);
                //BufferedImage capture = impl.getCardback(season);
                //cardBack = SwingFXUtils.toFXImage(capture, null);
            
        if(impl.getStarted(gameID)){
            
            load.setVisible(false);
            draw.setVisible(false);
            playCard.setVisible(false);
            returnToLobby.setVisible(false);
            update();
            System.out.println("updated");
            play();
            load.setDisable(true);
            //cardBack = new Image(impl.getCardback(season));
            Task updateTask;
            updateTask = new Task<Void>() {
                @Override public synchronized Void call() throws RemoteException, InterruptedException, IOException {
                    try {
                        Card c, previous=null;boolean finished=false;
                        while (!finished) {
                            System.out.println("runloop");
                            if(impl.waitForNewCardPlayed(gameID)){
                                c = impl.getLatestPlayedCard(gameID);

                                if(!c.equals(previous)){
                                    previous = c;                              
                                    Platform.runLater(() -> {
                                            try {
                                                update();  
                                            } catch (RemoteException ex) {
                                                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                    });
                                }
                            }
                            else{
                                finished = true;
                            }
                            
                        }
                    } catch (Exception e) {
                    e.printStackTrace();
                    }               
                    return null;
                }
            };

            new Thread(updateTask).start();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("game not started yet, still waiting for others players, try later again!");

            alert.showAndWait();
        }
        userView.getItems().setAll(impl.getSpelersList(gameID));
        

        
    }

    public void reportAndLogException(final Throwable t)throws RemoteException{
    Platform.runLater(() -> {
        System.out.println("in update");
        List<Card> hands;
        try {
            hands = impl.getHand(gameID, token);
            handView.getItems().clear();
            handView.getItems().setAll(hands);
            //playerListView.getItems().setAll(impl.getSpelersList(gameID));
            Card c = impl.getLatestPlayedCard(gameID);
            
            //ontdubbelen warning
            /*
            //indien colour last played = any
            if(c.getColour()==Colour.ANY){
                Colour current = impl.getCurrentColour(gameID);
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("New Colour is: "+ current);
                alert.setContentText("be careful, colour has changed!");
                
                alert.showAndWait();
            }*/
            lastCardI.setImage(new Image("image/"+c.getColour()+"_"+c.getNumber()+".png"));
            //lastCard.setText(c.toString());
            //handsizes wijzigen
            List<Integer> handS = impl.getSpelersHandSize(gameID);
            handsizeView.getItems().setAll(handS);
            System.out.println("einde update"); 
        } catch (RemoteException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    });
  }
    
    
    public synchronized void update() throws RemoteException{
        try{
            System.out.println("in update");
            List<Card> hands;
                
            hands = impl.getHand(gameID, token);
            handView.getItems().clear();
            handView.getItems().setAll(hands);
            //playerListView.getItems().setAll(impl.getSpelersList(gameID));
            Card c = impl.getLatestPlayedCard(gameID);
            //indien colour last played = any
            if(c.getColour()==Colour.ANY){
                Colour current = impl.getCurrentColour(gameID);
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("New Colour is: "+ current);
                alert.setContentText("be careful, colour has changed!");

                alert.showAndWait();
            }
            lastCardI.setImage(new Image("image/"+c.getColour()+"_"+c.getNumber()+".png"));
            lastCard.setText(c.toString());
            //handsizes wijzigen
            List<Integer> handS = impl.getSpelersHandSize(gameID);
            handsizeView.getItems().setAll(handS);
            System.out.println("einde update"); 

        }
        catch (Exception e)
        {
        reportAndLogException(e);
        }
    }
    
    @FXML public void playCard(ActionEvent event)throws RemoteException{
        System.out.println("playCard");
        Card c = handView.getSelectionModel()
            .getSelectedItem();
        
        if (c != null) {
            boolean playable =impl.playCardAllowed(gameID,c);

            if(playable) {
                //TODO kaart controleren clientside
                if (c.getNumber()>=13){
                    //kleur vragen aan gebruiker
                    List<Colour> choices = new ArrayList<>();
                    choices.add(Colour.BLUE);
                    choices.add(Colour.RED);
                    choices.add(Colour.GREEN);
                    choices.add(Colour.YELLOW);

                    ChoiceDialog<Colour> dialog;
                    dialog = new ChoiceDialog<>(Colour.BLUE, choices);
                    dialog.setTitle("choose a colour");
                    dialog.setHeaderText("What colour do you prefer?");
                    dialog.setContentText("Colour:");
                    Optional<Colour> result = dialog.showAndWait();
                    result.ifPresent(
                        letter -> {
                            try {
                                impl.setPrefered(gameID, token, letter);
                            } catch (RemoteException ex) {
                                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }         
                    );


                }


                //controleer if playable
                hand.remove(c);
                
                impl.playCard(token, gameID, c);
                 List<Card> hands = impl.getHand(gameID, token);
                handView.getItems().clear();
                handView.getItems().setAll(hands);
            }else{
                //not playable
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Invalid move");
                alert.setHeaderText("Playing this card is not a valid move.");
                alert.setContentText("Please play another card, this card does not have the correct colour or value to be played right now. If you can play no cards, draw a card to end your turn.");

                alert.showAndWait();
            }
        }
        draw.setVisible(false);
        playCard.setVisible(false);
        play();
         
    }
    
    @FXML void drawCard()throws RemoteException{
        System.out.println("drawCard");
        impl.drawCard(gameID, token);
        
        draw.setDisable(true);
        playCard.setDisable(true);
        draw.setVisible(false);
        playCard.setVisible(false);
        play();
    }
    
    public void play() throws RemoteException{
        System.out.println("in play");
        //thread vraagt om laatst gespeelde kaart, erna alle spelers updaten en kijken indien het spel afgelopen zou zijn
        //new UpdateGameThread(this,gameID,userID,myRegistry,impl).start();
        
        //not finished
        //System.out.println("threadstarted");
        
        //TODO nieuwe task maken om regelmatig laatste kaart up te daten    
        
            
        Task task;
        task = new Task<Void>() {
            @Override public synchronized Void call() throws RemoteException, InterruptedException, IOException {
                
                if (impl.getStarted(gameID)){
                    if(impl.myTurn(gameID,token)){
                        //myturn to play
                        Platform.runLater(() -> {
                            try {
                                System.out.println("my turn");
                                update();
                                draw.setDisable(false);
                                playCard.setDisable(false);
                                draw.setVisible(true);
                                playCard.setVisible(true);
                                System.out.println("buttons enabled");
                            } catch (RemoteException ex) {
                                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });                       
                    }
                    else{
                        //einde game popup venster wordt aangemaakt
                        Platform.runLater(() -> {
                            try {
                                System.out.println("einde game er wordt om scores gevraagd");
                                gameFinished = true;
                                endGame();
                            } catch (RemoteException ex) {
                                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });  
                        
                        
                    }

                }
                return null;
            }
        };

        new Thread(task).start();
        
    }
    public void endGame() throws RemoteException{
        //game is gedaan
        System.out.println("in endgame");
            Map<User,Integer> score = impl.getScore(gameID);
            String result = new String();
            for (Map.Entry<User,Integer> e:score.entrySet()){
                result = result.concat(e.getKey().getLogin() + ": "+ e.getValue()+'\n');
            }
            System.out.println(result);
            
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Game had ended!");
                alert.setHeaderText("The results:");
                alert.setContentText(result);
                alert.showAndWait();
                
            returnToLobby.setVisible(true);
            
                
            
    }
    
    public void redirectGame(int gameID, String token, Communication impl, Registry myRegistry, String username) {
        this.gameID = gameID;
        this.impl = impl;
        this.myRegistry = myRegistry;    
        this.token = token;
        this.username = username;
    }
    
    
}
