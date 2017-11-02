import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	Connection con;
	PreparedStatement createUser;
	PreparedStatement updateUser;
	PreparedStatement deleteUser;
	PreparedStatement getUser;
	PreparedStatement getUserWithLogin;

	Database() {
		try {
			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection("jdbc:sqlite:database.db");

			if (con != null) {
				// prepaperedstatements hieronder
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

				System.out.println("ready");
			}

		} catch (Exception e) {
			System.out.println("no connection");
			e.printStackTrace();

		}

	}

	void closeConnection() {

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
	void createUser(User u) {
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

	User readUser(String login) {
		//SELECT id, login, salt_password, password, salt_token, token, timestamp FROM User WHERE login = ?
		
		System.out.println("reading user");
		User result = null;
		try {
			getUserWithLogin.setString(1, login);
			ResultSet rs = getUserWithLogin.executeQuery();

			if(rs.next())
			result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
					rs.getString(6), rs.getLong(7));
			
			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	User readUser(int i) {

		// SELECT id, login, salt_password, password, salt_token, token, timestamp FROM
		// User WHERE id = ?
		
		System.out.println("reading user");
		User result = null;
		try {
			getUser.setInt(1, i);
			ResultSet rs = getUser.executeQuery();

			if(rs.next())
			result = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
					rs.getString(6), rs.getLong(7));
			
			System.out.println("read user");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	void updateUser(User u) {
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

	void deleteUser(User u) {

		deleteUser(u.getId());

	}

	void deleteUser(int i) {

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

	void saveGame() {

	}

	Game readGame() {

		return null;
	}

	void deleteGame() {

	}

}