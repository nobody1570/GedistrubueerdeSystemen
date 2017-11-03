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

	Game(int id) {

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

    public Game() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	void shuffleCards() {
		Collections.shuffle(deck);
	}

	
	void addPlayer(User u) {
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
			
		}
		
	}
	
	int countPlayers() {
		int count=0;
		for (int i=0;i<MAX_USERS;i++)
		if(users.get(i)!=null)count++;
		
		return count;
	}
	
	void removePlayer(User u) {
		
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
	
	//
	//null-->kaart nemen. c-->kaart afleggen
	//checkt of speler mag afleggen
	boolean performTurn(User u,Card c){
		boolean ok=false;
		
		if(u.equals(users.get(turn))) {
			
			if(c!=null) {
				
				
				ok=playCard(u,c);
				endTurn();
				
			}
			
			else
				
			{
				
				takeCard(u);
				
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
				
			}
			
			i++;
		}
		
		
	}
	
	
	
	//voor kaarten te spelen
	//controleerd of speler deze mag afleggen
	boolean playCard(User u, Card c) {

		Boolean ok = false;
		int position = findUserPosition(u);

		if (cards.get(position).contains(c)) {
			ok = true;
			cards.get(position).remove(c);
			deck.addLast(c);
			LastCard=c;

			performCardActions(c);
		}

		return ok;

	}

	//gebruikt in playcard
	private void performCardActions(Card c) {
		// special actions handler
		
		switch(c.getNumber()) {
		
		case 10: endTurn(); break;
		case 11: direction=direction*(-1); break;
		case 12:  endTurn(); takeCards(users.get(turn),2);break;
		case 13:  break;//kleurverandering met boolean?
		case 14:  endTurn(); takeCards(users.get(turn),4);break;//kleurverandering met boolean?
		
		default:;
		}

	}
	
	void endTurn() {
		turn=turn+direction;
		turn=turn%amountOfPlayers;
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



	Integer findUserPosition(User u) {
		Integer result = null;

		boolean found = false;
		int i = 0;
		if (users.contains(u))
			while (i < MAX_USERS && !found) {

				if (users.get(i).equals(u)) {
					found = true;
					result = i;

				}

				i++;
			}

		return result;
	}
	
	//probleem!
	//kan niet werken User.equals kijkt alleen naar het id v/d user
	//is beter als users hun eigen id doorgeven.
        public List<Card> getHand(String username){
            for (int i = 0; i<4;i++){
                if (users.get(i).equals(username)){
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

	@Override
	public String toString() {
		return "Game [id=" + id + ",\n      users=" + users + ",\n      cards=" + cards + ",\n      deck=" + deck + ",\n      started=" + started
				+ "]";
	}
	
	
	
	
}