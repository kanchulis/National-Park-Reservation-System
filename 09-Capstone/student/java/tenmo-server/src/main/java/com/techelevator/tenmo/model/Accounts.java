package com.techelevator.tenmo.model;


public class Accounts {
private int userId;
private int accountId;
private Double balance;
private int userIdTo;

public int getUserIdTo() {
	return userIdTo;
}
public void setUserIdTo(int userIdTo) {
	this.userIdTo = userIdTo;
}
public int getUserId() {
	return userId;
}
public void setUserId(int userId) {
	this.userId = userId;
}
public int getAccountId() {
	return accountId;
}
public void setAccountId(int accountId) {
	this.accountId = accountId;
}
public Double getBalance() {
	return balance;
}
public void setBalance(Double balance) {
	this.balance = balance;
}
@Override
public String toString() {
	return "Accounts [userId=" + userId + ", accountId=" + accountId + ", balance=" + balance + "]";
}


}
