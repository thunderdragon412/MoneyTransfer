package com.techelevator.tenmo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

@Component
public class TransferSqlDAO implements TransferDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AccountDAO accountDAO;

	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

	}

	@Override
	public List<Transfers> getAllTransfers(int userId) {
		List<Transfers> transfers = new ArrayList<>();
		String sql = "SELECT t.*, u.username AS userFrom, v.username AS userTo FROM transfers t "
				+ "JOIN accounts a ON t.account_from = a.account_id "
				+ "JOIN accounts b ON t.account_to = b.account_id " + "JOIN users u ON a.user_id = u.user_id "
				+ "JOIN users v ON b.user_id = v.user_id " + "WHERE a.user_id = ? OR b.user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
		while (results.next()) {

			Transfers transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;

	}

	@Override
	public Transfers getTransferById(int transferId) {
		Transfers transfer = new Transfers();
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);

		if (result.next()) {
			transfer = mapRowToTransfer(result);
		}
		return transfer;
	}

	@Override
	public List<Transfers> getPendingRequests(int userId) {
		List<Transfers> output = new ArrayList<>();
		String sql = "SELECT t.*, u.username AS userFrom, v.username AS userTo FROM transfers t "
				+ "JOIN accounts a ON t.account_from = a.account_id "
				+ "JOIN accounts b ON t.account_to = b.account_id " + "JOIN users u ON a.user_id = u.user_id "
				+ "JOIN users v ON b.user_id = v.user_id "
				+ "WHERE transfer_status_id = 1 AND (account_from = ? OR account_to = ?)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
		while (results.next()) {
			Transfers transfer = mapRowToTransfer(results);
			output.add(transfer);
		}
		return output;

	}

	@Override
	public String updateTransferRequest(Transfers transfer, int statusId) {
		if (statusId == 3) {
			String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
			jdbcTemplate.update(sql, statusId, transfer.getTransferId());
			return "Update sucessful";
		}
		if (!(accountDAO.getBalance(transfer.getAccountFrom()).compareTo(transfer.getAmount()) == -1)) {
			String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
			jdbcTemplate.update(sql, statusId, transfer.getTransferId());
			accountDAO.addToBalance(transfer.getAmount(), transfer.getAccountTo());
			accountDAO.subtractFromBalance(transfer.getAmount(), transfer.getAccountFrom());
			return "Update sucessful";
		} else {
			return "No funds for transfer";
		}
	}

	@Override
	public String sendTransfer(int userFrom, int userTo, BigDecimal amount) {
		if (userFrom == userTo) {
			return "You can not send money to your self.";
		}
		if (amount.compareTo(accountDAO.getBalance(userFrom)) == -1 && amount.compareTo(new BigDecimal(0)) == 1) {
			String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
					+ "VALUES (2, 2, ?, ?, ?)";
			jdbcTemplate.update(sql, userFrom, userTo, amount);
			accountDAO.addToBalance(amount, userTo);
			accountDAO.subtractFromBalance(amount, userFrom);
			return "Transfer complete";
		} else {
			return "Transfer failed due to a lack of funds or amount was less then or equal to 0 or not a valid user";
		}
	}

	@Override
	public String requestTransfer(int userFrom, int userTo, BigDecimal amount) {
		if (userFrom == userTo) {
			return "You can not request Yourself.";
		}
		if (amount.compareTo(new BigDecimal(0)) == 1) {
			String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
					+ "VALUES (1, 1, ?, ?, ?)";
			jdbcTemplate.update(sql, userFrom, userTo, amount);
			return "Request sent";
		} else {
			return "There was a problem sending the request";
		}

	}

	private Transfers mapRowToTransfer(SqlRowSet results) {
		Transfers transfer = new Transfers();
		transfer.setTransferId(results.getInt("transfer_id"));
		transfer.setTransferTypeId(results.getInt("transfer_type_id"));
		transfer.setTransferStatusId(results.getInt("transfer_status_id"));
		transfer.setAccountFrom(results.getInt("account_From"));
		transfer.setAccountTo(results.getInt("account_to"));
		transfer.setAmount(results.getBigDecimal("amount"));
		try {
			transfer.setUserFrom(results.getString("userFrom"));
			transfer.setUserTo(results.getString("userTo"));
		} catch (Exception e) {
		}
		try {
			transfer.setTransferType(results.getString("transfer_type_desc"));
			transfer.setTransferStatus(results.getString("transfer_status_desc"));
		} catch (Exception e) {
		}
		return transfer;
	}
}
