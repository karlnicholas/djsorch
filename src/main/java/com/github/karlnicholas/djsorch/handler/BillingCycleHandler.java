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
import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.google.protobuf.ByteString;

@Component
public class BillingCycleHandler {
	private static final Logger logger = LoggerFactory.getLogger(BillingCycleHandler.class);
	private final ServiceClients serviceClients;
	public BillingCycleHandler(ServiceClients serviceClients) {
		this.serviceClients = serviceClients;
	}
	@PostConstruct
	public void init() {
		
	}
	public void handleBillingCycle(QueueEntry queueEntry, Consumer<Long> completePostedTransaction) {
		logger.info("handleBillingCycle");

		Map<String, ByteString> params = new HashMap<>();
		Map<String, ByteString> results = new HashMap<>();
		params.put("subject", ByteString.copyFromUtf8(queueEntry.getTransactionId()));
//		params.put("billingDate", ByteString.copyFromUtf8(queueEntry.));

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
