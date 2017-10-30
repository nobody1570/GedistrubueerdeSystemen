import java.sql.Time;
public class User {

	int id;
	String login;
	String salt_password;
	String password;
	String salt_token;
	String token;
	Time timestamp;
	
	
	
	//time= time in milliseconds
	public User(int id, String login, String salt_password, String password) {
		super();
		this.id = id;
		this.login = login;
		this.salt_password = salt_password;
		this.password = password;
		this.salt_token = null;
		this.token = null;
		this.timestamp = null;
	}
	
	//getters en setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSalt_password() {
		return salt_password;
	}
	public void setSalt_password(String salt_password) {
		this.salt_password = salt_password;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt_token() {
		return salt_token;
	}
	public void setSalt_token(String salt_token) {
		this.salt_token = salt_token;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Time getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Time timestamp) {
		this.timestamp = timestamp;
	}
	public void setTimestamp(Long time) {
		this.timestamp = new Time(time);
	}
	
}
