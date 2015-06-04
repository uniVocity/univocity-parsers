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

import java.util.*;
import java.util.Map.Entry;

import com.univocity.parsers.common.*;

public abstract class RowProcessorSwitch implements RowProcessor {

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
	public final void processStarted(ParsingContext context) {
		rowProcessors = new HashMap<RowProcessor, ParsingContextWrapper>();
		selectedRowProcessor = NoopRowProcessor.instance;
	}

	@Override
	public final void rowProcessed(String[] row, ParsingContext context) {
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
						return fieldNames;
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
	public final void processEnded(ParsingContext context) {
		rowProcessorSwitched(selectedRowProcessor, null);
		selectedRowProcessor = NoopRowProcessor.instance;
		for (Entry<RowProcessor, ParsingContextWrapper> e : rowProcessors.entrySet()) {
			e.getKey().processEnded(e.getValue());
		}
	}

}