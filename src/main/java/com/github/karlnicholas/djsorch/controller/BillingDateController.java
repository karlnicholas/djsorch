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
import com.github.karlnicholas.djsorch.service.BusinessDateService;
import com.github.karlnicholas.djsorch.service.PostingReader;
import com.github.karlnicholas.djsorch.service.QueueService;

@RestController
@RequestMapping("billingcycle")
public class BillingDateController {
	private final TransactionOpenRepository transactionOpenRepository;
	private final QueueService queueService;
	private final PostingReader postingReader;
	private final BusinessDateService businessDateService;

	public BillingDateController(
		TransactionOpenRepository transactionOpenRepository,
		QueueService queueService,
		PostingReader postingReader, 
		BusinessDateService businessDateService
	) {
		this.transactionOpenRepository = transactionOpenRepository;
		this.queueService = queueService;
		this.postingReader = postingReader;
		this.businessDateService = businessDateService;
	}
	@GetMapping
	public ResponseEntity<List<Long>> billingDate() throws Exception {
		List<TransactionOpen> billingCycles = transactionOpenRepository.fetchLatestBillingCycles();
		LocalDate billingDate = businessDateService.getBusinessDate();
		List<Long> result = billingCycles.stream()
			.filter(transaction->postingReader.readValue(transaction, BillingCyclePosting.class).getPeriodEndDate().compareTo(billingDate)==0)
			.map(transaction->{
				queueService.queueNewTransactionPost(transaction.getAccountId(), transaction.getId(), "billingcycle");
				return transaction.getAccountId();
			})
			.collect(Collectors.toList());
		return ResponseEntity.ok(result); 
	}
}
