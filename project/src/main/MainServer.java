/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Jonas
 */
import communication.CommunicationImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class MainServer {
    private void startServer() {
        try {
                // create on port 1099
                Registry registry = LocateRegistry.createRegistry(1099);
                // create a new service named CounterService
                registry.rebind("CommunicationService", new CommunicationImpl());
        } catch (Exception e) {
        e.printStackTrace();
        }


        System.out.println("system is ready");
    }
    public static void main(String[] args) {

        MainServer main = new MainServer();
        main.startServer();
    }
}
