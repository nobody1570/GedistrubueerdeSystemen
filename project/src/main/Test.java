/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas
 */
public class Test {
    public static void main(String[] args) {

        Main_S_D_Com_Controller mainSDComController = new Main_S_D_Com_Controller();
        mainSDComController.startCSComContr();

        Main_C_S_Com_Controller mainCSComController = new Main_C_S_Com_Controller();
        mainCSComController.startCSComContr();

        MainDatabase mainDB = new MainDatabase();
        mainDB.startDatabase();
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        MainServer main = new MainServer();
        main.startServer();
                
    }
}
