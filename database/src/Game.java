
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
	List<Set<Card>> cards=new ArrayList<Set<Card>>(MAX_USERS);

	LinkedList<Card> deck;

	Boolean started;

	Game() {

		deck = new LinkedList<Card>();
		// TODO alle kaarten toevoegen

		shuffleCards();

		for(int i=0;i<MAX_USERS;i++)cards.set(i, new TreeSet<Card>()) ;
		
		started=false;

	}

	void shuffleCards() {
		Collections.shuffle(deck);
	}

	
	void addPlayer(User u) {
		
		if(!users.contains(u)&&countPlayers()<MAX_USERS) {
			
			Boolean found =false;
			int i=0;
			while(!found&&i<MAX_USERS) {
				
				if(users.get(i)==null) {
					
					users.set(i,u);
					found=true;
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
					
					users.set(i, null);
					cleanUsers();
				}
				i++;
			}
			
			
			//TODO kaarten op deck smijten
		}
		
	}

	private void cleanUsers() {
		// moves all users with a null value to the back of our list
		//zou moeten werken
		for (int i=0;i<(MAX_USERS-1);i++) {
			
			if(users.get(i)==null) {
				int k=i;
				do {
				users.set(k, users.get(k+1));
				k++;
				}while(k<MAX_USERS-2);
				
			}
			
			
			
		}
		
	}
	
	
	
	
}