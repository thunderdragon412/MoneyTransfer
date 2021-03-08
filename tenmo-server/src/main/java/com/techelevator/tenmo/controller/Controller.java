package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.User;

@PreAuthorize("isAuthenticated()")
@RestController
public class Controller {
	private UserDAO userDAO;
	private AccountDAO accountDAO;
	private TransferDAO transferDAO;

	public Controller(AccountDAO accountDAO, TransferDAO transferDAO, UserDAO userDAO) {
		this.userDAO = userDAO;
		this.accountDAO = accountDAO;
		this.transferDAO = transferDAO;
	}

	@RequestMapping(path = "users", method = RequestMethod.GET)
	public List<User> list() {
		List<User> users = userDAO.findAll();
		return users;
	}

	@RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)
	public BigDecimal getbalance(@PathVariable int id) {
		BigDecimal balance = accountDAO.getBalance(id);
		return balance;
	}
}
