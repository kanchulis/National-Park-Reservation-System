package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class TransferStatusSqlDAO implements TransferStatusDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public TransferStatusSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	

	@Override
	public int getByID(int transferID) {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForObject("SELECT transfer_status_id FROM transfer_statuses WHERE transfer_id = ?", int.class, transferID);
	}

}
