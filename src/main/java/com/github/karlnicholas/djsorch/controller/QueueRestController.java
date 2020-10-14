package com.github.karlnicholas.djsorch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.github.karlnicholas.djsorch.model.TransactionSubmitted;
import com.github.karlnicholas.djsorch.repository.TransactionSubmittedRepository;
import com.github.karlnicholas.djsorch.service.BusinessDateService;
import com.github.karlnicholas.djsorch.service.QueueService;

@RestController
@RequestMapping("/queue")
public class QueueRestController {
	private static final Logger logger = LoggerFactory.getLogger(QueueRestController.class);
	private final TransactionSubmittedRepository transactionSubmittedRepository;
	private final BusinessDateService businessDateService;
	private final QueueService queueService;
	public QueueRestController(
			TransactionSubmittedRepository transactionSubmittedRepository, 
			BusinessDateService businessDateService, 
			QueueService queueService
	) {
		this.transactionSubmittedRepository = transactionSubmittedRepository;
		this.businessDateService = businessDateService;
		this.queueService = queueService;
	}
	@PostMapping("/post")
	public ResponseEntity<?> handlePost(@RequestBody TransactionSubmitted transactionSubmitted) {
		try {
			transactionSubmitted.setBusinessDate(businessDateService.getBusinessDate());
			transactionSubmitted.setAsNew();
			logger.info("post saving: " + transactionSubmitted);
			transactionSubmittedRepository.save(transactionSubmitted);
			queueService.queueNewTransactionPost(transactionSubmitted.getAccountId(), transactionSubmitted.getId(), "transaction");
			return ResponseEntity.accepted().build();
		} catch ( Exception e ) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

//		queueNewTransactionPost(transactionSubmitted.getAccount().getId(), transactionSubmitted.getId(), "transaction");

	}
/*	
	public Mono<ServerResponse> playPost(ServerRequest serverRequest) {
			QueueEntry queueEntry = QueueEntry.builder()
					.queueId(queueId.getAndIncrement())
					.action("playpost")
					.accountId("ACCOUNTID")
					.transactionId("TRANSACTIONID")
					.serverWebExchange(serverRequest.exchange())
					.httpMethod("POST")
					.build();
			subjectQueueManager.addQueueEntry("ACCOUNTID", queueEntry);
			return serverRequest.exchange().getResponse().setComplete()
					.flatMap(v->ServerResponse.accepted().build())
					.doOnError(e->logger.error("Exception: " + e.getMessage()))
					.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	public SubjectQueueManager getSubjectQueueManager() {
		return subjectQueueManager;
	}
*/
	@GetMapping("/get/{action}/{subject}")
	public DeferredResult<ResponseEntity<?>> handleGet(
			@PathVariable("action") String action, 
			@PathVariable("subject") String subject, 
			HttpServletRequest request, 
			HttpServletResponse response 
	) {
		
	    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
	    
	    queueService.queueNewGet(response, action, subject, output);
	    return output;
	}
	@GetMapping("transactions")
	public Iterable<TransactionSubmitted> listTransactions() {
		return transactionSubmittedRepository.findAll();
	}
	@GetMapping("count")
	public Long countTransactions() {
		return transactionSubmittedRepository.count();
	}
	@PostMapping("count")
	public ResponseEntity<String> handlePostCount() {
		logger.info("posting");
		return ResponseEntity.ok(Long.toString(transactionSubmittedRepository.count()));

//		queueNewTransactionPost(transactionSubmitted.getAccount().getId(), transactionSubmitted.getId(), "transaction");

	}
}
