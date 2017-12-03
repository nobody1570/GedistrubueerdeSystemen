package model;

import java.rmi.RemoteException;
import java.util.ArrayList;

import model.Card.Colour;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Database db=new Database();
			
			User u= new User(14, "bla125", "fu", "er", "yu", "er", System.currentTimeMillis(), 212);
			db.updateUser(u);
			
			
			
			
			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
