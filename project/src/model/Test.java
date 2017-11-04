package model;

import java.util.ArrayList;

import model.Card.Colour;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Game g=new Game(1);
		
		User u1= new User(1,null,null,null);
		User u2= new User(2,null,null,null);
		User u3= new User(3,null,null,null);
		User u4= new User(4,null,null,null);
		
		g.addPlayer(u1);
		g.addPlayer(u2);
		g.addPlayer(u3);
		g.addPlayer(u4);
		
		ArrayList <Card> cards=new ArrayList<Card>();
		
		
		
		for(int i=8;i<=14;i++) 
		cards.add(new Card(0,i));
		
		cards.add(new Card(2,9));
		
		//is smerig maar gemakkelijk om te testen.
		g.cards.get(0).addAll(cards);
		
		
		
		
		/*g.performTurn(u1, null);
		g.performTurn(u2, null);
		g.performTurn(u3, null);
		g.performTurn(u1, null);
		g.performTurn(u3, null);
		System.out.println(g);*/
		
		//performTurn: werkt enkel voor user aan beurt
		//null--> kaart bijpakken
		//Anders kaart object meegeven -->geeft true terug als ok anders false.
		g.LastCard=new Card(0,5);
		g.current=Colour.RED;
		
		System.out.println(g);
		g.performTurn(u1, new Card(0,9));
		g.performTurn(u2, null);
		g.performTurn(u3, null);
		g.performTurn(u4, null);
		System.out.println(g);
		
		
				Card test=new Card(2,9);
				
				
				
				
				System.out.println(g.performTurn(u1, test));
				
				System.out.println(g);
				
				
		//om kleurveranderende kaarten te kunnen gebruiken:
		//Eerst SetPreferredColour gebruiken voor de speler die aan beurt is //nota: GEEN Colour.ANY aan meegeven!
		//daarna performTurn
		//Card test=new Card(0,13);
		//g.SetPreferredColour(u1, Colour.GREEN);
		//System.out.println(g.performTurn(u1, test));
		
		//System.out.println(g);
		

	}

}
