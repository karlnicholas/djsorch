package com.github.karlnicholas.djsorch.queue;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

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
	private HttpServletResponse response;
	private DeferredResult<ResponseEntity<?>> deferredResult;
//	private ServletResponse servletResponse; 
	private long queueId;
}
