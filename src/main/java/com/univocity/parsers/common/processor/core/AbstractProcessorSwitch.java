/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

import java.util.*;
import java.util.Map.*;

/**
 * A special {@link Processor} implementation that combines and allows switching among different
 * Processors. Each Processor will have its own {@link Context}. Concrete implementations of this class
 * are expected to implement the {@link #switchRowProcessor(String[], Context)} method and analyze the input row
 * to determine whether or not the current {@link Processor} implementation must be changed to handle a special
 * circumstance (determined by the concrete implementation) such as a different row format.
 *
 * When the processor is switched, the {@link #processorSwitched(Processor, Processor)} will be called, and
 * must be overridden, to notify the change to the user.
 */
public abstract class AbstractProcessorSwitch<T extends Context> implements Processor<T>, ColumnOrderDependent {

	private Map<Processor, T> processors;
	private Processor selectedProcessor;
	private T contextForProcessor;

	/**
	 * Analyzes the input to determine whether or not the row processor implementation must be changed
	 *
	 * @param row     a row parsed from the input
	 * @param context the current parsing context (not associated with the current row processor used by this class)
	 *
	 * @return the row processor implementation to use. If it is not the same as the one used by the previous row,
	 * the returned row processor will be used, and the {@link #processorSwitched(Processor, Processor)} method
	 * will be called.
	 */
	protected abstract Processor<T> switchRowProcessor(String[] row, T context);

	/**
	 * Returns the headers in use by the current row processor implementation, which can vary among row processors.
	 * If {@code null}, the headers parsed by the input, or defined in {@link CommonParserSettings#getHeaders()} will be returned.
	 *
	 * @return the current sequence of headers to use.
	 */
	public String[] getHeaders() {
		return null;
	}

	/**
	 * Returns the indexes in use by the current row processor implementation, which can vary among row processors.
	 * If {@code null} all columns of a given record will be considered.
	 *
	 * @return the current sequence of indexes to use.
	 */
	public int[] getIndexes() {
		return null;
	}

	/**
	 * Notifies a change of {@link Processor} implementation. Users are expected to override this method to receive the notification.
	 *
	 * @param from the processor previously in use
	 * @param to   the new processor to use to continue processing the input.
	 */
	public void processorSwitched(Processor<T> from, Processor<T> to) {
		if (from != null) {
			if (from instanceof RowProcessor) {
				if (to == null || to instanceof RowProcessor) {
					rowProcessorSwitched((RowProcessor) from, (RowProcessor) to);
				}
			}
		} else if (to != null && to instanceof RowProcessor) {
			rowProcessorSwitched((RowProcessor) from, (RowProcessor) to);
		}
	}


	/**
	 * Notifies a change of {@link RowProcessor} implementation. Users are expected to override this method to receive the notification.
	 *
	 * @param from the row processor previously in use
	 * @param to   the new row processor to use to continue processing the input.
	 */
	public void rowProcessorSwitched(RowProcessor from, RowProcessor to) {

	}

	@Override
	public void processStarted(T context) {
		processors = new HashMap<Processor, T>();
		selectedProcessor = NoopProcessor.instance;
	}

	/**
	 * Wraps a given parser context object that returns headers and extracted field indexes
	 * associated with a given processor in this switch.
	 * @param context the context to wrap
	 * @return a wrapped context that returns the headers and extracted field
	 * indexes from {@link #getHeaders()} and {@link #getIndexes()}
	 */
	protected abstract T wrapContext(T context);

	@Override
	public final void rowProcessed(String[] row, final T context) {
		Processor processor = switchRowProcessor(row, context);
		if (processor == null) {
			processor = NoopProcessor.instance;
		}
		if (processor != selectedProcessor) {
			contextForProcessor = processors.get(processor);

			if (processor != NoopProcessor.instance) {
				if (contextForProcessor == null) {
					contextForProcessor = wrapContext(context);

					processor.processStarted(contextForProcessor);
					processors.put(processor, contextForProcessor);
				}

				processorSwitched(selectedProcessor, processor);
				selectedProcessor = processor;
				if (getIndexes() != null) {
					int[] indexes = getIndexes();
					String[] tmp = new String[indexes.length];
					for (int i = 0; i < indexes.length; i++) {
						int index = indexes[i];
						if (index < row.length) {
							tmp[i] = row[index];
						}
					}
					row = tmp;
				}
				selectedProcessor.rowProcessed(row, contextForProcessor);
			}
		} else {
			selectedProcessor.rowProcessed(row, contextForProcessor);
		}
	}

	@Override
	public void processEnded(T context) {
		processorSwitched(selectedProcessor, null);
		selectedProcessor = NoopProcessor.instance;
		for (Entry<Processor, T> e : processors.entrySet()) {
			e.getKey().processEnded(e.getValue());
		}
	}

	public boolean preventColumnReordering() {
		return true;
	}
}