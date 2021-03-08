package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.Account;

@Component
public interface AccountDAO {
	// CRUD

	public BigDecimal getBalance(int userId);

	public BigDecimal addToBalance(BigDecimal amountToAdd, int id);

	public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id);

	public Account findUserById(int userId);

	public Account findAccountById(int id);

}
