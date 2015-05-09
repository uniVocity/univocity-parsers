/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.processor;

import java.util.concurrent.*;

import com.univocity.parsers.common.*;

/**
 * A {@link RowProcessor} implementation to perform row processing tasks in parallel. The {@code ConcurrentRowProcessor} wraps another {@link RowProcessor}, and collects rows read from the input.
 * The actual row processing is performed in by wrapped {@link RowProcessor} in a separate thread.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @see AbstractParser
 * @see RowProcessor
 */
public class ConcurrentRowProcessor implements RowProcessor {

	private final RowProcessor rowProcessor;

	private boolean ended = false;

	private class Node {
		public Node(String[] row) {
			this.row = row;
		}

		public final String[] row;
		public Node next;
	}

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private long rowCount;

	private Future<Void> process;

	private ParsingContext context;
	private Node inputQueue;
	private volatile Node outputQueue;

	/**
	 * Creates a non-blocking {@code ConcurrentRowProcessor}, to perform processing of rows parsed from the input in a separate thread.
	 * @param rowProcessor a regular {@link RowProcessor} implementation which will be executed in a separate thread.
	 */
	public ConcurrentRowProcessor(RowProcessor rowProcessor) {
		if (rowProcessor == null) {
			throw new IllegalArgumentException("Row processor cannot be null");
		}
		this.rowProcessor = rowProcessor;
	}

	@Override
	public final void processStarted(ParsingContext context) {
		rowProcessor.processStarted(context);

		this.context = new ParsingContextWrapper(context) {
			@Override
			public long currentRecord() {
				return rowCount;
			}
		};

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
					rowProcessor.rowProcessed(outputQueue.row, context);

					while (outputQueue.next == null) {
						if (ended && outputQueue.next == null) {
							return null;
						}
					}
					outputQueue = outputQueue.next;
				}

				while (outputQueue != null) {
					rowCount++;
					rowProcessor.rowProcessed(outputQueue.row, context);
					outputQueue = outputQueue.next;
				}

				return null;
			}

		});
	}

	@Override
	public final void rowProcessed(String[] row, ParsingContext context) {
		if (inputQueue == null) {
			inputQueue = new Node(row);
			outputQueue = inputQueue;
		} else {
			inputQueue.next = new Node(row);
			inputQueue = inputQueue.next;
		}
	}

	@Override
	public final void processEnded(ParsingContext context) {
		rowProcessor.processEnded(context);
		ended = true;

		try {
			process.get();
		} catch (ExecutionException e) {
			throw new DataProcessingException("Error executing process", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Process interrupted", e);
		}
	}
}
