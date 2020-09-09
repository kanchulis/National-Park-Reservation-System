package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {
	
	public void sendTransfer (Transfer transfer);
	
	public void requestTransfer(Transfer transfer);

	// "A transfer includes the User IDs of the FROM annd TO users and the amount of TE Bucks
	public List<Transfer> listAll();
	
	// See transfers sent or received for a specific account ID 
	public List <Transfer> getTransferHisotry (int accountId);
	
	// Updates the status of a specific transfer (a sending transfer has an initial status of "approve")
	public void updateStatus(Transfer theTransfer);
	
	// Retrieves details of any transfer based on the transferId
	public List<Transfer> getTransferById (int transferId);

	public Transfer reviewRequest(Transfer transfer);

	public List<Transfer> getPendingTransfers(int accountNumber);
	
	
}
