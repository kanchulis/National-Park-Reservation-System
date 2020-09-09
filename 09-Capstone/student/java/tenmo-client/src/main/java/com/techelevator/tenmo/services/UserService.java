package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.models.Accounts;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class UserService {

	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	public static String AUTH_TOKEN = "";

	public UserService(String url) {

		this.BASE_URL = url;
	}


	public User[] findAll(String userToken) {
		AUTH_TOKEN = userToken;
		User[] user = null;
		user = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
		return user;
	}
	

	public Accounts getAccount(String userToken, int userId) {
		AUTH_TOKEN = userToken;
		Accounts account = null;
		account = restTemplate.exchange(BASE_URL + "users/" + userId, HttpMethod.GET, makeAuthEntity(), Accounts.class)
				.getBody();

		return account;
	}
	
	  public User getUserWithAccount(String userToken, int accountNumber) {
	    	AUTH_TOKEN = userToken;
	    	User user = null;

	        	user = restTemplate.exchange(BASE_URL + accountNumber, HttpMethod.GET, makeAuthEntity(), User.class).getBody();

	        return user;
	    }
	
	public Transfer[] getPendingTransfers(String userToken, int accountId) {
    	AUTH_TOKEN = userToken;
    	
    	Transfer [] pendingTransfers = null;

        	pendingTransfers = restTemplate.exchange(BASE_URL + "pending/" + accountId, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
	
        return pendingTransfers;
    }
	
	
	public Double viewBalance(int userId) throws RestClientException {
		 //acc = null;
		
		Double ret = restTemplate
				.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(), Double.class).getBody();
		

		return ret;
		
	}

	public double withdraw(String userToken, int userId, double amount ) {
		AUTH_TOKEN = userToken;
		double newBalance = 0.00;
		
		newBalance = restTemplate.exchange(BASE_URL + "users/" + userId + "/withdraw/" + amount, HttpMethod.GET,
				makeAuthEntity(), double.class).getBody();
		return newBalance;
	}

	
	public double deposit(String userToken, int moneyTo, double amount) {
		AUTH_TOKEN = userToken;
		double newBalance = 0;
		newBalance = restTemplate.exchange(BASE_URL + "users/" + moneyTo + "/deposit/" + amount, HttpMethod.GET, makeAuthEntity(), double.class).getBody();

		return newBalance;
	}

	public void sendTransfer(String userToken, int accountFrom, int accountTo, double amount) {
		AUTH_TOKEN = userToken;
		Transfer transfer = makeTransfer(accountFrom, accountTo, amount);
		restTemplate.exchange(BASE_URL + "transfers/send", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class).getBody();
		
	}

	public void requestTransfer(String userToken, int accountFrom, int accountTo, double amount) {
		AUTH_TOKEN = userToken;
		Transfer transfer = makeTransfer(accountFrom, accountTo, amount);
		restTemplate.exchange(BASE_URL + "transfers/request", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class).getBody();
		
		
	}
	
	
	public Transfer[] getTransferHisotry(String userToken, int accountId) {
		AUTH_TOKEN = userToken;
		Transfer[] allTransfers = null;
		 allTransfers = restTemplate.exchange(BASE_URL + "transfers/" + accountId, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();

		return allTransfers;
	}

	public void updateStatus(String userToken, Transfer transferId) {
		AUTH_TOKEN = userToken;
		restTemplate.exchange(BASE_URL + "transfers/update", HttpMethod.POST, makeTransferEntity(transferId),
				Transfer.class).getBody();

	}

	public Transfer[] getTransferById(String userToken, int transferId) {
		AUTH_TOKEN = userToken;
		Transfer[] transfer = null;
		transfer = restTemplate.exchange(BASE_URL + "transfers/getById/" + transferId, HttpMethod.GET, makeAuthEntity(),
				Transfer[].class).getBody();
		return transfer;
	}
	
	public Transfer getTransferByIdSingle(String userToken, int transferID) {
    	AUTH_TOKEN = userToken;
    	Transfer aTransfer = null;
    		aTransfer = restTemplate.exchange(BASE_URL + "getById/" + transferID, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
    return aTransfer;
    }

	private HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity makeTransferEntity(Transfer transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(AUTH_TOKEN);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}
	
	
	public void updateTransferStatus(String userToken, Transfer theTransfer) {
    	AUTH_TOKEN = userToken;
        	restTemplate.exchange(BASE_URL + "update_status", 
        			                         HttpMethod.POST, makeTransferEntity(theTransfer), 
        			                         Transfer.class).getBody();

    }
	
	private Transfer makeTransfer(int accountFrom, int accountTo, double amount) {
		Transfer transfer = new Transfer();
		transfer.setTransferTypeId(2);
		transfer.setTransferStatusId(2);
		transfer.setAccountFrom(accountFrom);
		transfer.setAccountTo(accountTo);
		transfer.setAmount(amount);
		return transfer;
	}
	
	
	



}
