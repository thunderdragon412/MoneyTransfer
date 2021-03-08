package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfers;

@RestController
@PreAuthorize("isAuthenticated()")
public class ControllerTransfer {
	@Autowired
	private TransferDAO transfersDAO;
	// private Transfers transfer;

	// public ControllerTransfer(TransferDAO transfersDAO, Transfers transfer) {
	// this.transfersDAO =transfersDAO;
	// this.transfer = transfer;

	@RequestMapping(path = "account/transfers/{id}", method = RequestMethod.GET)
	public List<Transfers> getAllMyTransfers(@PathVariable int id) {
		List<Transfers> output = transfersDAO.getAllTransfers(id);
		return output;
	}

	@RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
	public Transfers getSelectedTransfer(@PathVariable int id) {
		Transfers transfer = transfersDAO.getTransferById(id);
		return transfer;
	}

	@RequestMapping(path = "transfer", method = RequestMethod.POST)
	public String sendTransferRequest(@RequestBody Transfers transfer) {
		String results = transfersDAO.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(),
				transfer.getAmount());
		return results;
	}

	@RequestMapping(path = "request", method = RequestMethod.POST)
	public String requestTransferRequest(@RequestBody Transfers transfer) {
		String results = transfersDAO.requestTransfer(transfer.getAccountFrom(), transfer.getAccountTo(),
				transfer.getAmount());
		return results;
	}

	@RequestMapping(path = "request/{id}", method = RequestMethod.GET)
	public List<Transfers> getAllTransferRequests(@PathVariable int id) {
		List<Transfers> output = transfersDAO.getPendingRequests(id);
		return output;
	}

	@RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
	public String updateRequest(@RequestBody Transfers transfer, @PathVariable int statusId) {
		String output = transfersDAO.updateTransferRequest(transfer, statusId);
		return output;
	}

}
