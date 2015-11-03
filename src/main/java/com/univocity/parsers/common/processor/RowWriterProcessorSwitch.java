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

public abstract class RowWriterProcessorSwitch<T> implements RowWriterProcessor<T> {

	private RowWriterProcessor<T> selectedRowWriterProcessor;

	protected abstract RowWriterProcessor<T> switchRowProcessor(T row);

	protected String[] getHeaders() {
		return null;
	}

	protected int[] getIndexes() {
		return null;
	}

	public void rowProcessorSwitched(RowWriterProcessor<T> from, RowWriterProcessor<T> to) {
	}

	public RowWriterProcessorSwitch() {
		selectedRowWriterProcessor = null;
	}

	@Override
	public Object[] write(T input, String[] headers, int[] indexesToWrite) {
		RowWriterProcessor<T> processor = switchRowProcessor(input);
		if (processor == null) {
			return null;
		}
		if (processor != selectedRowWriterProcessor) {
			rowProcessorSwitched(selectedRowWriterProcessor, processor);
			selectedRowWriterProcessor = processor;
		}

		String[] headersToUse = getHeaders();
		int[] indexesToUse = getIndexes();

		headersToUse = headersToUse == null ? headers : headersToUse;
		indexesToUse = indexesToUse == null ? indexesToWrite : indexesToUse;

		return selectedRowWriterProcessor.write(input, headersToUse, indexesToUse);
	}
}
