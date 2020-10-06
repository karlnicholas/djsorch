package com.github.karlnicholas.djsorch.queue;

import java.util.function.Consumer;

@FunctionalInterface
public interface ProcessPostMethod {
	void postMethod(QueueEntry queueEntry, Consumer<Long> completePostedTransaction);
}
