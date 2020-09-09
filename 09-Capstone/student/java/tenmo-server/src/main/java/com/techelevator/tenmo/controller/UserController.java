package com.techelevator.tenmo.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.techelevator.tenmo.dao.AccountsDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.TransferStatusDAO;
import com.techelevator.tenmo.dao.TransferTypeDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

	// instance of each DAO class
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private AccountsDAO accountsDAO;
	@Autowired
	private TransferStatusDAO transferStatusDAO;
	@Autowired
	private TransferTypeDAO transferTypeDAO;
	@Autowired
	private TransferDAO transferDAO;

	@RequestMapping(path = "/test", method = RequestMethod.GET)
	public void test() {
		System.out.println("it works");
	}

	// Lists all users in system
	//from userDAO
	@PreAuthorize("permitAll")
	@RequestMapping(path = "users", method = RequestMethod.GET)
	public List<User> findAll() {
		return userDAO.findAll();
	}
	
	@RequestMapping(path = "/users/{accountNumber}", method = RequestMethod.GET)
    public User getUserWithAccount(@PathVariable int accountNumber) {
        return userDAO.getUserWithAccount(accountNumber);
    }

	
	//Getting all account info for a specific user ID
	//from accountsDAO
	@PreAuthorize("permitAll")
	@RequestMapping(path = "users/{userId}", method = RequestMethod.GET)
	public Accounts getAccount(@PathVariable int userId) {
		Accounts allAccounts = accountsDAO.getAccount(userId);
		return allAccounts;
	}

	//from accountsDAO
	// returns balance of specific userId
	@RequestMapping(path = "accounts/{userId}", method = RequestMethod.GET)
	public Double currentBalance(@PathVariable int userId) {
		return accountsDAO.getBalance(userId);
	}

	//from accountsDAO
	@RequestMapping(path = "users/{userId}/withdraw/{amount}", method = RequestMethod.GET)
	public double withdraw(@PathVariable int userId, @PathVariable double amount) {
		return accountsDAO.withdraw(userId, amount);
	}

	//from accountsDAO
	@RequestMapping(path = "users/{userId}/deposit/{amount}", method = RequestMethod.GET)
	public double deposit(@PathVariable int userId, @PathVariable double amount) {
		return accountsDAO.deposit(userId, amount);
	}

	//from transferDAO
	
	@RequestMapping(path = "transfers/send", method = RequestMethod.POST)
	public void createTransfer(@Valid @RequestBody Transfer transfer) {
		transferDAO.sendTransfer(transfer);
		// if (transfer.getTransferStatusId() == 2) {
		accountsDAO.withdraw(transfer.getAccountFrom(), transfer.getAmount());
		// }
		accountsDAO.deposit(transfer.getAccountTo(), transfer.getAmount());
	}
	
	//from transferDAO
		@PreAuthorize("permitAll")
		@RequestMapping(path = "transfers/request", method = RequestMethod.POST)
		public void requestTransfer(@Valid @RequestBody Transfer transfer) {
			transferDAO.requestTransfer(transfer);
		}

	//from transferDAO
	
	@RequestMapping(path = "transfers/{accountId}", method = RequestMethod.GET)
	public List<Transfer> getTransferHisotry(@PathVariable int accountId) {
		return transferDAO.getTransferHisotry(accountId);
	}

	//from transferDAO
	@RequestMapping(path = "transfers/update", method = RequestMethod.POST)
	public void updateStatus(@RequestBody Transfer transferId) {
		transferDAO.updateStatus(transferId);
	}
	
	//from transferDAO
	@RequestMapping (path = "transfers/getById/{transferId}", method = RequestMethod.GET)
	public List<Transfer> getTransferById(@PathVariable int transferId) {
		return transferDAO.getTransferById(transferId);
	}
	
	 @RequestMapping(path="/transfers/pending/{accountNumber}", method = RequestMethod.GET)		
	    public List <Transfer> getPendingTransfers(@PathVariable int accountNumber) {
	    	return  transferDAO.getPendingTransfers(accountNumber);
	    }
	
	
}

