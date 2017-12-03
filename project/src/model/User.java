/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.sql.Timestamp;
public class User implements Serializable{

	int id;
	int score;
	String login;
	String salt_password;
	String password;
	

	String salt_token;
	String token;
	Long timestamp;//ander type?
	
	
	
	public User(int id, String login, String salt_password, String password, String salt_token, String token,
			Long i, int score) {
		super();
		this.id = id;
		this.login = login;
		this.salt_password = salt_password;
		this.password = password;
		this.salt_token = salt_token;
		this.token = token;
		this.timestamp = i;
		this.score=score;
	}

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
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long time) {
		this.timestamp = time;
	}
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", salt_password=" + salt_password + ", password=" + password
				+ ", salt_token=" + salt_token + ", token=" + token + ", timestamp=" + timestamp + "]";
	}
	
}
