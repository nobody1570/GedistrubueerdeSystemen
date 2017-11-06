/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import model.Card.Colour;

public class Game {

	

	int id;
	public static final int MAX_USERS = 4;
	public static final int START_CARDS = 4;
	
	//users
	List<User> users=new ArrayList<User>(MAX_USERS);
	
	//cards of user
	List<ArrayList<Card>> cards=new ArrayList<ArrayList<Card>>(MAX_USERS);

	LinkedList<Card> deck;

	Boolean started;
	
	int amountOfPlayers=0;
	
	int turn;//speler aan beurt 0 1 2 3
	int direction=1;//1 klokwijzerszin  -1 tegenklokwijzerszin
	
	Card LastCard;
	Colour current;
	Colour preferred;

	public Game(int id) {

		this.id=id;
		deck = new LinkedList<Card>();
		//alle kaarten toevoegen
		
		for(int i=0;i<15;i++) {
			for (int j=0;j<4;j++) {
				
				deck.add(new Card(j,i));
				
			}	
		}
		
		for(int i=1;i<13;i++) {
			for (int j=0;j<4;j++) {
				
				deck.add(new Card(j,i));
				
			}	
		}
		
		//kaarten toegevoegd (UNO heeft 4 != nullen, 4 +4 en 4 kleur veranderen en 2 v/d rest(deze zijn wel gelijk))

		
		//kaarten schudden
		shuffleCards();

		for(int i=0;i<MAX_USERS;i++) {
			
			users.add(null);
			
			cards.add(new ArrayList<Card>()) ;
			
		}
		
		started=false;
		int turn=0;

	}

    

	public void shuffleCards() {
		Collections.shuffle(deck);
	}

        public Card getLastCard(){
            return LastCard;
        }
	
	public void addPlayer(User u) {
		//niet in spel en nog plaats vrij
		if(!users.contains(u)&&countPlayers()<MAX_USERS) {
			
			Boolean found =false;
			int i=0;
			while(!found&&i<MAX_USERS) {
				
				if(users.get(i)==null) {
					
					users.set(i,u);
					found=true;
					amountOfPlayers++;
				}
				
				i++;
			}
			
			//TODO eventueel kaarten toevoegen
			if(started&&found) {
					
					int max=0;
					//find max amount of cards
					for(int j=0;j<amountOfPlayers;j++) {
						if(cards.get(j).size()>max) {max=cards.get(j).size();}
					}
					
					//give that amount to new player
					takeCards(u,max);
				}
		}
		
	}
	
	private int countPlayers() {
		int count=0;
		for (int i=0;i<MAX_USERS;i++)
		if(users.get(i)!=null)count++;
		
		return count;
	}
        public int getAmountOfPlayers(){
            return amountOfPlayers;
        }
	
	public void removePlayer(User u) {
		
		if (users.contains(u)) {
			boolean found =false;
			int i=0;
			while(!found){
				
				if(users.get(i).equals(u)) {
					found=true;
					users.set(i, null);
					deck.addAll(cards.get(i));
					cards.get(i).clear();
					cleanUsers();
					amountOfPlayers--;
				}
				i++;
			}
			if(turn>amountOfPlayers-1) {
					
					turn++;
					turn=turn%(amountOfPlayers+1);
				}
			
			//TODO kaarten op deck smijten
		}
		
	}

	private void cleanUsers() {
		// moves all users with a null value to the back of our list
		//makes sure the cards follow the users
		//werkt.
		for (int i=0;i<(MAX_USERS-1);i++) {
			
			if(users.get(i)==null) {
				int k=i;
				do {
				users.set(k, users.get(k+1));
				users.set(k+1, null);
				
				ArrayList <Card> aid=cards.get(k);
				cards.set(k, cards.get(k+1));
				cards.set(k+1, aid);
				k++;
				}while(k<MAX_USERS-2);
				
			}
			
			
			
		}
		
	}
	
	public void start() {
            if(amountOfPlayers>1){
                started=true;
                for(int i=0;i<amountOfPlayers;i++) {

                    takeCards(users.get(i),7);
                }
                Card c;
                do{
                    c=deck.getFirst();
                    shuffleCards();
                    System.out.println("beginkaart: "+c);
                
                }while(c.getColour() == Colour.ANY && c.getNumber()>=10);
                
                LastCard = c;
                current= c.getColour();
                deck.remove(c);
                
	    }
            
	}
	
	
	public void SetPreferredColour(User u,Colour c) {
		
		if(u.equals(users.get(turn)))preferred=c;
		
	}
	//
	//null-->kaart nemen. c-->kaart afleggen
	//checkt of speler mag afleggen
	public boolean performTurn(User u,Card c){
		boolean ok=false;
		
		if(u.equals(users.get(turn))) {
			
			if(c!=null) {
                            ok=playCard(u,c);
                            if(ok)endTurn();
				
			}
			
			else{				
                            takeCard(u);
                            endTurn();
                            ok = true;				
			}			
		}		
		return ok;
	}
	
	//gebruikt in performturn
	private void takeCard(User u) {
		boolean found=false;
		int i=0;
		if(u!=null)
		if(users.contains(u))
		while(i<MAX_USERS&&!found) {
			
			if (users.get(i).equals(u)) {
				found=true;
				
				if(!deck.isEmpty())
				cards.get(i).add(deck.removeFirst());
				//nog else nodig indien deck leeg is refill?
			}			
			i++;
		}				
	}
	
	
	
	//voor kaarten te spelen
	//controleerd of speler deze mag afleggen
	private boolean playCard(User u, Card c) {

		Boolean ok = false;
		
		
		if (cards.get(turn).contains(c)&&playCardAllowed(c)) {
			
			ok = true;
			cards.get(turn).remove(c);
			deck.addLast(c);
			LastCard=c;
			current=c.getColour();

			performCardActions(c);
		}

		return ok;

	}
	
	//gebruikt in playcard
	public boolean playCardAllowed(Card c) {
		boolean allowed=false;
		
		if(current==c.getColour()||c.getColour()==Colour.ANY||c.sameNumber(LastCard)) {
			allowed =true;
		}
		
		return allowed;
	}

	//gebruikt in playcard
	private void performCardActions(Card c) {
		// special actions handler
		
		switch(c.getNumber()) {
		
		case 10: endTurn(); break;
		case 11: direction=direction*(-1); break;
		case 12:  endTurn(); takeCards(users.get(turn),2);break;
		case 13:  if(preferred!=null)current=preferred; break;
		case 14:  if(preferred!=null)current=preferred; endTurn(); takeCards(users.get(turn),4);break;
		
		default:;
		}

	}
	
	private void endTurn() {
		turn=turn+direction;
		if(turn<0)turn=turn+amountOfPlayers;
		turn=turn%amountOfPlayers;
	}
        
        public List<User> getPlayers(){
            return users;
        }
	
	//gebruikt in performCardActions
        private void takeCards(User u,int a) {
                boolean found=false;
                int i=0;
                if(users.contains(u))
                while(i<MAX_USERS&&!found) {

                        if (users.get(i).equals(u)) {
                                found=true;

                                for(int j=0;j<a&&!deck.isEmpty();j++) {
                                        cards.get(i).add(deck.removeFirst());
                                        }
                        }

                        i++;
                }
        }   

	//probleem!
	//kan niet werken User.equals kijkt alleen naar het id v/d user
	//is beter als users hun eigen id doorgeven.
        public List<Card> getHand(User u){
            for (int i = 0; i<4;i++){
                if (users.get(i).equals(u)){
                    return cards.get(i);
                }
            }
            return null;
        }
        
        public Boolean getStarted() {
    		return started;
    	}

    	public void setStarted(Boolean started) {
    		this.started = started;
    	}

    	public int getId() {
    		return id;
    	}
        
        public int getTurn(){
            return turn;
        }
        public void setTurn(int turn) {
            this.turn=turn;
        }
        
        public int getDirection() {
            return direction;
        }
        
        public void setDirection(int direction) {		
            this.direction=direction;
        }
        
        public int getLastColour() {		
            switch (current) {		
            case GREEN: return 1; 		
            case BLUE: return 2; 		
            case YELLOW: return 3; 		
            case ANY: return 4; 		
            default: return -1;		
           }		
        }
        
        public void setLastColour(int lastColour) {		
            switch (lastColour) {		
            case 0: current=Colour.RED; break;		
            case 1: current=Colour.GREEN; break;		
            case 2: current=Colour.BLUE; break;		
            case 3: current=Colour.YELLOW; break;		
            }		
        }
        
        public int getLastNumber() {
            return LastCard.getNumber();
        }
        
        public void setLastNumber(int lastNumber) {
            LastCard=new Card(-1,lastNumber);
        }
        
        //ONLY USE IN DATABASE-CLASS!!!!!!!!!!!!!!!
        public void removeHandsFromStack() {
            for(int i=0;i<MAX_USERS;i++) {		
                for(Card c:cards.get(i)){
                    deck.remove(c);
                }		
                        
            }
        }
        
        public Colour getCurrent() {
            return current;
        }
        
        @Override
        public String toString() {
                return "Game [users=" + users + ",\n cards=" + cards + ",\n  started=" + started + ",\n  amountOfPlayers="
                                + amountOfPlayers + ",\n turn=" + turn + ",\n direction=" + direction + ",\n LastCard=" + LastCard
                                + ",\n current=" + current + ",\n preferred=" + preferred + "]";
        }

    

    	
	
	
	
	
}