package com.github.karlnicholas.djsorch.queue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SubjectQueueManager {
	private Map<String, Queue<QueueEntry>> processQueueMap;

    private Map<String, ProcessGetMethod> getMethods;
    private Map<String, ProcessPostMethod> postMethods;

    public SubjectQueueManager() {
		processQueueMap = new HashMap<>();
		getMethods = new HashMap<>();
		postMethods = new HashMap<>();
    }

	public synchronized void addQueueEntry( String accountId, QueueEntry queueEntry) {
		Queue<QueueEntry> queue;
		if ( processQueueMap.containsKey(accountId)) {
			queue = processQueueMap.get(accountId);
		} else {
			queue = new LinkedList<>();
			processQueueMap.put(accountId, queue);
		}
		queue.add(queueEntry);
		recheckQueue(queue);
		if ( queue.isEmpty() ) {
			processQueueMap.remove(accountId);
		}
	}

	public Queue<QueueEntry> getProcessQueueMap(String accountId) {
		return processQueueMap.get(accountId);
	}
	
	
	private void handleGet(QueueEntry queueEntry) {
		try {
			ProcessGetMethod processGetMethod = getMethods.get(queueEntry.getAction());
			processGetMethod.getMethod(queueEntry);
		} catch (Exception e) {
			queueEntry.getResponse().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			queueEntry.getDeferredResult().setErrorResult(e.getMessage());
		}
	}
	private void handlePost(QueueEntry queueEntry) {
			queueEntry.setHttpMethod("posted");
			ProcessPostMethod processPostMethod = postMethods.get(queueEntry.getAction());
			processPostMethod.postMethod(queueEntry, this::completePostedTransaction);
	}
	public void addGetMethod(String action, ProcessGetMethod processGetMethod) {
		getMethods.put(action, processGetMethod);
	}
	public void addPostMethod(String action, ProcessPostMethod processPostMethod) {
		postMethods.put(action, processPostMethod);
	}

	public synchronized void completePostedTransaction(long queueId) {
		Iterator<Entry<String, Queue<QueueEntry>>> entryIt = processQueueMap.entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<String, Queue<QueueEntry>> mapEntry = entryIt.next();
			if ( mapEntry.getValue().stream().filter(queueEntry->queueEntry.getQueueId() == queueId).findAny().isPresent()) {
				Queue<QueueEntry> queue = mapEntry.getValue();
				if ( !queue.isEmpty() && queue.peek().getQueueId() == queueId) {
					queue.remove();
					recheckQueue(queue);
					if ( queue.isEmpty() ) {
						entryIt.remove();
					}
				} else {
					throw new IllegalStateException("Queue Invalid:"+queue);
				}
				break;
			}
		}
	}

	private void recheckQueue(Queue<QueueEntry> queue) {
		if ( !queue.isEmpty() ) {
			QueueEntry queueEntry = queue.peek();
			if ( queueEntry.getHttpMethod().equalsIgnoreCase("get") ) {
				handleGets(queue);
			}
			if ( !queue.isEmpty() ) {
				queueEntry = queue.peek();
				if ( queueEntry.getHttpMethod().equalsIgnoreCase("post") ) {
					handlePost(queueEntry);
				}
			}
		}
	}
	
	private void handleGets(Queue<QueueEntry> queue) {
		while (queue.peek() != null && queue.peek().getHttpMethod().equalsIgnoreCase("get") ) {
			handleGet(queue.remove());
		}
	}

}
