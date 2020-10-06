package com.github.karlnicholas.djsorch.queue;

import javax.servlet.ServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueueEntry {
	private String action;
	private String accountId;
	private String transactionId;
	private String httpMethod;
	// for get
	private ServerWebExchange serverWebExchange;
	private DeferredResult<ResponseEntity<?>> output;
//	private ServletResponse servletResponse; 
	private long queueId;
}
