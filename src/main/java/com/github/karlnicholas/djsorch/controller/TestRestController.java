package com.github.karlnicholas.djsorch.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.processor.CalculatorGrpc;
import com.github.karlnicholas.djsorch.processor.CalculatorGrpc.CalculatorBlockingStub;
import com.github.karlnicholas.djsorch.processor.CalculatorOuterClass;
import com.github.karlnicholas.djsorch.distributed.TransactionProcessorGrpc;
import com.github.karlnicholas.djsorch.distributed.TransactionProcessorGrpc.TransactionProcessorBlockingStub;
import com.github.karlnicholas.djsorch.distributed.Workitem.WorkItemMessage;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@RestController
@RequestMapping("test")
public class TestRestController {

/*
    public void shutdownChannels() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdownNow);
        Optional.ofNullable(inProcChannel).ifPresent(ManagedChannel::shutdownNow);
    }
*/

	private CalculatorBlockingStub blockingStub;
	private TransactionProcessorBlockingStub transactionProcessorBlockingStub;
	
	@PostConstruct
	public void init() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", 6565);
        channelBuilder.usePlaintext();
        ManagedChannel channel =  channelBuilder.build();
        blockingStub = CalculatorGrpc.newBlockingStub(channel);
        transactionProcessorBlockingStub = TransactionProcessorGrpc.newBlockingStub(channel);
	}
	@GetMapping("calc")
	public String testCalc() throws InterruptedException, ExecutionException {

		double dr = blockingStub.calculate(CalculatorOuterClass.CalculatorRequest.newBuilder().setNumber1(1).setNumber2(1).build())
				.getResult();
		
        
		return Double.toString(dr);
		
	}
	@GetMapping("work")
	public String testWorkitem() throws InterruptedException, ExecutionException {

		WorkItemMessage wim = WorkItemMessage.newBuilder()
				.putParams("action", ByteString.copyFromUtf8("action"))
				.putParams("subject", ByteString.copyFromUtf8("subject"))
				.build();
		WorkItemMessage wimn = transactionProcessorBlockingStub.validateAndProcess(wim);
		
        
		return wimn.toString();
		
	}
}
