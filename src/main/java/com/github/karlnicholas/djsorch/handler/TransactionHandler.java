package com.github.karlnicholas.djsorch.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.djsorch.distributed.ServiceClients;
import com.github.karlnicholas.djsorch.distributed.Grpcservices.WorkItemMessage;
import com.github.karlnicholas.djsorch.model.TransactionType;
import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.google.protobuf.ByteString;

@Component
public class TransactionHandler {
	private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);
	private final ServiceClients serviceClients;
	public TransactionHandler(ServiceClients serviceClients) {
		this.serviceClients = serviceClients;
	}
	@PostConstruct
	public void init() {
		
	}
	public void handleTransaction(QueueEntry queueEntry, Consumer<Long> completePostedTransaction) {
		logger.info("handleTransaction");

		Map<String, ByteString> params = new HashMap<>();
		Map<String, ByteString> results = new HashMap<>();
		params.put("subject", ByteString.copyFromUtf8(queueEntry.getTransactionId()));

		WorkItemMessage wim = serviceClients.validateAndProcessTransaction(WorkItemMessage.newBuilder().putAllParams(params).putAllResults(results).build());
		results.putAll(wim.getResultsMap());
		params.putAll(wim.getParamsMap());

		Boolean validated = Boolean.valueOf( results.get("validated").toStringUtf8());
		if ( validated.booleanValue() ) {
			if ( TransactionType.valueOf(results.get("transactionType").toStringUtf8()) == TransactionType.LOAN_FUNDING ) {
				wim =  serviceClients.accountFunded(wim.toBuilder().putAllParams(params).putAllResults(results).build());
				params.putAll(wim.getParamsMap());
				results.putAll(wim.getResultsMap());
				params.put("subject", ByteString.copyFromUtf8(queueEntry.getAccountId()));
				serviceClients.initialBillingCycle(wim.toBuilder().putAllParams(params).putAllResults(results).build());
			};
		}
		completePostedTransaction.accept(queueEntry.getQueueId());

	}
		
}
