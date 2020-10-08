package com.github.karlnicholas.djsorch.distributed;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.github.karlnicholas.djsorch.distributed.TransactionProcessorGrpc.TransactionProcessorBlockingStub;
import com.github.karlnicholas.djsorch.distributed.Workitem.WorkItemMessage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Component
public class ServiceClients {
	private TransactionProcessorBlockingStub transactionProcessorBlockingStub;
	
	@PostConstruct
	public void init() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", 6565);
        channelBuilder.usePlaintext();
        ManagedChannel channel =  channelBuilder.build();
        transactionProcessorBlockingStub = TransactionProcessorGrpc.newBlockingStub(channel);
	}


    public WorkItemMessage validateAndProcessTransaction(WorkItemMessage wim) {
		return transactionProcessorBlockingStub.validateAndProcess(wim);
    }
    
    public WorkItemMessage accountFunded(WorkItemMessage wim) {
		return transactionProcessorBlockingStub.accountFunded(wim);
    }

    public WorkItemMessage initialBillingCycle(WorkItemMessage wim) {
		return transactionProcessorBlockingStub.initialBillingCycle(wim);
    }
}
