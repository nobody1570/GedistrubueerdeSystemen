package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Database {

	private Connection con;

	// user
	private PreparedStatement createUser;
	private PreparedStatement updateUser;
	private PreparedStatement deleteUser;
	private PreparedStatement getUser;
	private PreparedStatement getUserWithLogin;
	private PreparedStatement getHighestUserID;

	// game
	private PreparedStatement createGame;
	private PreparedStatement createCard;

	public Database() {

		try {
			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection("jdbc:sqlite:database.db");

			if (con != null) {
				// prepaperedstatements hieronder

				// user
				// voor nieuwe users aan te maken
				String insertInUser = "INSERT INTO User" + "(id, login, password) VALUES" + "(?,?,?)";
				createUser = con.prepareStatement(insertInUser);

				// read user
				String getUserString = "SELECT id, login, salt_password, password, salt_token, token, timestamp FROM User WHERE id = ?";
				getUser = con.prepareStatement(getUserString);

				String getUserLogin = "SELECT id, login, salt_password, password, salt_token, token, timestamp FROM User WHERE login = ?";
				getUserWithLogin = con.prepareStatement(getUserLogin);

				// update user
				String updateUserString = "UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?, timestamp = ? WHERE id = ?";
				updateUser = con.prepareStatement(updateUserString);

				// delete user
				String deleteUserString = "DELETE FROM User WHERE id = ?";
				deleteUser = con.prepareStatement(deleteUserString);

				// get max id of users
				String getHighestUserIDString = "SELECT MAX(id) FROM User";
				getHighestUserID = con.prepareStatement(getHighestUserIDString);

				// game
				// insert game
				String insertGame = "INSERT INTO game" + "(game_id, player1, player2, player3, player4) VALUES"
						+ "(?,?,?,?,?)";
				createGame = con.prepareStatement(insertGame);

				// insert cards from game
				String insertCard = "INSERT INTO cards" + "(game_id, user_id, colour, number) VALUES" + "(?,?,?,?)";
				createCard = con.prepareStatement(insertCard);

				System.out.println("ready");
			}

		} catch (Exception e) {
			System.out.println("no connection");
			e.printStackTrace();

		}

	}

	public void closeConnection() {

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
	public void createUser(User u) {
		// "INSERT INTO User(id, login, password) VALUES"(?,?,?)

		System.out.println("creating new user");
		try {
			createUser.setInt(1, u.getId());
			createUser.setString(2, u.getLogin());
			createUser.setString(3, u.getPassword());
			createUser.executeUpdate();
			System.out.println("new user created");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public User readUser(String login) {
		// SELECT id, login, salt_password, password, salt_token, token, timestamp FROM
		// User WHERE login = ?

		System.out.println("reading user");
		User result = null;
		try {
			getUserWithLogin.setString(1, login);
			ResultSet rs = getUserWithLogin.executeQuery();

			if (rs.next())
				result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7));

			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public User readUser(int i) {

		// SELECT id, login, salt_password, password, salt_token, token, timestamp FROM
		// User WHERE id = ?

		System.out.println("reading user");
		User result = null;
		try {
			getUser.setInt(1, i);
			ResultSet rs = getUser.executeQuery();

			if (rs.next())
				result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getLong(7));

			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public void updateUser(User u) {
		// Timestamp ID NIET Ge�mplementeerd!
		// UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?,
		// timestamp = ? WHERE id = ?
		System.out.println("updating user");
		try {

			updateUser.setString(1, u.getSalt_password());
			updateUser.setString(2, u.getPassword());
			updateUser.setString(3, u.getSalt_token());
			updateUser.setString(4, u.getToken());
			updateUser.setLong(5, u.getTimestamp());
			updateUser.setInt(6, u.getId());
			updateUser.executeUpdate();

			System.out.println("new user updated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getHighestID() {
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

	public void deleteUser(User u) {

		deleteUser(u.getId());

	}

	public void deleteUser(int i) {

		// DELETE User WHERE id = ?
		System.out.println("deleting user");
		try {

			deleteUser.setInt(1, i);
			deleteUser.executeUpdate();

			System.out.println("user deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveGame(Game g) {

		// "INSERT INTO game" + "(game_id, player1, player2, player3, player4) VALUES" +
		// "(?,?,?,?,?)";

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

			createGame.executeUpdate();
			
			
			//add cards to database
			int gameID=g.getId();
			//"INSERT INTO cards" + "(game_id, user_id, colour, number) VALUES" + "(?,?,?,?)";
			
			//voor alle users
			List<Card> cl;
			User u; 
			int userID;
			for(int i=0;i<g.MAX_USERS;i++) {
				
			u=players.get(i);
				
			if(u!=null) {
				
			cl= g.getHand(u);
			userID=u.getId();
			
			for(Card c: cl) {
				
				
			createCard.setInt(1,gameID);
			createCard.setInt(2,userID);
			createCard.setInt(3,c.getColourValue());
			createCard.setInt(4,c.getNumber());
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

	public Game readGame(int gameID) {

		return null;
	}

	public void deleteGame() {

	}

}
