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
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Jonas
 */
public class LoginController implements Initializable {

    private static Communication impl;
    private static Registry myRegistry;
    private static int userID;
    
    private Controller controller;
    @FXML private Stage stage;
    @FXML 
    private Label label;
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    
    
    @FXML
    private void loginButtonAction(ActionEvent event) throws IOException{
        System.out.println(username.getText() + " "+ password.getText());
        //controleren gegevens zenden naar server
        //pw gwn doorsturen TODO
        userID = impl.login(username.getText(),password.getText());
        System.out.println(userID + " "+ username.getText());
        //met DB
        /*try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","");
            Statement st = con.createStatement();
            ResultSet rs = st.execu
        }*/
        //if gegevens ok login else geef foutmelding
        if (userID >= 0){
            //redirect naar lobby
            controller = new Controller();
            
            Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
            Scene scene = new Scene(root);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            controller.redirectLobby(userID,impl,myRegistry, username.getText());
            
            window.setOnCloseRequest(evt -> {		
	                // prevent window from closing		
	                evt.consume();		
			
	                // execute own shutdown procedure		
	                shutdown(window);		
	            });
            window.show();
            
            
        }
        else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("incorrect login information!");

            alert.showAndWait();
        }
    
    }
    
    private void shutdown(Stage mainWindow) {		
        
        if(controller.getGameID() != -1){
            Alert alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);		
            alert.setTitle("Closing UNO");		
            alert.setHeaderText("Do you really wish to close the program?");		
            alert.setContentText("Progress might be lost. Unfinished games will be counted as a loss.");		
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {		
                try {
                impl.endGame(controller.getGameID(), userID);
                } catch (RemoteException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
                mainWindow.close();
            }   
        }      
        else{	            
            mainWindow.close();
        }
    }
    
    @FXML
    private void registerButtonAction(ActionEvent event) throws IOException{
        if(impl.createNewAccount(username.getText(),password.getText())){
            loginButtonAction(event);
        }
        else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("registration fialed");
            alert.setContentText("username already in use!");

            alert.showAndWait();
        }
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        username.setPromptText("username");
        password.setPromptText("password");
    }    

    public void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public void setCommunication(Communication impl, Registry myRegistry) {
        this.impl = impl;
        this.myRegistry = myRegistry;
    }
    
}
