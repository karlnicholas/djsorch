package com.github.karlnicholas.djsorch.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.journal.BillingCyclePosting;
import com.github.karlnicholas.djsorch.model.TransactionOpen;
import com.github.karlnicholas.djsorch.repository.TransactionOpenRepository;
import com.github.karlnicholas.djsorch.service.PostingReader;
import com.github.karlnicholas.djsorch.service.QueueService;

@RestController
@RequestMapping("billingdate")
public class BillingDateController {
	private final TransactionOpenRepository transactionOpenRepository;
	private final QueueService queueService;
	private final PostingReader postingReader;

	public BillingDateController(
		TransactionOpenRepository transactionOpenRepository,
		QueueService queueService,
		PostingReader postingReader
	) {
		this.transactionOpenRepository = transactionOpenRepository;
		this.queueService = queueService;
		this.postingReader = postingReader;
	}
	@GetMapping("/{billingdate}")
	public ResponseEntity<List<Long>> billingDate( @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billingdate) throws Exception {
		List<TransactionOpen> billingCycles = transactionOpenRepository.fetchLatestBillingCycles();
		List<Long> result = billingCycles.stream()
			.filter(transaction->postingReader.readValue(transaction, BillingCyclePosting.class).getPeriodEndDate().compareTo(billingdate)==0)
			.map(transaction->{
				queueService.queueNewTransactionPost(transaction.getAccountId(), transaction.getId(), "billingcycle");
				return transaction.getAccountId();
			})
			.collect(Collectors.toList());
		return ResponseEntity.ok(result); 
	}
}
