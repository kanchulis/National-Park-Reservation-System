package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class TransferTypeSqlDAO implements TransferTypeDAO{
	
	private JdbcTemplate jdbcTemplate;
	
	public TransferTypeSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int getByID(int transferID) {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForObject("SELECT transfer_type_id FROM transfer_types WHERE transfer_id = ?", int.class, transferID);
	}

}
