/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import communication.Communication;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        String r = impl.login(username.getText(),password.getText());
        System.out.println(r);
        //met DB
        /*try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","");
            Statement st = con.createStatement();
            ResultSet rs = st.execu
        }*/
        //if gegevens ok login else geef foutmelding
        if (!r.equals("nok")){
            //redirect naar lobby
            controller = new Controller();
            
            Parent root = FXMLLoader.load(getClass().getResource("/gui/lobby.fxml"));
            Scene scene = new Scene(root);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            controller.redirectLobby(username.getText(),impl,myRegistry);
            window.show();
            
            
        }
        else{
            label.setText("incorrecte gegevens");
        }
    
    }
    
    @FXML
    private void registerButtonAction(ActionEvent event) throws IOException{
        if(impl.createNewAccount(username.getText(),password.getText())){
            loginButtonAction(event);
        }
        else{
            Dialog d = new Dialog();
            d.setTitle("registration failed: Username already in use!");
            d.show();
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
