package com.github.karlnicholas.djsorch.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.djsorch.model.TransactionType;
import com.github.karlnicholas.djsorch.queue.QueueEntry;

@Component
public class TransactionProcessorJava {
	private static final Logger logger = LoggerFactory.getLogger(TransactionProcessorJava.class);
	public void handleTransaction(QueueEntry queueEntry, Consumer<Long> completePostedTransaction) {
	}
/*
	public void handleTransaction(QueueEntry queueEntry, Consumer<Long> completePostedTransaction) {
		logger.info("handleTransaction");

		WorkItemMessage wim = userviceClients.validateAndProcessTransaction(buildWim(queueEntry));
		Boolean validated = Boolean.valueOf( wim.getResults().getOrDefault("validated", Boolean.FALSE).toString());
		if ( !validated.booleanValue() ) {
			if ( TransactionType.valueOf(wim.getResults().getOrDefault("transactionType", "UNKNOWN").toString()) == TransactionType.LOAN_FUNDING ) {
					wim.getParams().put("Subject", wim.getResults().get("transactionId"));
					WorkItemMessage wimn =  userviceClients.accountFunded(wim);
					wimn.getParams().put("Subject", queueEntry.getAccountId());
					userviceClients.initialBillingCycle(wimn);
			};
		}
	}
*/
	private WorkItemMessage buildWim(QueueEntry queueEntry) {
		WorkItemMessage wim = new WorkItemMessage();
		Map<String, Object> params = new HashMap<>();
		params.put("Subject", queueEntry.getTransactionId());
		Map<String, Object> results = new HashMap<>();
		wim.setParams(params);
		wim.setResults(results);
		return wim;
	}
		
}
