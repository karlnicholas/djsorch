package com.github.karlnicholas.djsorch.config;

import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.karlnicholas.djsorch.processor.CalculatorGrpc;
import com.github.karlnicholas.djsorch.processor.CalculatorOuterClass;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.stub.StreamObserver;

@Configuration
public class CalcConfig {
    @Bean
    @GRpcGlobalInterceptor
    public  ServerInterceptor globalInterceptor(){
        return new ServerInterceptor(){
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
               // your logic here
                return next.startCall(call, headers);
            }
        };
    }
    
    @GRpcService
    public static class CalculatorService extends CalculatorGrpc.CalculatorImplBase{
        @Override
        public void calculate(CalculatorOuterClass.CalculatorRequest request, StreamObserver<CalculatorOuterClass.CalculatorResponse> responseObserver) {
            CalculatorOuterClass.CalculatorResponse.Builder resultBuilder = CalculatorOuterClass.CalculatorResponse.newBuilder();
            switch (request.getOperation()){
                case ADD:
                    resultBuilder.setResult(request.getNumber1()+request.getNumber2());
                    break;
                case SUBTRACT:
                    resultBuilder.setResult(request.getNumber1()-request.getNumber2());
                    break;
                case MULTIPLY:
                    resultBuilder.setResult(request.getNumber1()*request.getNumber2());
                    break;
                case DIVIDE:
                    resultBuilder.setResult(request.getNumber1()/request.getNumber2());
                    break;
                case UNRECOGNIZED:
                    break;
            }
            responseObserver.onNext(resultBuilder.build());
            responseObserver.onCompleted();


        }
        
    }

}
