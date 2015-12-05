/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;

import java.util.*;
import java.util.Map.*;

public abstract class RowProcessorSwitch implements RowProcessor, ColumnOrderDependent {

	private Map<RowProcessor, ParsingContextWrapper> rowProcessors;
	private RowProcessor selectedRowProcessor;
	private ParsingContextWrapper contextForProcessor;

	protected abstract RowProcessor switchRowProcessor(String[] row, ParsingContext context);

	public String[] getHeaders() {
		return null;
	}

	public void rowProcessorSwitched(RowProcessor from, RowProcessor to) {
	}

	@Override
	public void processStarted(ParsingContext context) {
		rowProcessors = new HashMap<RowProcessor, ParsingContextWrapper>();
		selectedRowProcessor = NoopRowProcessor.instance;
	}

	@Override
	public final void rowProcessed(String[] row, final ParsingContext context) {
		RowProcessor processor = switchRowProcessor(row, context);
		if (processor == null) {
			processor = NoopRowProcessor.instance;
		}
		if (processor != selectedRowProcessor) {
			contextForProcessor = rowProcessors.get(processor);

			if (contextForProcessor == null) {
				contextForProcessor = new ParsingContextWrapper(context) {

					private final String[] fieldNames = getHeaders();

					@Override
					public String[] headers() {
						return fieldNames == null || fieldNames.length == 0 ? context.headers() : fieldNames;
					}
				};

				processor.processStarted(contextForProcessor);
				rowProcessors.put(processor, contextForProcessor);
			}

			if (selectedRowProcessor != NoopRowProcessor.instance) {
				rowProcessorSwitched(selectedRowProcessor, processor);
			}
			selectedRowProcessor = processor;
			selectedRowProcessor.rowProcessed(row, contextForProcessor);
		} else {
			selectedRowProcessor.rowProcessed(row, contextForProcessor);
		}
	}

	@Override
	public void processEnded(ParsingContext context) {
		rowProcessorSwitched(selectedRowProcessor, null);
		selectedRowProcessor = NoopRowProcessor.instance;
		for (Entry<RowProcessor, ParsingContextWrapper> e : rowProcessors.entrySet()) {
			e.getKey().processEnded(e.getValue());
		}
	}

	public boolean preventColumnReordering() {
		return true;
	}
}