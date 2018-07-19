/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;

import java.util.concurrent.*;

/**
 * A {@link Processor} implementation to perform row processing tasks in parallel. The {@code ConcurrentRowProcessor}
 * wraps another {@link Processor}, and collects rows read from the input.
 * The actual row processing is performed in by wrapped {@link Processor} in a separate thread.
 *
 * <i>Note: </i> by default the {@link Context} object passed on to the wrapped {@link Processor} will <b>not</b> reflect the
 * state of the parser at the time the row as generated, but the current state of the parser instead. You can enable the
 * {@link #contextCopyingEnabled} flag to generate copies of the {@link Context} at the time each row was generated.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see Processor
 */
public abstract class AbstractConcurrentProcessor<T extends Context> implements Processor<T> {

	private final Processor processor;

	private boolean ended = false;

	private static class Node<T> {
		public Node(String[] row, T context) {
			this.row = row;
			this.context = context;
		}

		public final T context;
		public final String[] row;
		public Node next;
	}

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private volatile long rowCount;

	private Future<Void> process;

	private T currentContext;
	private Node<T> inputQueue;
	private volatile Node<T> outputQueue;
	private final int limit;
	private volatile long input;
	private volatile long output;
	private final Object lock;
	private boolean contextCopyingEnabled = false;

	/**
	 * Creates a non-blocking {@code AbstractConcurrentProcessor}, to perform processing of rows parsed from the input in a separate thread.
	 *
	 * @param processor a regular {@link Processor} implementation which will be executed in a separate thread.
	 */
	public AbstractConcurrentProcessor(Processor<T> processor) {
		this(processor, -1);
	}

	/**
	 * Creates a blocking {@code ConcurrentProcessor}, to perform processing of rows parsed from the input in a separate thread.
	 *
	 * @param processor a regular {@link Processor} implementation which will be executed in a separate thread.
	 * @param limit     the limit of rows to be kept in memory before blocking the input parsing process.
	 */
	public AbstractConcurrentProcessor(Processor<T> processor, int limit) {
		if (processor == null) {
			throw new IllegalArgumentException("Row processor cannot be null");
		}
		this.processor = processor;
		input = 0;
		output = 0;
		lock = new Object();
		this.limit = limit;
	}

	/**
	 * Indicates whether this processor should persist the {@link Context} object that is sent to the wrapped {@link Processor}
	 * given in the constructor of this class, so all methods of {@link Context} reflect the parser state at the time
	 * each row was parsed.
	 *
	 * Defaults to {@code false}
	 *
	 * @return a flag indicating whether the parsing context must be persisted along with the parsed row
	 * so its methods reflect the state of the parser at the time the record was produced.
	 */
	public boolean isContextCopyingEnabled() {
		return contextCopyingEnabled;
	}

	/**
	 * Configures this processor to persist the {@link Context} object that is sent to the wrapped {@link Processor}
	 * given in the constructor of this class, so all methods of {@link Context} reflect the parser state at the time
	 * each row was parsed.
	 *
	 * Defaults to {@code false}
	 *
	 * @param contextCopyingEnabled a flag indicating whether the parsing context must be persisted along with the parsed row
	 *                              so its methods reflect the state of the parser at the time the record was produced.
	 */
	public void setContextCopyingEnabled(boolean contextCopyingEnabled) {
		this.contextCopyingEnabled = contextCopyingEnabled;
	}

	@Override
	public final void processStarted(T context) {
		currentContext = wrapContext(context);

		processor.processStarted(currentContext);

		startProcess();
	}

	private void startProcess() {
		ended = false;
		rowCount = 0;

		process = executor.submit(new Callable<Void>() {

			@Override
			public Void call() {
				while (outputQueue == null && !ended) {
					Thread.yield();
				}

				while (!ended) {
					rowCount++;


					processor.rowProcessed(outputQueue.row, outputQueue.context);
					while (outputQueue.next == null) {
						if (ended && outputQueue.next == null) {
							return null;
						}
						Thread.yield();
					}
					outputQueue = outputQueue.next;
					output++;
					if (limit > 1) {
						synchronized (lock) {
							lock.notify();
						}
					}
				}

				while (outputQueue != null) {
					rowCount++;
					processor.rowProcessed(outputQueue.row, outputQueue.context);
					outputQueue = outputQueue.next;
				}

				return null;
			}

		});
	}

	@Override
	public final void rowProcessed(String[] row, T context) {
		if (inputQueue == null) {
			inputQueue = new Node(row, grabContext(context));
			outputQueue = inputQueue;
		} else {
			if (limit > 1) {
				synchronized (lock) {
					try {
						if (input - output >= limit) {
							lock.wait();
						}
					} catch (InterruptedException e) {
						ended = true;
						Thread.currentThread().interrupt();
						return;
					}
				}
			}
			inputQueue.next = new Node(row, grabContext(context));
			inputQueue = inputQueue.next;
		}
		input++;
	}

	@Override
	public final void processEnded(T context) {
		ended = true;
		if (limit > 1) {
			synchronized (lock) {
				lock.notify();
			}
		}

		try {
			process.get();
		} catch (ExecutionException e) {
			throw new DataProcessingException("Error executing process", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			try {
				processor.processEnded(grabContext(context));
			} finally{
				executor.shutdown();
			}
		}
	}

	private T grabContext(T context) {
		if (contextCopyingEnabled) {
			return copyContext(context);
		}
		return currentContext;

	}

	protected final long getRowCount(){
		return rowCount;
	}

	protected abstract T copyContext(T context);

	protected abstract T wrapContext(T context);
}
