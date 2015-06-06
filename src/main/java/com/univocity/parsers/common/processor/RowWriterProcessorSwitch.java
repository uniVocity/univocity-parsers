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
