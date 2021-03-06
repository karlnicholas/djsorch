package com.github.karlnicholas.djsorch.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.github.karlnicholas.djsorch.queue.QueueEntry;
import com.github.karlnicholas.djsorch.queue.SubjectQueueManager;

@Service
public class QueueService {
	private final AtomicLong queueId;
	private final SubjectQueueManager subjectQueueManager;
	public QueueService(SubjectQueueManager subjectQueueManager) {
		queueId = new AtomicLong();
		this.subjectQueueManager = subjectQueueManager;
	}
	public void queueNewTransactionPost(Long accountId, Long transactionId, String action) {
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
	public void queueNewGet(HttpServletResponse response, String action, String subject,
			DeferredResult<ResponseEntity<?>> deferredResult) {
		QueueEntry queueEntry = QueueEntry.builder()
				.action(action)
				.accountId(subject)
				.response(response)
				.deferredResult(deferredResult)
				.httpMethod("GET")
				.build();
		subjectQueueManager.addQueueEntry(subject, queueEntry);
	}
}
