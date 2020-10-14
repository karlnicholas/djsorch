package com.github.karlnicholas.djsorch.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.djsorch.distributed.ServiceClients;
import com.github.karlnicholas.djsorch.model.Transaction;
import com.github.karlnicholas.djsorch.distributed.Grpcservices.WorkItemMessage;
import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.github.karlnicholas.djsorch.repository.TransactionClosedRepository;
import com.github.karlnicholas.djsorch.repository.TransactionOpenRepository;
import com.github.karlnicholas.djsorch.service.BusinessDateService;
import com.google.protobuf.ByteString;

@Component
public class BillingCycleHandler {
	private static final Logger logger = LoggerFactory.getLogger(BillingCycleHandler.class);
	private final ServiceClients serviceClients;
	private final BusinessDateService businessDateService;
	private final TransactionOpenRepository transactionOpenRepository;
	private final TransactionClosedRepository transactionClosedRepository;

	public BillingCycleHandler(
		ServiceClients serviceClients, 
		BusinessDateService businessDateService, 
		TransactionOpenRepository transactionOpenRepository, 
		TransactionClosedRepository transactionClosedRepository
	) {
		this.serviceClients = serviceClients;
		this.businessDateService = businessDateService;
		this.transactionOpenRepository = transactionOpenRepository; 
		this.transactionClosedRepository = transactionClosedRepository;
	}
	@PostConstruct
	public void init() {
		
	}
	
	public void fetchLatestBillingCycleForAccount(QueueEntry queueEntry) {
		Long accountId = new Long(queueEntry.getAccountId());
		Transaction latestBillingCycle = transactionOpenRepository.fetchLatestBillingCycleForAccount(accountId);
		if ( latestBillingCycle == null ) {
			latestBillingCycle = transactionClosedRepository.fetchLatestBillingCycleForAccount(accountId);
		};
		queueEntry.getDeferredResult().setResult(ResponseEntity.ok(latestBillingCycle));
	}

	public void handleBillingCycle(QueueEntry queueEntry, Consumer<Long> completePostedTransaction) {
		logger.info("handleBillingCycle");

		Map<String, ByteString> params = new HashMap<>();
		Map<String, ByteString> results = new HashMap<>();
		params.put("subject", ByteString.copyFromUtf8(queueEntry.getAccountId().toString()));
		params.put("billingdate", ByteString.copyFromUtf8(businessDateService.getBusinessDate().toString()));

		WorkItemMessage wim = serviceClients.accountDueDate(WorkItemMessage.newBuilder().putAllParams(params).putAllResults(results).build());
		params.putAll(wim.getParamsMap());
		results.putAll(wim.getResultsMap());
		serviceClients.accountInterest(wim.toBuilder().putAllParams(params).putAllResults(results).build());
		params.putAll(wim.getParamsMap());
		results.putAll(wim.getResultsMap());
		serviceClients.accountBillingCycle(wim.toBuilder().putAllParams(params).putAllResults(results).build());
		params.putAll(wim.getParamsMap());
		results.putAll(wim.getResultsMap());
		serviceClients.accountClosing(wim.toBuilder().putAllParams(params).putAllResults(results).build());
		params.putAll(wim.getParamsMap());
		results.putAll(wim.getResultsMap());
		
		completePostedTransaction.accept(queueEntry.getQueueId());

	}
		
}
