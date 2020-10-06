package com.github.karlnicholas.djsorch.queue;

@FunctionalInterface
public interface ProcessGetMethod {
	void getMethod(QueueEntry queueEntry) throws Exception;
}
