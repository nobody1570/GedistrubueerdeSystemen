package model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import CommunicationControllers.InterfaceDBController;
import CommunicationControllers.SimplePortDatabaseImpl;
import communication.DatabaseCommunication;

public class Database extends UnicastRemoteObject implements DatabaseCommunication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// implementation of InterfaceDBController

	// Communicatie-constanten
	private static Registry controlRegistry;
	private static final String localhost = Constants.Constants.localhost;

	// connection to database
	private Connection con;

	// user
	private PreparedStatement createUser;
	private PreparedStatement updateUser;
	private PreparedStatement deleteUser;
	private PreparedStatement getUser;
	private PreparedStatement getUserWithLogin;
	private PreparedStatement getUserWithToken;
	private PreparedStatement getHighestUserID;

	// game
	private PreparedStatement createGame;
	private PreparedStatement readGame;
	private PreparedStatement createCard;
	private PreparedStatement readCard;

	int dbPort;
	InterfaceDBController idbc;
	List<SimplePortDatabaseImpl> otherDBs;

	public Database(InterfaceDBController idbc, int port) throws RemoteException {

		dbPort = port;
		this.idbc = idbc;
		otherDBs = new ArrayList<>();

		try {

			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection("jdbc:sqlite:database" + dbPort + ".db");

			if (con != null) {
				// prepaperedstatements hieronder

				// user
				// voor nieuwe users aan te maken
				String insertInUser = "INSERT INTO User" + "(id, login, password, salt_password) VALUES" + "(?,?,?,?)";
				createUser = con.prepareStatement(insertInUser);

				// read user
				String getUserString = "SELECT id, login, salt_password, password, salt_token, token, timestamp, score FROM User WHERE id = ?";
				getUser = con.prepareStatement(getUserString);

				String getUserLogin = "SELECT id, login, salt_password, password, salt_token, token, timestamp, score FROM User WHERE login = ?";
				getUserWithLogin = con.prepareStatement(getUserLogin);
				
				String getUserToken = "SELECT id, login, salt_password, password, salt_token, token, timestamp, score FROM User WHERE token = ?";
				getUserWithToken = con.prepareStatement(getUserToken);

				// update user
				String updateUserString = "UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?, timestamp = ?, score = ? WHERE id = ?";
				updateUser = con.prepareStatement(updateUserString);

				// delete user
				String deleteUserString = "DELETE FROM User WHERE id = ?";
				deleteUser = con.prepareStatement(deleteUserString);

				// get max id of users
				String getHighestUserIDString = "SELECT MAX(id) FROM User";
				getHighestUserID = con.prepareStatement(getHighestUserIDString);

				// game
				// insert game
				String insertGame = "INSERT INTO game"
						+ "(game_id, player1, player2, player3, player4, turn, direction, last_colour, last_number) VALUES"
						+ "(?,?,?,?,?,?,?,?,?)";
				createGame = con.prepareStatement(insertGame);

				// get game
				String getGame = "SELECT game_id, player1, player2, player3, player4, turn, direction, last_colour, last_number FROM game WHERE game_id = ?";
				readGame = con.prepareStatement(getGame);

				// insert cards from game
				String insertCard = "INSERT INTO cards" + "(game_id, user_id, colour, number) VALUES" + "(?,?,?,?)";
				createCard = con.prepareStatement(insertCard);

				// get cards

				String getCards = "SELECT colour, number FROM cards WHERE game_id = ? AND user_id= ?";
				readCard = con.prepareStatement(getCards);

				System.out.println("ready");
			}

		} catch (Exception e) {
			System.out.println("no connection");
			e.printStackTrace();

		}

	}

	
	//getting other databases
	public void init() throws RemoteException {
		// wait until other db's are online
		Scanner sc = new Scanner(System.in);

		System.out.println("Press enter to proceed, only proceed when all databases are online");
		sc.nextLine();
		sc.close();

		System.out.println("----------------------------------------------------------");

		// get other databases

		List<Integer> temp = idbc.getAllDatabasesPorts();

		for (int i : temp) {

			if (i != dbPort) {

				otherDBs.add(new SimplePortDatabaseImpl(i));
			}

		}
	}

	@Override
	public void closeConnection() throws RemoteException {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("connection closed");
		}

	}

	// https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	// preparedstatements in constructor maken en hier invullen en uitvoeren
	@Override
	public void createUser(User u) throws RemoteException {
		// "INSERT INTO User(id, login, password) VALUES"(?,?,?)

		System.out.println("creating new user");
		idbc.getWriteAccess(dbPort);
		try {

			createUser.setInt(1, u.getId());
			createUser.setString(2, u.getLogin());
			createUser.setString(3, u.getPassword());
			createUser.setString(4, u.getSalt_password());
			createUser.executeUpdate();
			System.out.println("new user created");

			// propagate to other servers
			int port;
			for (SimplePortDatabaseImpl dbc : otherDBs) {

				port = dbc.getPort();
				idbc.getWriteAccess(port);
				try {

					dbc.getDc().nonPropagateCreateUser(u);

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				idbc.releaseWriteAccess(port);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idbc.releaseWriteAccess(dbPort);
	}

	@Override
	public void nonPropagateCreateUser(User u) throws RemoteException {
		// TODO Auto-generated method stub
		try {

			System.out.println("creating new user propagated from other server");
			createUser.setInt(1, u.getId());
			createUser.setString(2, u.getLogin());
			createUser.setString(3, u.getPassword());
			createUser.executeUpdate();
			System.out.println("new user created propagated from other server");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public User readUser(String login) throws RemoteException {
		// SELECT id, login, salt_password, password, salt_token, token, timestamp FROM
		// User WHERE login = ?

		System.out.println("reading user");
		idbc.getReadAccess(dbPort);
		User result = null;
		try {
			getUserWithLogin.setString(1, login);
			ResultSet rs = getUserWithLogin.executeQuery();

			if (rs.next())
				result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7), rs.getInt(8));

			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idbc.releaseReadAccess(dbPort);
		return result;
	}

	@Override
	public User readUser(int i) throws RemoteException {

		// SELECT id, login, salt_password, password, salt_token, token, timestamp FROM
		// User WHERE id = ?

		System.out.println("reading user");
		idbc.getReadAccess(dbPort);
		User result = null;
		try {
			getUser.setInt(1, i);
			ResultSet rs = getUser.executeQuery();

			if (rs.next())
				result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7), rs.getInt(8));

			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idbc.releaseReadAccess(dbPort);
		return result;
	}
	
	@Override
	public User readTokenUser(String token) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("reading user");
		idbc.getReadAccess(dbPort);
		User result = null;
		try {
			getUserWithToken.setString(1, token);
			ResultSet rs = getUserWithToken.executeQuery();

			if (rs.next())
				result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7), rs.getInt(8));

			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idbc.releaseReadAccess(dbPort);
		return result;
		
	}

	@Override
	public void updateUser(User u) throws RemoteException {
		// Timestamp ID NIET Ge�mplementeerd!
		// UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?,
		// timestamp = ? WHERE id = ?

		System.out.println("updating user: "+ u);
		idbc.getWriteAccess(dbPort);
		try {

			updateUser.setString(1, u.getSalt_password());
			updateUser.setString(2, u.getPassword());
			updateUser.setString(3, u.getSalt_token());
			updateUser.setString(4, u.getToken());
			updateUser.setLong(5, u.getTimestamp());
			updateUser.setInt(6, u.getScore());
			updateUser.setInt(7, u.getId());
			updateUser.executeUpdate();

			// propagate to other servers
			int port;
			for (SimplePortDatabaseImpl dbc : otherDBs) {

				port = dbc.getPort();
				idbc.getWriteAccess(port);
				try {

					dbc.getDc().nonPropagateUpdateUser(u);

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				idbc.releaseWriteAccess(port);

			}
			System.out.println("user updated");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		idbc.releaseWriteAccess(dbPort);

	}

	@Override
	public void nonPropagateUpdateUser(User u) throws RemoteException {
		// TODO Auto-generated method stub

		try {
			System.out.println("updating user propagated from other database");
			updateUser.setString(1, u.getSalt_password());
			updateUser.setString(2, u.getPassword());
			updateUser.setString(3, u.getSalt_token());
			updateUser.setString(4, u.getToken());
			updateUser.setLong(5, u.getTimestamp());
			updateUser.setInt(6, u.getScore());
			updateUser.setInt(7, u.getId());
			updateUser.executeUpdate();
			System.out.println("user updated propagated from other database");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int getHighestID() throws RemoteException {
		System.out.println("searching highest user id");
		int maxID = -1;
		try {

			getHighestUserID.execute();
			ResultSet rs = getHighestUserID.getResultSet();

			if (rs.next()) {
				maxID = rs.getInt(1);
			}
			System.out.println("max id received");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxID;

	}
	
	@Override
	public int getNextID() throws RemoteException {
		// TODO Auto-generated method stub
		return idbc.getNewUserID();
	}


	@Override
	public void deleteUser(User u) throws RemoteException {

		deleteUser(u.getId());

	}

	@Override
	public void deleteUser(int i) throws RemoteException {

		// DELETE User WHERE id = ?
		System.out.println("deleting user");
		idbc.getWriteAccess(dbPort);
		try {

			deleteUser.setInt(1, i);
			deleteUser.executeUpdate();

			System.out.println("user deleted");

			int port;
			for (SimplePortDatabaseImpl dbc : otherDBs) {

				port = dbc.getPort();
				idbc.getWriteAccess(port);
				try {

					dbc.getDc().nonPropagateDeleteUser(i);

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				idbc.releaseWriteAccess(port);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idbc.releaseWriteAccess(dbPort);

	}

	@Override
	public void nonPropagateDeleteUser(int i) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("deleting propagated user");
		try {

			deleteUser.setInt(1, i);
			deleteUser.executeUpdate();

			System.out.println("propagated user deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	// NOT DISTRIBUTED!
	public void saveGame(Game g) throws RemoteException {

		// "(game_id, player1, player2, player3, player4, turn, direction, last_colour,
		// last_number) VALUES"
		// + "(?,?,?,?,?,?,?,?,?)";

		System.out.println("saving game");
		List<User> players = g.getPlayers();
		try {

			// add players to database
			createGame.setInt(1, g.getId());
			createGame.setInt(2, players.get(0).getId());
			createGame.setInt(3, players.get(1).getId());

			if (players.get(2) != null)
				createGame.setInt(4, players.get(2).getId());

			if (players.get(3) != null)
				createGame.setInt(5, players.get(3).getId());

			createGame.setInt(6, g.getTurn());
			createGame.setInt(7, g.getDirection());
			createGame.setInt(8, g.getLastColour());
			createGame.setInt(9, g.getLastNumber());

			createGame.executeUpdate();

			// add cards to database
			int gameID = g.getId();
			// "INSERT INTO cards" + "(game_id, user_id, colour, number) VALUES" +
			// "(?,?,?,?)";

			// voor alle users
			List<Card> cl;
			User u;
			int userID;
			for (int i = 0; i < Game.MAX_USERS; i++) {

				u = players.get(i);

				if (u != null) {

					cl = g.getHand(u);
					userID = u.getId();

					for (Card c : cl) {

						createCard.setInt(1, gameID);
						createCard.setInt(2, userID);
						createCard.setInt(3, c.getColourValue());
						createCard.setInt(4, c.getNumber());
						createCard.executeUpdate();

					}

				}
			}

			System.out.println("game saved");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	// NOT DISTRIBUTED!
	public Game readGame(int gameID) throws RemoteException {
		// "SELECT game_id, player1, player2, player3, player4, turn, direction,
		// last_colour, last_number FROM game WHERE game_id = ?"
		System.out.println("reading game");
		Game g = null;
		try {
			readGame.setInt(1, gameID);
			ResultSet rsg = readGame.executeQuery();
			// creating game
			if (rsg.next()) {
				g = new Game(rsg.getInt(1));

				Integer p1 = rsg.getInt(2);
				Integer p2 = rsg.getInt(3);
				Integer p3 = rsg.getInt(4);
				Integer p4 = rsg.getInt(5);

				ArrayList<Integer> al = new ArrayList<Integer>(Arrays.asList(p1, p2, p3, p4));

				int amountOfPlayers = 2;

				if (p3 != null)
					amountOfPlayers++;
				if (p4 != null)
					amountOfPlayers++;

				for (int i = 0; i < amountOfPlayers; i++) {

					g.addPlayer(readUser(al.get(i)));
				}

				// add cards

				List<User> l = g.getPlayers();

				for (int i = 0; i < amountOfPlayers; i++) {

					List<Card> lc = g.getHand(l.get(i));

					readCard.setInt(1, gameID);
					readCard.setInt(2, l.get(i).getId());

					ResultSet rs = readCard.executeQuery();

					while (rs.next()) {

						lc.add(new Card(rs.getInt(1), rs.getInt(2)));

					}

				}

				// set rest of the values.

				g.setStarted(true);

				g.setTurn(rsg.getInt(6));
				g.setDirection(rsg.getInt(7));
				g.setLastColour(rsg.getInt(8));
				g.setLastNumber(rsg.getInt(9));

				g.removeHandsFromStack();
				g.shuffleCards();

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("read game");
		return g;
	}

	@Override
	// NOT DISTRIBUTED!
	public void deleteGame() throws RemoteException {

	}


	@Override
	public synchronized void addScore(User u, int gameScore) throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			getUser.setInt(1, u.getId());
			ResultSet rs = getUser.executeQuery();

			if (rs.next())
				u = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7), rs.getInt(8));

			
			
			
			u.setScore(u.getScore()+gameScore);
			
			
			nonPropagateUpdateUser(u);
			
			for (SimplePortDatabaseImpl dbc : otherDBs) {
				
				
				dbc.getDc().nonPropagateaddScore(u, gameScore);
				
				
			}
			
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
	}


	public synchronized void nonPropagateaddScore(User u, int gameScore) throws RemoteException{
		// TODO Auto-generated method stub
		
		
		try {
			getUser.setInt(1, u.getId());
			ResultSet rs = getUser.executeQuery();

			if (rs.next())
				u = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7), rs.getInt(8));

			
			
			
			u.setScore(u.getScore()+gameScore);
			
			
			nonPropagateUpdateUser(u);
			
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}





	
}