package com.techelevator.tenmo.models;

public class AuthenticatedUser {
	
	private String token;
	private User user;
	private int balance;
	
	public int getBalance() {
		return balance;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
