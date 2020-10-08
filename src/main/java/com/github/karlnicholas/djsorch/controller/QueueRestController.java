package com.github.karlnicholas.djsorch.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ServerWebExchange;

import com.github.karlnicholas.djsorch.model.TransactionSubmitted;
import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.github.karlnicholas.djsorch.queue.SubjectQueueManager;
import com.github.karlnicholas.djsorch.repository.TransactionSubmittedRepository;
import com.github.karlnicholas.djsorch.service.BusinessDateService;

@RestController
@RequestMapping("/queue")
public class QueueRestController {
	private static final Logger logger = LoggerFactory.getLogger(QueueRestController.class);
	private final SubjectQueueManager subjectQueueManager;
	private final TransactionSubmittedRepository transactionSubmittedRepository;
	private final BusinessDateService businessDateService;
	private AtomicLong queueId;
	public QueueRestController(
			SubjectQueueManager subjectQueueManager, 
			TransactionSubmittedRepository transactionSubmittedRepository, 
			BusinessDateService businessDateService 
	) {
		this.subjectQueueManager = subjectQueueManager;
		this.transactionSubmittedRepository = transactionSubmittedRepository;
		this.businessDateService = businessDateService;
		this.queueId = new AtomicLong();
	}
	@PostMapping("/post")
	public ResponseEntity<?> handlePost(@RequestBody TransactionSubmitted transactionSubmitted) {
		try {
			transactionSubmitted.setBusinessDate(businessDateService.getBusinessDate());
			transactionSubmitted.setAsNew();
			logger.info("post saving: " + transactionSubmitted);
			transactionSubmittedRepository.save(transactionSubmitted);
			queueNewTransactionPost(transactionSubmitted.getAccountId(), transactionSubmitted.getId(), "transaction");
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

	private void queueNewTransactionPost(Long accountId, Long transactionId, String action) {
		QueueEntry queueEntry = QueueEntry.builder()
				.queueId(queueId.getAndIncrement())
				.action(action)
				.accountId(accountId.toString())
				.transactionId(transactionId.toString())
				.httpMethod("POST")
				.build();
		
		subjectQueueManager.addQueueEntry(accountId.toString(), queueEntry);
//		queueEntry.getMonoWim().publishOn(Schedulers.elastic()).subscribe();
	}
*/
	private void queueNewTransactionPost(Long accountId, Long transactionId, String action) {
		QueueEntry queueEntry = QueueEntry.builder()
				.queueId(queueId.getAndIncrement())
				.action(action)
				.accountId(accountId.toString())
				.transactionId(transactionId.toString())
				.httpMethod("POST")
				.build();
		
		subjectQueueManager.addQueueEntry(accountId.toString(), queueEntry);
//		queueEntry.getMonoWim().publishOn(Schedulers.elastic()).subscribe();
	}
	@GetMapping("/get/{action}/{subject}")
	public DeferredResult<ResponseEntity<?>> handleGet(ServerWebExchange serverWebExchange) {
		String action = serverWebExchange.getRequiredAttribute("action");
		String subject = serverWebExchange.getRequiredAttribute("subject");
		
	    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
	    
		QueueEntry queueEntry = QueueEntry.builder()
				.action(action)
				.accountId(subject)
				.serverWebExchange(serverWebExchange)
				.output(output)
				.httpMethod("GET")
				.build();
		subjectQueueManager.addQueueEntry(subject, queueEntry);
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
