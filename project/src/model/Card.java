/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Rectangle;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class Card extends Parent{

	Colour c;
	int number;
	//TODO comparator
	

	//10,11,12,13 en 14 zijn speciale nummers
	//10: skip
	//11: switch direction
	//12: +2
	//13: change colour
	//14: +4 change colour
	public Card(int c, int number) {
		
		this.number = number;
		
		if(number>12) {
			this.c=Colour.ANY;
		}
		else {
			
			switch (c) {
			
			case 0: this.c=Colour.RED; break;
			case 1: this.c=Colour.GREEN; break;
			case 2: this.c=Colour.BLUE; break;
			case 3: this.c=Colour.YELLOW; break;
			
			}
		}

		
	}

    public Card() {
        
    }
	
	public Colour getColour() {
		return c;
	}
	public int getNumber() {
		return number;
	}
	
	

	//false als niet gelijk of kaart die kleur can veranderen
	boolean sameNumber(Card card) {
		boolean same=false;
		if(card.getNumber()==number)same=true;
				
		return same;
	}
	
	
	@Override
	public String toString() {
		return "Card [colour=" + c + ", number=" + number + "]";
	}
	
	
	

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (!c.equals(other.c))
			return false;
		if (number != other.number)
			return false;
		return true;
	}




	public enum Colour {
	    RED,GREEN,BLUE,YELLOW,ANY
	}


	
}
