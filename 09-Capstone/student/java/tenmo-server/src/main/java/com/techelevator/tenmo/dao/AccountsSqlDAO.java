package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Accounts;


@Component
public class AccountsSqlDAO implements AccountsDAO{
	private JdbcTemplate jdbcTemplate;
	
	public AccountsSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	//Getting balance for a specific user ID 
	@Override
	public Double getBalance(int userId) {
		Double balance = 0.00;
		String sqlQuery = "Select balance from accounts where user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, userId);
		while (results.next()) {
			
			balance = results.getDouble("balance");
		}
	return balance;
		
	}

	//Getting all account info for a specific user ID 
	@Override
	public Accounts getAccount(int userId) {
		Accounts userAccount = null;
		String sqlQuery = "select * from accounts where user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, userId);
		if(results.next()) {
			userAccount = mapRowToAccounts(results);
		}
		return userAccount;
	}

	//Adding money and updating balance for a specific user ID
	@Override
	public double deposit(int userID, double amount) {
		double oldBalance = getAccount(userID).getBalance();
		double newBalance = oldBalance += amount;
		String updateBalance = "update accounts set balance = ? where user_id = ?";
        jdbcTemplate.update(updateBalance, newBalance, userID);
       
        return newBalance;
	}
	
	
	//Withdrawing money and updating balance for a specific user ID
	@Override
	public double withdraw(int userID, double amount) {
		double oldBalance = getAccount(userID).getBalance();
		double newBalance = 0.00;
		if (oldBalance >= amount) {
			 newBalance = oldBalance -= amount;
		}
		else{
			System.out.println("Not enough money");
		}
		
		String updateBalance = "update accounts set balance = ? where user_id = ?";
        jdbcTemplate.update(updateBalance, newBalance, userID);
        
        return newBalance;
	}



	private Accounts mapRowToAccounts(SqlRowSet rs) {
		Accounts account = new Accounts();
		account.setAccountId(rs.getInt("account_id"));
		account.setUserId(rs.getInt("user_id"));
		account.setBalance(rs.getDouble("balance"));
		return account;
	}
	
	
}
