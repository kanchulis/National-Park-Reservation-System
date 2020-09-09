package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;

@Component
public class TransferSqlDAO implements TransferDAO {

	private JdbcTemplate jdbcTemplate;

	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override

	public void sendTransfer(Transfer transfer) {
		String sqlQuery = "INSERT into transfers (transfer_type_id,transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
		jdbcTemplate.update(sqlQuery, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

	}
	
	@Override

	public void requestTransfer(Transfer transfer) {
		String sqlQuery = "INSERT into transfers (transfer_type_id,transfer_status_id, account_from, account_to, amount) VALUES (1, 1, ?, ?, ?)";
		jdbcTemplate.update(sqlQuery, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

	}
	
	
	@Override
	public Transfer reviewRequest(Transfer transfer) {
		Transfer request = null;
		String sql = "Select * from transfers where transfer_Type_id= 1";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if(results.next()) {
			 request = mapRowToTransfer(results);
		}
		return request;
		
	}

	// Lists all transfer table
	@Override
	public List<Transfer> listAll() {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * from transfers";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}

		return transfers;
	}

	// This gets all data from transfer table for a specific account (either from or
	// to)
	@Override
	public List<Transfer> getTransferHisotry(int accountId) {
		List<Transfer> transferHistory = new ArrayList<>();
		String sql = "SELECT * FROM transfers WHERE account_from = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql,accountId);
		while (results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transferHistory.add(transfer);
		}
		return transferHistory;
	}

	// updates the transfer status ID (to either 1, 2, or 3) for a specific transfer ID
	@Override
	public void updateStatus(Transfer transferId) {
		String sqlQuery = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		jdbcTemplate.update(sqlQuery);

	}

	// This gets all info from transfer table for a specifc transfer ID"
	@Override
	public List<Transfer> getTransferById(int transferId) {
		List <Transfer> getTransferById = new ArrayList<>();
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
		while(results.next()) {
			Transfer placeHolder = mapRowToTransfer(results);
			getTransferById.add(placeHolder);
		}
		return getTransferById;
	}

	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer transfer = new Transfer();
		transfer.setTransferId(rs.getInt("transfer_id"));
		transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
		transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
		transfer.setAccountFrom(rs.getInt("account_from"));
		transfer.setAccountTo(rs.getInt("account_to"));
		transfer.setAmount(rs.getDouble("amount"));

		return transfer;

	}

	@Override
	public List<Transfer> getPendingTransfers(int accountId) {
		List<Transfer> transferHistroy = new ArrayList<>();
		String sql = "select * " + 
					 "from transfers " + 
				     "where account_from = ? " + 
				     "and transfer_status_id = 1 " ;
				
		 SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
	        while(results.next()) {
	            Transfer transfer = mapRowToTransfer(results);
	            transferHistroy.add(transfer);
	        }

	        return transferHistroy;
	}


}
