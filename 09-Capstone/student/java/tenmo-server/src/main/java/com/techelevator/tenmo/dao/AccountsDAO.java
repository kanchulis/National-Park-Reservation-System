package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;

public interface AccountsDAO {

	Double getBalance(int accountId);
	
	public Accounts getAccount(int userId); 
	
	public double deposit(int userID, double amount);

	public double withdraw(int userID, double amount);

}
