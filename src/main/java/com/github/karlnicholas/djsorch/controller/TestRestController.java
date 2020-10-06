package com.github.karlnicholas.djsorch.controller;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.processor.CalculatorGrpc;
import com.github.karlnicholas.djsorch.processor.CalculatorGrpc.CalculatorBlockingStub;
import com.github.karlnicholas.djsorch.processor.CalculatorOuterClass;

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
	
	@PostConstruct
	public void init() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", 6565);
        channelBuilder.usePlaintext();
        ManagedChannel channel =  channelBuilder.build();
        blockingStub = CalculatorGrpc.newBlockingStub(channel);
	}
	@GetMapping
	public String testRest() throws InterruptedException, ExecutionException {

		double dr = blockingStub.calculate(CalculatorOuterClass.CalculatorRequest.newBuilder().setNumber1(1).setNumber2(1).build())
				.getResult();
		
        
		return Double.toString(dr);
		
	}
}
