import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
 




public class Database {
	
	
Connection con;
PreparedStatement createUser;
PreparedStatement updateUser;

	
	Database(){
		try {
			Class.forName("org.sqlite.JDBC");
		
		con=DriverManager.getConnection("jdbc:sqlite:database.db");
		
		
		
		
		 if(con!=null){
			 //prepaperedstatements hieronder
			 //voor nieuwe users aan te maken
			 String insertInUser = "INSERT INTO User"
						+ "(id, login, password) VALUES"
						+ "(?,?,?)";
			 createUser=con.prepareStatement(insertInUser);
			 
			 String updateUserString = "UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?, timestamp = ? WHERE id = ?";
			 updateUser = con.prepareStatement(updateUserString);
			 
             System.out.println("ready");
         }
		 
		 
		 
		} catch (Exception e) {
			System.out.println("no connection");
			e.printStackTrace();
		
		}
		
	}
	
	void closeConnection() {
		
		if(con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("connection closed");
		}
		
	}
	
	
	
	//https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	//preparedstatements in constructor maken en hier invullen en uitvoeren
	void createUser(User u) {
		//"INSERT INTO User(id, login, password) VALUES"(?,?,?)
		
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
		
		
		return null;
	}

	User readUser(User u) {
		
		
		return null;
	}
	
	void updateUser(User u) {
		//Timestamp ID NIET Geïmplementeerd!
		//UPDATE User SET salt_password = ?, password = ?, salt_token = ?, token = ?, timestamp = ?  WHERE id = ?
		System.out.println("updating user");
		try {
			
			updateUser.setString(1, u.getSalt_password());
			updateUser.setString(2, u.getPassword());
			updateUser.setString(3, u.getSalt_token());
			updateUser.setString(4, u.getToken());
			updateUser.setInt(5, 0);
			updateUser.setInt(6, u.getId());
			updateUser.executeUpdate();
			
			System.out.println("new user updated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void deleteUser(User u) {
		
	}
	
	void saveGame() {
		
	}
	
	Game readGame() {
		
		return null;
	}
	
	void deleteGame() {
		
	}
	
}
