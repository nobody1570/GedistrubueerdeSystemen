import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 




public class Database {
	
	
Connection con;

	
	Database(){
		try {
			Class.forName("org.sqlite.JDBC");
		
		con=DriverManager.getConnection("jdbc:sqlite:database.db");
		 if(con!=null){
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
	void createUser() {
		
		
	}
	
	User readUser() {
		
		
		return null;
	}
	
	void updateUser() {
		
	
	}
	
	void deleteUser() {
		
	}
	
	void saveGame() {
		
	}
	
	Game readGame() {
		
		return null;
	}
	
	void deleteGame() {
		
	}
	
}
