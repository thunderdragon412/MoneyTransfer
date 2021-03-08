package com.techelevator.tenmo.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.Account;

@Component
public class AccountSqlDAO implements AccountDAO {

	private JdbcTemplate jdbcTemplate;

	public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

	}

	@Override
	public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {
		Account account = findAccountById(id);
		BigDecimal newBalance = account.getBalance().add(amountToAdd);
		System.out.println(newBalance);
		String sqlString = "UPDATE accounts SET balance = ? WHERE user_id = ?";
		try {
			jdbcTemplate.update(sqlString, newBalance, id);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return account.getBalance();
	}

	@Override
	public BigDecimal getBalance(int userId) {
		String sqlString = "SELECT balance FROM accounts WHERE user_id = ?";
		SqlRowSet results = null;
		BigDecimal balance = null;
		try {
			results = jdbcTemplate.queryForRowSet(sqlString, userId);
			if (results.next()) {
				balance = results.getBigDecimal("balance");
			}
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return balance;
	}

	@Override
	public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
		Account account = findAccountById(id);
		BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
		String sqlString = "UPDATE accounts SET balance = ? WHERE user_id = ?";
		try {
			jdbcTemplate.update(sqlString, newBalance, id);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return account.getBalance();
	}

	@Override
	public Account findUserById(int userId) {
		String sqlString = "SELECT * FROM accounts WHERE user_id = ?";
		Account account = null;
		try {
			SqlRowSet result = jdbcTemplate.queryForRowSet(sqlString, userId);
			account = mapRowToUser(result);
		} catch (DataAccessException e) {
			System.out.println("Error accessing data");
		}
		return account;
	}

	@Override
	public Account findAccountById(int id) {
		Account account = null;
		String sql = "SELECT * FROM accounts WHERE account_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()) {
			account = mapRowToUser(results);
		}
		return account;
	}

	private Account mapRowToUser(SqlRowSet rs) {
		Account account = new Account();
		account.setAccountId(rs.getLong("account_id"));
		account.setUserId(rs.getInt("user_id"));
		account.setBalance(rs.getBigDecimal("balance"));
		return account;

	}

}
