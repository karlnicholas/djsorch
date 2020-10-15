package com.github.karlnicholas.djsorch.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.github.karlnicholas.djsorch.service.AccountClosedService;
import com.github.karlnicholas.djsorch.service.AccountClosedSummary;

@Component
public class AccountHandler {
	private static final Logger logger = LoggerFactory.getLogger(AccountHandler.class);
	private final AccountClosedService accountClosedService;

	public AccountHandler(
		AccountClosedService accountClosedService
	) {
		this.accountClosedService = accountClosedService;
	}

	
	public void accountClosedSummary(QueueEntry queueEntry) {
		AccountClosedSummary accountClosedSummary = accountClosedService.getAccountClosedSummary(new Long(queueEntry.getAccountId()));
		queueEntry.getDeferredResult().setResult(ResponseEntity.ok(accountClosedSummary));
	}

}
