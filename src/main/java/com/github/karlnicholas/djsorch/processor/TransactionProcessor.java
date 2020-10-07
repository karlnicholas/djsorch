package com.github.karlnicholas.djsorch.processor;

import org.lognet.springboot.grpc.GRpcService;

import com.github.karlnicholas.djsorch.processor.Workitem.WorkItemMessage;

import io.grpc.stub.StreamObserver;

@GRpcService
public class TransactionProcessor extends TransactionProcessorGrpc.TransactionProcessorImplBase {
	@Override
	public void validateAndProcess(WorkItemMessage request, StreamObserver<WorkItemMessage> responseObserver) {
		responseObserver.onNext(request.toBuilder()
				.putResults("action", request.getParamsOrThrow("action"))
				.putResults("subject", request.getParamsOrThrow("subject"))
				.build());
        responseObserver.onCompleted();
	}

}