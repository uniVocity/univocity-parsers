/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 * The AbstractWriter class provides a common ground for all writers in uniVocity-parsers.
 *
 * It handles all settings defined by {@link CommonWriterSettings}, and delegates the writing algorithm implementation to its subclasses through the abstract method {@link AbstractWriter#processRow(Object[])}
 *
 * <p> The following (absolutely required) attributes are exposed to subclasses:
 * <ul>
 * <li><b>appender (<i>{@link WriterCharAppender}</i>):</b> the character writer that appends characters from a given input into an internal buffer</li>
 * </ul>
 *
 * @param <S> The specific writer settings configuration class, which can potentially provide additional configuration options supported by the writer implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.csv.CsvWriter
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriter
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 * @see com.univocity.parsers.tsv.TsvWriter
 * @see com.univocity.parsers.tsv.TsvWriterSettings
 * @see com.univocity.parsers.common.input.WriterCharAppender
 * @see com.univocity.parsers.common.processor.RowWriterProcessor
 */
public abstract class AbstractWriter<S extends CommonWriterSettings<?>> {

	@SuppressWarnings("rawtypes")
	private final RowWriterProcessor writerProcessor;

	private Writer writer;
	private final boolean skipEmptyLines;
	private final char comment;
	private final WriterCharAppender rowAppender;
	private final boolean isHeaderWritingEnabled;

	private Object[] outputRow;
	private int[] indexesToWrite;
	private final char[] lineSeparator;

	protected String[] headers;
	protected long recordCount = 0;

	protected final String nullValue;
	protected final String emptyValue;
	protected final WriterCharAppender appender;

	private final Object[] partialLine;
	private int partialLineIndex = 0;
	private Map<String[], Map<String, Integer>> headerIndexes;
	private int largestRowLength = -1;
	protected boolean writingHeaders = false;

	private String[] dummyHeaderRow;
	protected boolean expandRows;
	private boolean usingSwitch;
	private boolean enableNewlineAfterRecord = true;
	protected boolean usingNullOrEmptyValue;
	protected final int whitespaceRangeStart;
	private final boolean columnReorderingEnabled;

	private final CommonSettings<DummyFormat> internalSettings = new CommonSettings<DummyFormat>() {
		@Override
		protected DummyFormat createDefaultFormat() {
			return DummyFormat.instance;
		}
	};
	private final int errorContentLength;

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 *
	 * @param settings the writer configuration
	 */
	public AbstractWriter(S settings) {
		this((Writer) null, settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param file     the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, S settings) {
		this(ArgumentUtils.newWriter(file), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param file     the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the file
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, String encoding, S settings) {
		this(ArgumentUtils.newWriter(file, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param file     the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the file
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, Charset encoding, S settings) {
		this(ArgumentUtils.newWriter(file, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, S settings) {
		this(ArgumentUtils.newWriter(output), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the stream
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, String encoding, S settings) {
		this(ArgumentUtils.newWriter(output, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the stream
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, Charset encoding, S settings) {
		this(ArgumentUtils.newWriter(output, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 *
	 * @param writer   the output resource that will receive the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(Writer writer, S settings) {
		settings.autoConfigure();
		internalSettings.setMaxColumns(settings.getMaxColumns());
		this.errorContentLength = settings.getErrorContentLength();
		this.nullValue = settings.getNullValue();
		this.emptyValue = settings.getEmptyValue();

		this.lineSeparator = settings.getFormat().getLineSeparator();
		this.comment = settings.getFormat().getComment();
		this.skipEmptyLines = settings.getSkipEmptyLines();
		this.writerProcessor = settings.getRowWriterProcessor();
		this.usingSwitch = writerProcessor instanceof RowWriterProcessorSwitch;
		this.expandRows = settings.getExpandIncompleteRows();
		this.columnReorderingEnabled = settings.isColumnReorderingEnabled();
		this.whitespaceRangeStart = settings.getWhitespaceRangeStart();
		this.appender = new WriterCharAppender(settings.getMaxCharsPerColumn(), "", whitespaceRangeStart, settings.getFormat());
		this.rowAppender = new WriterCharAppender(settings.getMaxCharsPerColumn(), "", whitespaceRangeStart, settings.getFormat());


		this.writer = writer;


		this.headers = settings.getHeaders();

		updateIndexesToWrite(settings);

		this.partialLine = new Object[settings.getMaxColumns()];
		this.isHeaderWritingEnabled = settings.isHeaderWritingEnabled();

		if (writerProcessor instanceof DefaultConversionProcessor) {
			DefaultConversionProcessor conversionProcessor = (DefaultConversionProcessor) writerProcessor;
			conversionProcessor.context = null;
			conversionProcessor.errorHandler = settings.getProcessorErrorHandler();
		}

		initialize(settings);
	}

	protected void enableNewlineAfterRecord(boolean enableNewlineAfterRecord) {
		this.enableNewlineAfterRecord = enableNewlineAfterRecord;
	}

	/**
	 * Initializes the concrete implementation of this class with format-specific settings.
	 *
	 * @param settings the settings object specific to the format being written.
	 */
	protected abstract void initialize(S settings);

	/**
	 * Update indexes to write based on the field selection provided by the user.
	 */
	private void updateIndexesToWrite(CommonSettings<?> settings) {
		FieldSelector selector = settings.getFieldSelector();
		if (selector != null) {
			if (headers != null && headers.length > 0) {
				indexesToWrite = selector.getFieldIndexes(headers);
				if (columnReorderingEnabled) { //column reordering enabled?
					outputRow = new Object[indexesToWrite.length];
				} else {
					outputRow = new Object[headers.length];
				}
			} else if (!(selector instanceof FieldNameSelector) && !(selector instanceof ExcludeFieldNameSelector)) {
				int rowLength = largestRowLength;
				if ((selector instanceof FieldIndexSelector)) {
					boolean gotLengthFromSelection = false;
					for (Integer index : ((FieldIndexSelector) selector).get()) {
						if (rowLength <= index) {
							rowLength = index;
							gotLengthFromSelection = true;
						}
					}
					if (gotLengthFromSelection) {
						rowLength++;
					}
					if (rowLength < largestRowLength) {
						rowLength = largestRowLength;
					}
				} else {
					rowLength = settings.getMaxColumns();
				}
				indexesToWrite = selector.getFieldIndexes(new String[rowLength]); //generates a dummy header array - only the indexes matter so we are good
				if (columnReorderingEnabled) { //column reordering enabled?
					outputRow = new Object[indexesToWrite.length];
				} else {
					outputRow = new Object[rowLength];
				}
			} else {
				throw new IllegalStateException("Cannot select fields by name with no headers defined");
			}
		} else {
			outputRow = null;
			indexesToWrite = null;
		}
	}

	/**
	 * Updates the selection of fields to write.  This is useful if the input rows
	 * change during the writing process and their values need be allocated to specific columns.
	 *
	 * @param newFieldSelection the new selection of fields to write.
	 */
	public void updateFieldSelection(String... newFieldSelection) {
		if (headers == null) {
			throw new IllegalStateException("Cannot select fields by name. Headers not defined.");
		}
		internalSettings.selectFields(newFieldSelection);
		updateIndexesToWrite(internalSettings);
	}

	/**
	 * Updates the selection of fields to write. This is useful if the input rows
	 * change during the writing process and their values need be allocated to specific columns.
	 *
	 * @param newFieldSelectionByIndex the new selection of fields to write.
	 */
	public void updateFieldSelection(Integer... newFieldSelectionByIndex) {
		internalSettings.selectIndexes(newFieldSelectionByIndex);
		updateIndexesToWrite(internalSettings);
	}

	/**
	 * Updates the selection of fields to exclude when writing. This is useful if the input rows
	 * change during the writing process and their values need be allocated to specific columns.
	 *
	 * @param fieldsToExclude the selection of fields to exclude from the output.
	 */
	public void updateFieldExclusion(String... fieldsToExclude) {
		if (headers == null) {
			throw new IllegalStateException("Cannot de-select fields by name. Headers not defined.");
		}
		internalSettings.excludeFields(fieldsToExclude);
		updateIndexesToWrite(internalSettings);
	}

	/**
	 * Updates the selection of fields to exclude when writing. This is useful if the input rows
	 * change during the writing process and their values need be allocated to specific columns.
	 *
	 * @param fieldIndexesToExclude the selection of fields to exclude from the output.
	 */
	public void updateFieldExclusion(Integer... fieldIndexesToExclude) {
		internalSettings.excludeIndexes(fieldIndexesToExclude);
		updateIndexesToWrite(internalSettings);
	}

	/**
	 * Submits a row for processing by the format-specific implementation.
	 *
	 * @param row the data to be written for a single record in the output.
	 */
	private void submitRow(Object[] row) {
		if (largestRowLength < row.length) {
			largestRowLength = row.length;
		}
		processRow(row);
	}

	/**
	 * Format-specific implementation for writing a single record into the output.
	 *
	 * <p> The AbstractWriter handles the initialization and processing of the output until it is ready to be written (generally, reorganizing it and passing it on to a {@link RowWriterProcessor}).
	 * <p> It then delegates the record to the writer-specific implementation defined by {@link #processRow(Object[])}. In general, an implementation of {@link AbstractWriter#processRow(Object[])} will perform the following steps:
	 * <ul>
	 * <li>Iterate over each object in the given input and convert it to the expected String representation.</li>
	 * <li>The conversion <b>must</b> happen using the provided {@link AbstractWriter#appender} object. The an individual value is processed, the {@link AbstractWriter#appendValueToRow()} method must be called.
	 * This will clear the accumulated value in {@link AbstractWriter#appender} and add it to the output row.</li>
	 * <li>Format specific separators and other characters must be introduced to the output row using {@link AbstractWriter#appendToRow(char)}</li>
	 * </ul>
	 * <p> Once the {@link #processRow(Object[])} method returns, a row will be written to the output with the processed information, and a newline will be automatically written after the given contents, unless this is a
	 * {@link com.univocity.parsers.fixed.FixedWidthWriter} whose {@link FixedWidthWriterSettings#getWriteLineSeparatorAfterRecord()} evaluates to {@code false}. The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> This cycle repeats until the writing process is stopped by the user or an error happens.
	 * <p> In case of errors, the unchecked exception {@link TextWritingException} will be thrown and all resources in use will be closed automatically. The exception should contain the cause and more information about the output state when the error happened.
	 *
	 * @param row the data to be written to the output in the expected format.
	 *
	 * @see com.univocity.parsers.common.input.CharAppender
	 * @see com.univocity.parsers.common.CommonWriterSettings
	 */
	protected abstract void processRow(Object[] row);

	/**
	 * Appends the processed sequence of characters in {@link AbstractWriter#appender} to the output row.
	 */
	protected final void appendValueToRow() {
		rowAppender.append(appender);
	}

	/**
	 * Appends the given character to the output row.
	 *
	 * @param ch the character to append to the output row
	 */
	protected final void appendToRow(char ch) {
		rowAppender.append(ch);
	}

	/**
	 * Writes the headers defined in {@link CommonSettings#getHeaders()}
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to the output.
	 */
	public final void writeHeaders() {
		writeHeaders(this.headers);
	}

	/**
	 * Writes the given collection of headers to the output.
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to the output.
	 *
	 * @param headers the headers to write to the output.
	 */
	public final void writeHeaders(Collection<?> headers) {
		if (headers != null && headers.size() > 0) {
			writeHeaders(headers.toArray(new String[headers.size()]));
		} else {
			throw throwExceptionAndClose("No headers defined.");
		}
	}

	/**
	 * Writes the given collection of headers to the output.
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to the output.
	 *
	 * @param headers the headers to write to the output.
	 */
	public final void writeHeaders(String... headers) {
		if (recordCount > 0) {
			throw throwExceptionAndClose("Cannot write headers after records have been written.", headers, null);
		}
		if (headers != null && headers.length > 0) {
			writingHeaders = true;
			if (columnReorderingEnabled && outputRow != null) {
				fillOutputRow(headers);
				headers = Arrays.copyOf(outputRow, outputRow.length, String[].class);
			}
			submitRow(headers);

			this.headers = headers;
			internalWriteRow();
			writingHeaders = false;
		} else {
			throw throwExceptionAndClose("No headers defined.", headers, null);
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them, then finally and closes the output
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param allRecords the records to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecordsAndClose(Iterable<?> allRecords) {
		try {
			processRecords(allRecords);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them, then finally and closes the output
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param allRecords the records to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecordsAndClose(Object[] allRecords) {
		try {
			processRecords(allRecords);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param records the records to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecords(Iterable<?> records) {
		for (Object record : records) {
			processRecord(record);
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them.
	 * <p> The output will remain open for further writing.
	 * * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param records the records to transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecords(Object[] records) {
		for (Object record : records) {
			processRecord(record);
		}
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecord(Object... record) {
		processRecord((Object) record);
	}


	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	@SuppressWarnings("unchecked")
	public final void processRecord(Object record) {
		if (this.writerProcessor == null) {
			String recordDescription;
			if (record instanceof Object[]) {
				recordDescription = Arrays.toString((Object[]) record);
			} else {
				recordDescription = String.valueOf(record);
			}
			String message = "Cannot process record '" + recordDescription + "' without a writer processor. Please define a writer processor instance in the settings or use the 'writeRow' methods.";
			this.throwExceptionAndClose(message);
		}

		Object[] row;
		try {
			if (usingSwitch) {
				dummyHeaderRow = ((RowWriterProcessorSwitch) writerProcessor).getHeaders(record);
				if (dummyHeaderRow == null) {
					dummyHeaderRow = this.headers;
				}
				row = writerProcessor.write(record, dummyHeaderRow, indexesToWrite);
			} else {
				row = writerProcessor.write(record, getRowProcessorHeaders(), indexesToWrite);
			}
		} catch (DataProcessingException e) {
			e.setErrorContentLength(errorContentLength);
			throw e;
		}

		if (row != null) {
			writeRow(row);
		}
	}

	private String[] getRowProcessorHeaders() {
		if (headers == null && indexesToWrite == null) {
			return null;
		}
		return headers;
	}

	/**
	 * Iterates over all records, writes them and closes the output.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecordsAndClose(Iterable)} for that.
	 *
	 * @param <C>     Collection of objects containing values of a row
	 * @param allRows the rows to be written to the output
	 */
	public final <C extends Collection<?>> void writeRowsAndClose(Iterable<C> allRows) {
		try {
			writeRows(allRows);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records, writes them and closes the output.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecordsAndClose(Object[])} for that.
	 *
	 * @param allRows the rows to be written to the output
	 */
	public final void writeRowsAndClose(Collection<Object[]> allRows) {
		try {
			writeRows(allRows);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records, writes them and closes the output.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecordsAndClose(Iterable)} for that.
	 *
	 * @param allRows the rows to be written to the output
	 */
	public final void writeStringRowsAndClose(Collection<String[]> allRows) {
		try {
			writeStringRows(allRows);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records, writes them and closes the output.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecordsAndClose(Object[])} for that.
	 *
	 * @param allRows the rows to be written to the output
	 */
	public final void writeRowsAndClose(Object[][] allRows) {
		try {
			writeRows(allRows);
		} finally {
			close();
		}
	}

	/**
	 * Iterates over all records and writes them to the output.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Object[])} for that.
	 *
	 * @param rows the rows to be written to the output
	 */
	public final void writeRows(Object[][] rows) {
		for (Object[] row : rows) {
			writeRow(row);
		}
	}

	/**
	 * Iterates over all records and writes them to the output.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param <C>  Collection of objects containing values of a row
	 * @param rows the rows to be written to the output
	 */
	public final <C extends Collection<?>> void writeRows(Iterable<C> rows) {
		for (Collection<?> row : rows) {
			writeRow(row);
		}
	}

	/**
	 * Iterates over all records and writes them to the output.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to the output
	 */
	public final void writeStringRows(Collection<String[]> rows) {
		for (String[] row : rows) {
			writeRow(row);
		}
	}

	/**
	 * Iterates over all records and writes them to the output.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param <C>  Collection of objects containing values of a row
	 * @param rows the rows to be written to the output
	 */
	public final <C extends Collection<?>> void writeStringRows(Iterable<C> rows) {
		for (Collection<?> row : rows) {
			writeRow(row.toArray());
		}
	}

	/**
	 * Iterates over all records and writes them to the output.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to the output
	 */
	public final void writeRows(Collection<Object[]> rows) {
		for (Object[] row : rows) {
			writeRow(row);
		}
	}

	/**
	 * Writes the data given for an individual record.
	 * <p> The output will remain open for further writing.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to the output
	 */
	public final void writeRow(Collection<?> row) {
		if (row == null) {
			return;
		}
		writeRow(row.toArray());
	}

	/**
	 * Writes the data given for an individual record.
	 * <p> The output will remain open for further writing.
	 * <p> If the given data is null or empty, and {@link CommonSettings#getSkipEmptyLines()} is true, the input will be just ignored.
	 * <p> If {@link CommonSettings#getSkipEmptyLines()} is false, then an empty row will be written to the output (as specified by {@link AbstractWriter#writeEmptyRow()}).
	 * <p> In case of any errors, a {@link TextWritingException} will be thrown and the {@link java.io.Writer} given in the constructor will be closed.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to the output
	 */
	public final void writeRow(String[] row) {
		writeRow((Object[]) row);
	}


	/**
	 * Writes the data given for an individual record.
	 * <p> The output will remain open for further writing.
	 * <p> If the given data is null or empty, and {@link CommonSettings#getSkipEmptyLines()} is true, the input will be just ignored.
	 * <p> If {@link CommonSettings#getSkipEmptyLines()} is false, then an empty row will be written to the output (as specified by {@link AbstractWriter#writeEmptyRow()}).
	 * <p> In case of any errors, a {@link TextWritingException} will be thrown and the {@link java.io.Writer} given in the constructor will be closed.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to the output
	 */
	public final void writeRow(Object... row) {
		try {
			if (recordCount == 0 && isHeaderWritingEnabled && headers != null) {
				writeHeaders();
			}
			if (row == null || (row.length == 0 && !expandRows)) {
				if (skipEmptyLines) {
					return;
				} else {
					writeEmptyRow();
					return;
				}
			}

			row = adjustRowLength(row);
			submitRow(row);

			internalWriteRow();
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing row.", row, ex);
		}
	}

	protected Object[] expand(Object[] row, int length, String[] h2) {
		if (row.length < length) {
			return Arrays.copyOf(row, length);
		} else if (h2 != null && row.length < h2.length) {
			return Arrays.copyOf(row, h2.length);
		}

		if (length == -1 && h2 == null && row.length < largestRowLength) {
			return Arrays.copyOf(row, largestRowLength);
		}
		return row;
	}

	/**
	 * Writes a plain (potentially free-text) String as a line to the output.
	 * <p> A newline will automatically written after the given contents, unless this is a
	 * {@link com.univocity.parsers.fixed.FixedWidthWriter} whose
	 * {@link FixedWidthWriterSettings#getWriteLineSeparatorAfterRecord()} evaluates to {@code false}.
	 * The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The writer implementation has no control over the format of this content.
	 * <p> The output will remain open for further writing.
	 *
	 * @param row the line to be written to the output
	 */
	public final void writeRow(String row) {
		try {
			writer.write(row);
			if (enableNewlineAfterRecord) {
				writer.write(lineSeparator);
			}
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing row.", row, ex);
		}
	}

	/**
	 * Writes an empty line to the output, unless this is a {@link com.univocity.parsers.fixed.FixedWidthWriter} whose
	 * {@link FixedWidthWriterSettings#getWriteLineSeparatorAfterRecord()} evaluates to {@code false}.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The output will remain open for further writing.
	 */
	public final void writeEmptyRow() {
		try {
			if (enableNewlineAfterRecord) {
				writer.write(lineSeparator);
			}
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing empty row.", Arrays.toString(lineSeparator), ex);
		}
	}

	/**
	 * Writes a comment row to the output.
	 * <p> A newline will automatically written after the given contents, unless this is a
	 * {@link com.univocity.parsers.fixed.FixedWidthWriter} whose
	 * {@link FixedWidthWriterSettings#getWriteLineSeparatorAfterRecord()} evaluates to {@code false}.
	 * The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The output will remain open for further writing.
	 *
	 * @param comment the contents to be written as a comment to the output
	 */
	public final void commentRow(String comment) {
		writeRow(this.comment + comment);
	}

	/**
	 * Used when fields were selected and the input rows have a different order than the output. This method
	 * fills the internal #outputRow array with the values provided by the user in the correct order.
	 *
	 * @param row user-provided data which has to be rearranged to the expected record sequence before writing to the output.
	 */
	private <T> void fillOutputRow(T[] row) {
		if (columnReorderingEnabled) {
			for (int i = 0; i < indexesToWrite.length; i++) {
				if (indexesToWrite[i] < row.length) {
					outputRow[i] = row[indexesToWrite[i]];
				} else {
					outputRow[i] = null;
				}
			}
		} else {
			if (row.length > outputRow.length) {
				outputRow = row;
			} else if (row.length > indexesToWrite.length) {
				for (int i = 0; i < indexesToWrite.length; i++) {
					outputRow[indexesToWrite[i]] = row[indexesToWrite[i]];
				}
			} else {
				for (int i = 0; i < indexesToWrite.length && i < row.length; i++) {
					outputRow[indexesToWrite[i]] = row[i];
				}
			}
		}
	}

	/**
	 * Writes the accumulated value of a record to the output, followed by a newline, and increases the record count.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * The contents of {@link AbstractWriter#rowAppender} depend on the concrete implementation of {@link AbstractWriter#processRow(Object[])}
	 */
	private void internalWriteRow() {
		try {
			if (skipEmptyLines && rowAppender.length() == 0) {
				return;
			}
			if (enableNewlineAfterRecord) {
				rowAppender.appendNewLine();
			}
			rowAppender.writeCharsAndReset(writer);
			recordCount++;
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing row.", rowAppender.getAndReset(), ex);
		}
	}

	/**
	 * Identifies the starting character index of a value being written if leading whitespaces are to be discarded.
	 * <p><b>Implementation note</b> whitespaces are considered all characters where {@code ch <= ' '} evaluates to {@code true}
	 *
	 * @param whitespaceRangeStart starting range after which characters will be considered whitespace
	 * @param element              the String to be scanned for leading whitespaces.
	 *
	 * @return the index of the first non-whitespace character in the given element.
	 */
	protected static int skipLeadingWhitespace(int whitespaceRangeStart, String element) {
		if (element.isEmpty()) {
			return 0;
		}

		for (int i = 0; i < element.length(); i++) {
			char nextChar = element.charAt(i);
			if (!(nextChar <= ' ' && whitespaceRangeStart < nextChar)) {
				return i;
			}
		}
		return element.length();
	}

	/**
	 * Flushes the {@link java.io.Writer} given in this class constructor.
	 * <p> An IllegalStateException will be thrown in case of any errors, and the writer will be closed.
	 */
	public final void flush() {
		try {
			writer.flush();
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error flushing output.", rowAppender.getAndReset(), ex);
		}
	}

	/**
	 * Closes the {@link java.io.Writer} given in this class constructor.
	 * <p> An IllegalStateException will be thrown in case of any errors.
	 */
	public final void close() {
		try {
			this.headerIndexes = null;
			if (writer != null) {
				writer.close();
				writer = null;
			}
		} catch (Throwable ex) {
			throw new IllegalStateException("Error closing the output.", ex);
		}
		if (this.partialLineIndex != 0) {
			throw new TextWritingException("Not all values associated with the last record have been written to the output. " +
					"\n\tHint: use 'writeValuesToRow()' or 'writeValuesToString()' to flush the partially written values to a row.",
					recordCount, getContent(Arrays.copyOf(partialLine, partialLineIndex)));
		}
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 *
	 * @param message Description of the error
	 */
	private TextWritingException throwExceptionAndClose(String message) {
		return throwExceptionAndClose(message, (Object[]) null, null);
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 *
	 * @param message Description of the error
	 * @param cause   the exception to be wrapped by a {@link TextWritingException}
	 */
	private TextWritingException throwExceptionAndClose(String message, Throwable cause) {
		return throwExceptionAndClose(message, (Object[]) null, cause);
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 *
	 * @param message          Description of the error
	 * @param recordCharacters characters used to write to the output at the time the exception happened
	 * @param cause            the exception to be wrapped by a {@link TextWritingException}
	 */
	private TextWritingException throwExceptionAndClose(String message, String recordCharacters, Throwable cause) {
		try {
			if (cause instanceof NullPointerException && writer == null) {
				message = message + " No writer provided in the constructor of " + getClass().getName() + ". You can only use operations that write to Strings.";
			}
			throw new TextWritingException(message, recordCount, getContent(recordCharacters), cause);
		} finally {
			close();
		}
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 *
	 * @param message      Description of the error
	 * @param recordValues values used to write to the output at the time the exception happened
	 * @param cause        the exception to be wrapped by a {@link TextWritingException}
	 */
	private TextWritingException throwExceptionAndClose(String message, Object[] recordValues, Throwable cause) {
		try {
			throw new TextWritingException(message, recordCount, getContent(recordValues), cause);
		} finally {
			try {
				close();
			} catch (Throwable t) {
				//ignore and let original error go.
			}
		}
	}

	/**
	 * Converts a given object to its String representation for writing to a {@code String}
	 * <ul>
	 * <li>If the object is null, then {@link AbstractWriter#nullValue} is returned.</li>
	 * <li>If the String representation of this object is an empty String, then {@link AbstractWriter#emptyValue} is returned</li>
	 * </ul>
	 *
	 * @param element the object to be converted into a String.
	 *
	 * @return the String representation of the given object
	 */
	protected String getStringValue(Object element) {
		usingNullOrEmptyValue = false;
		if (element == null) {
			usingNullOrEmptyValue = true;
			return nullValue;
		}
		String string = String.valueOf(element);
		if (string.isEmpty()) {
			usingNullOrEmptyValue = true;
			return emptyValue;
		}
		return string;
	}

	/**
	 * Writes a sequence of values to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param values the values to be written
	 */
	public final void addValues(Object... values) {
		try {
			System.arraycopy(values, 0, partialLine, partialLineIndex, values.length);
			partialLineIndex += values.length;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error adding values to in-memory row", values, t);
		}
	}

	/**
	 * Writes a sequence of Strings to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param values the values to be written
	 */
	public final void addStringValues(Collection<String> values) {
		if (values != null) {
			try {
				for (String o : values) {
					partialLine[partialLineIndex++] = o;
				}
			} catch (Throwable t) {
				throw throwExceptionAndClose("Error adding values to in-memory row", values.toArray(), t);
			}
		}
	}

	/**
	 * Writes a sequence of values to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param values the values to be written
	 */
	public final void addValues(Collection<?> values) {
		if (values != null) {
			try {
				for (Object o : values) {
					partialLine[partialLineIndex++] = o;
				}
			} catch (Throwable t) {
				throw throwExceptionAndClose("Error adding values to in-memory row", values.toArray(), t);
			}
		}
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param value the value to be written
	 */
	public final void addValue(Object value) {
		try {
			partialLine[partialLineIndex++] = value;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error adding value to in-memory row", new Object[]{value}, t);
		}
	}

	private void fillPartialLineToMatchHeaders() {
		if (headers != null && partialLineIndex < headers.length) {
			while (partialLineIndex < headers.length) {
				partialLine[partialLineIndex++] = null;
			}
		}
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #addValues(Object...) or #writeValue()} to a new record in the output.
	 */
	public final void writeValuesToRow() {
		fillPartialLineToMatchHeaders();
		writeRow(Arrays.copyOf(partialLine, partialLineIndex));
		discardValues();
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param index the position in the row that should receive the value.
	 * @param value the value to be written
	 */
	public final void addValue(int index, Object value) {
		if (index >= partialLine.length) {
			throw throwExceptionAndClose("Cannot write '" + value + "' to index '" + index + "'. Maximum number of columns (" + partialLine.length + ") exceeded.", new Object[]{value}, null);
		}
		partialLine[index] = value;
		if (partialLineIndex <= index) {
			partialLineIndex = index + 1;
		}
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 *
	 * @param headerName the name of the column of the new row that should receive the value.
	 * @param value      the value to be written
	 */
	public final void addValue(String headerName, Object value) {
		addValue(getFieldIndex(headers, headerName, false), value);
	}

	private final void addValue(String[] headersInContext, String headerName, boolean ignoreOnMismatch, Object value) {
		int index = getFieldIndex(headersInContext, headerName, ignoreOnMismatch);
		if (index != -1) {
			addValue(index, value);
		}
	}

	/**
	 * Calculates the index of a header name in relation to the original {@link #headers} array defined in this writer
	 *
	 * @param headersInContext headers currently in use (they might change).
	 * @param headerName       the name of the header whose position will be identified
	 * @param ignoreOnMismatch flag indicating that if the header is not found, no exception is to be thrown, and -1 should be returned instead.
	 *
	 * @return the position of the given header, or -1 if it's not found when ignoreOnMismatch is set to {@code true}
	 */
	private int getFieldIndex(String[] headersInContext, String headerName, boolean ignoreOnMismatch) {
		if (headerIndexes == null) {
			headerIndexes = new HashMap<String[], Map<String, Integer>>();
		}

		Map<String, Integer> indexes = headerIndexes.get(headersInContext);
		if (indexes == null) {
			indexes = new HashMap<String, Integer>();
			headerIndexes.put(headersInContext, indexes);
		}

		Integer index = indexes.get(headerName);
		if (index == null) {
			if (headersInContext == null) {
				throw throwExceptionAndClose("Cannot calculate position of header '" + headerName + "' as no headers were defined.", null);
			}
			index = ArgumentUtils.indexOf(ArgumentUtils.normalize(headersInContext), ArgumentUtils.normalize(headerName));
			if (index == -1) {
				if (!ignoreOnMismatch) {
					throw throwExceptionAndClose("Header '" + headerName + "' could not be found. Defined headers are: " + Arrays.toString(headersInContext) + '.', null);
				}
			}
			indexes.put(headerName, index);
		}
		return index;
	}

	/**
	 * Discards the contents written to the internal in-memory row (using {@link #addValues(Object...) or #writeValue()}.
	 */
	public final void discardValues() {
		Arrays.fill(partialLine, 0, partialLineIndex, null);
		partialLineIndex = 0;
	}

	/**
	 * Writes the headers defined in {@link CommonSettings#getHeaders()} to a {@code String}
	 *
	 * @return a formatted {@code String} containing the headers defined in {@link CommonSettings#getHeaders()}
	 */
	public final String writeHeadersToString() {
		return writeHeadersToString(this.headers);
	}

	/**
	 * Writes the given collection of headers to a {@code String}
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined.
	 *
	 * @param headers the headers to write to a {@code String}
	 *
	 * @return a formatted {@code String} containing the given headers
	 */
	public final String writeHeadersToString(Collection<?> headers) {
		if (headers != null && headers.size() > 0) {
			return writeHeadersToString(headers.toArray(new String[headers.size()]));
		} else {
			throw throwExceptionAndClose("No headers defined");
		}
	}

	/**
	 * Writes the given collection of headers to a {@code String}
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to a {@code String}
	 *
	 * @param headers the headers to write to a {@code String}
	 *
	 * @return a formatted {@code String} containing the given headers
	 */
	public final String writeHeadersToString(String... headers) {
		if (headers != null && headers.length > 0) {
			writingHeaders = true;
			submitRow(headers);
			writingHeaders = false;
			this.headers = headers;
			return internalWriteRowToString();
		} else {
			throw throwExceptionAndClose("No headers defined.");
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them to a {@code List} of {@code String}.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param records the records to be transformed by a {@link RowWriterProcessor} and then written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the information transformed from the given records as formatted {@code String}s
	 */
	public final List<String> processRecordsToString(Iterable<?> records) {
		try {
			List<String> out = new ArrayList<String>(1000);
			for (Object record : records) {
				out.add(processRecordToString(record));
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Unable process input records", t);
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them them to a {@code List} of {@code String}.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param records the records to transformed by a {@link RowWriterProcessor} and then written a {@code String}.
	 *
	 * @return a {@code List} containing the information transformed from the given records as formatted {@code String}s
	 */
	public final List<String> processRecordsToString(Object[] records) {
		try {
			List<String> out = new ArrayList<String>(1000);
			for (Object record : records) {
				out.add(processRecordToString(record));
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Unable process input records", records, t);
		}
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it to a {@code String}.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to a {@code String}.
	 *
	 * @return a formatted {@code String} containing the information transformed from the given record
	 */
	public final String processRecordToString(Object... record) {
		return processRecordToString((Object) record);
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to a {@code String}.
	 *
	 * @return a formatted {@code String} containing the information transformed from the given record
	 */
	@SuppressWarnings("unchecked")
	public final String processRecordToString(Object record) {
		if (this.writerProcessor == null) {
			throw throwExceptionAndClose("Cannot process record '" + record + "' without a writer processor. Please define a writer processor instance in the settings or use the 'writeRow' methods.");
		}

		try {
			Object[] row = writerProcessor.write(record, getRowProcessorHeaders(), indexesToWrite);
			if (row != null) {
				return writeRowToString(row);
			}
		} catch (Throwable t) {
			throw throwExceptionAndClose("Could not process record '" + record + "'", t);
		}
		return null;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Object[])} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeRowsToString(Object[][] rows) {
		try {
			List<String> out = new ArrayList<String>(rows.length);
			for (Object[] row : rows) {
				String string = writeRowToString(row);
				if (string != null) {
					out.add(string);
				}
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input rows", t);
		}
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param <C>  Collection of objects containing values of a row
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final <C extends Collection<?>> List<String> writeRowsToString(Iterable<C> rows) {
		try {
			List<String> out = new ArrayList<String>(1000);
			for (Collection<?> row : rows) {
				out.add(writeRowToString(row));
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input rows", t);
		}
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param <C>  Collection of objects containing values of a row
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final <C extends Collection<?>> List<String> writeStringRowsToString(Iterable<C> rows) {
		try {
			List<String> out = new ArrayList<String>(1000);
			for (Collection<?> row : rows) {
				String string = writeRowToString(row);
				if (string != null) {
					out.add(string);
				}
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input rows", t);
		}
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeRowsToString(Collection<Object[]> rows) {
		try {
			List<String> out = new ArrayList<String>(rows.size());
			for (Object[] row : rows) {
				out.add(writeRowToString(row));
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input rows", t);
		}
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 *
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeStringRowsToString(Collection<String[]> rows) {
		try {
			List<String> out = new ArrayList<String>(rows.size());
			for (String[] row : rows) {
				out.add(writeRowToString(row));
			}
			return out;
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input rows", t);
		}
	}

	/**
	 * Writes the data given for an individual record to a {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to a {@code String}
	 *
	 * @return a formatted {@code String} containing the information of the given record
	 */
	public final String writeRowToString(Collection<?> row) {
		try {
			if (row == null) {
				return null;
			}
			return writeRowToString(row.toArray());
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error writing input row ", t);
		}
	}

	/**
	 * Writes the data given for an individual record to a {@code String}.
	 * <p> If the given data is null or empty, and {@link CommonSettings#getSkipEmptyLines()} is true, {@code null} will be returned
	 * <p> In case of any errors, a {@link TextWritingException} will be thrown.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to a {@code String}.
	 *
	 * @return a formatted {@code String} containing the information of the given record
	 */
	public final String writeRowToString(String[] row) {
		return writeRowToString((Object[]) row);
	}

	/**
	 * Writes the data given for an individual record to a {@code String}.
	 * <p> If the given data is null or empty, and {@link CommonSettings#getSkipEmptyLines()} is true, {@code null} will be returned
	 * <p> In case of any errors, a {@link TextWritingException} will be thrown.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to a {@code String}.
	 *
	 * @return a formatted {@code String} containing the information of the given record
	 */
	public final String writeRowToString(Object... row) {
		try {
			if (row == null || (row.length == 0 && !expandRows)) {
				if (skipEmptyLines) {
					return null;
				}
			}
			row = adjustRowLength(row);
			submitRow(row);

			return internalWriteRowToString();
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing row.", row, ex);
		}
	}


	private Object[] adjustRowLength(Object[] row) {
		if (outputRow != null) {
			fillOutputRow(row);
			row = outputRow;
		} else if (expandRows) {
			if (usingSwitch) {
				row = expand(row, dummyHeaderRow == null ? -1 : dummyHeaderRow.length, headers);
				dummyHeaderRow = null;
			} else {
				row = expand(row, headers == null ? -1 : headers.length, null);
			}
		}
		return row;
	}

	/**
	 * Writes a comment row to a {@code String}
	 *
	 * @param comment the contents to be written as a comment to a {@code String}.
	 *
	 * @return a formatted {@code String} containing the comment.
	 */
	public final String commentRowToString(String comment) {
		return writeRowToString(this.comment + comment);
	}

	/**
	 * Writes the accumulated value of a record to the output, followed by a newline, and increases the record count.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * The contents of {@link AbstractWriter#rowAppender} depend on the concrete implementation of {@link AbstractWriter#processRow(Object[])}
	 *
	 * @return a formatted {@code String} containing the comment.
	 */
	private String internalWriteRowToString() {
		if (skipEmptyLines && rowAppender.length() == 0) {
			return null;
		}
		String out = rowAppender.getAndReset();
		recordCount++;
		return out;
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #addValues(Object...) or #addValue()} as a {@code String}
	 *
	 * @return a formatted {@code String} containing the information accumulated in the internal in-memory row.
	 */
	public final String writeValuesToString() {
		fillPartialLineToMatchHeaders();
		String out = writeRowToString(Arrays.copyOf(partialLine, partialLineIndex));
		discardValues();
		return out;
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #addValues(Object...) or #addValue()} to a new record in the output.
	 * The objects added to this row will be processed with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 */
	public final void processValuesToRow() {
		fillPartialLineToMatchHeaders();
		processRecord(Arrays.copyOf(partialLine, partialLineIndex));
		discardValues();
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #addValues(Object...) or #addValue()} to a {@code String}
	 * The objects added to this row will be processed with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 * <p> The output will remain open for further writing.
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @return a formatted {@code String} containing the result produced by the {@link RowWriterProcessor} using the values accumulated in internal in-memory row.
	 */
	public final String processValuesToString() {
		fillPartialLineToMatchHeaders();
		String out = processRecordToString(Arrays.copyOf(partialLine, partialLineIndex));
		discardValues();
		return out;
	}

	/**
	 * Returns the number of records written to the output so far
	 *
	 * @return the number of records written to the output so far
	 */
	public final long getRecordCount() {
		return recordCount;
	}

	/**
	 * Writes values from an implementation of {@link java.util.Map} to a partial output record, ready to be
	 * written to the output.
	 *
	 * Values will be stored under a column identified by the headers. If no headers are defined,
	 * the keys of the map will be used to initialize an internal header row.
	 *
	 * A map of headers can be optionally provided to assign a name to the keys of the input map. This is useful when
	 * the input map has keys will generate unwanted header names.
	 *
	 * @param headerMapping an optional map associating keys of the rowData map with expected header names
	 * @param rowData       the data to be written. Its keys will be used to form a header row in case no headers are available.
	 * @param <K>           type of the key in both rowData and headerMapping maps.
	 */
	private <K> void writeValuesFromMap(Map<K, String> headerMapping, Map<K, ?> rowData) {
		try {
			if (rowData != null && !rowData.isEmpty()) {
				dummyHeaderRow = this.headers;
				if (usingSwitch) {
					dummyHeaderRow = ((RowWriterProcessorSwitch) writerProcessor).getHeaders(headerMapping, rowData);
					if (dummyHeaderRow == null) {
						dummyHeaderRow = this.headers;
					}
				}

				if (dummyHeaderRow != null) {
					if (headerMapping == null) {
						for (Map.Entry<?, ?> e : rowData.entrySet()) {
							addValue(dummyHeaderRow, String.valueOf(e.getKey()), true, e.getValue());
						}
					} else {
						for (Map.Entry<?, ?> e : rowData.entrySet()) {
							String header = headerMapping.get(e.getKey());
							if (header != null) {
								addValue(dummyHeaderRow, header, true, e.getValue());
							}
						}
					}
				} else if (headerMapping != null) {
					setHeadersFromMap(headerMapping, false);
					writeValuesFromMap(headerMapping, rowData);
				} else {
					setHeadersFromMap(rowData, true);
					writeValuesFromMap(null, rowData);
				}
			}
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error processing data from input map", t);
		}
	}

	/**
	 * Iterates over the keys of a map and builds an internal header row.
	 *
	 * @param map  the input map whose keys will be used to generate headers for the output.
	 * @param keys indicates whether to take the map keys or values to build the header rows.
	 */
	private void setHeadersFromMap(Map<?, ?> map, boolean keys) {
		this.headers = new String[map.size()];
		int i = 0;
		for (Object header : keys ? map.keySet() : map.values()) {
			headers[i++] = String.valueOf(header);
		}
	}

	/**
	 * Writes the values of a given map to a {@code String} formatted to according to the specified output format.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a {@code String}.
	 *
	 * @return a {@code String} containing the given data as a formatted {@code String}
	 */
	public final String writeRowToString(Map<?, ?> rowData) {
		return writeRowToString(null, (Map) rowData);
	}

	/**
	 * Writes the values of a given map into new output record
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a new record
	 */
	public final void writeRow(Map<?, ?> rowData) {
		writeRow(null, (Map) rowData);
	}


	/**
	 * Writes the values of a given map to a {@code String} formatted to according to the specified output format.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code String}.
	 * @param <K>           the key type
	 *
	 * @return a {@code String} containing the given data as a formatted {@code String}
	 */
	public final <K> String writeRowToString(Map<K, String> headerMapping, Map<K, ?> rowData) {
		writeValuesFromMap(headerMapping, rowData);
		return writeValuesToString();
	}

	/**
	 * Writes the values of a given map into new output record
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a new record
	 * @param <K>           the key type
	 */
	public final <K> void writeRow(Map<K, String> headerMapping, Map<K, ?> rowData) {
		writeValuesFromMap(headerMapping, rowData);
		writeValuesToRow();
	}

	/**
	 * Writes the values of a given map to a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K, I extends Iterable<?>> List<String> writeRowsToString(Map<K, I> rowData) {
		return writeRowsToString(null, rowData);
	}

	/**
	 * Writes the values of a given map to multiple output records
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 */
	public final <K, I extends Iterable<?>> void writeRows(Map<K, I> rowData) {
		writeRows(null, rowData, null, false);
	}

	/**
	 * Writes the values of a given map to a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K, I extends Iterable<?>> List<String> writeRowsToString(Map<K, String> headerMapping, Map<K, I> rowData) {
		List<String> writtenRows = new ArrayList<String>();
		writeRows(headerMapping, rowData, writtenRows, false);
		return writtenRows;
	}

	/**
	 * Writes the values of a given map to multiple output records
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 */
	public final <K, I extends Iterable<?>> void writeRows(Map<K, String> headerMapping, Map<K, I> rowData) {
		writeRows(headerMapping, rowData, null, false);
	}

	/**
	 * Writes the values of a given map to a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param outputList    an output {@code List} to fill with formatted {@code String}s, each {@code String} representing
	 *                      one successful iteration over at least one
	 *                      element of the iterators in the map.
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 */
	private <K, I extends Iterable<?>> void writeRows(Map<K, String> headerMapping, Map<K, I> rowData, List<String> outputList, boolean useRowProcessor) {
		try {
			Iterator[] iterators = new Iterator[rowData.size()];
			Object[] keys = new Object[rowData.size()];
			final Map<Object, Object> rowValues = new LinkedHashMap<Object, Object>(rowData.size());

			int length = 0;
			for (Map.Entry<K, I> rowEntry : rowData.entrySet()) {
				iterators[length] = rowEntry.getValue() == null ? null : rowEntry.getValue().iterator();
				keys[length] = rowEntry.getKey();
				rowValues.put(rowEntry.getKey(), null);
				length++;
			}
			boolean nullsOnly;

			do {
				nullsOnly = true;
				for (int i = 0; i < length; i++) {
					Iterator<?> iterator = iterators[i];
					boolean isNull = iterator == null || !iterator.hasNext();
					nullsOnly &= isNull;
					if (isNull) {
						rowValues.put(keys[i], null);
					} else {
						rowValues.put(keys[i], iterator.next());
					}
				}
				if (!nullsOnly) {
					if (outputList == null) {
						if (useRowProcessor) {
							processRecord((Map) headerMapping, (Map) rowValues);
						} else {
							writeRow((Map) headerMapping, (Map) rowValues);
						}
					} else {
						if (useRowProcessor) {
							outputList.add(processRecordToString((Map) headerMapping, (Map) rowValues));
						} else {
							outputList.add(writeRowToString((Map) headerMapping, (Map) rowValues));
						}
					}
				}
			} while (!nullsOnly);
		} catch (Throwable t) {
			throw throwExceptionAndClose("Error processing input rows from map", t);
		}
	}

	/**
	 * Writes the values of a given map to a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K> List<String> writeStringRowsToString(Map<K, String> headerMapping, Map<K, String[]> rowData) {
		List<String> writtenRows = new ArrayList<String>();
		writeRows(headerMapping, wrapStringArray(rowData), writtenRows, false);
		return writtenRows;
	}

	/**
	 * Writes the values of a given map to multiple output records
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void writeStringRows(Map<K, String> headerMapping, Map<K, String[]> rowData) {
		writeRows(headerMapping, wrapStringArray(rowData), null, false);
	}

	/**
	 * Writes the values of a given map to a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K> List<String> writeObjectRowsToString(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		List<String> writtenRows = new ArrayList<String>();
		writeRows(headerMapping, wrapObjectArray(rowData), writtenRows, false);
		return writtenRows;
	}

	/**
	 * Writes the values of a given map to multiple output records
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void writeObjectRows(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		writeRows(headerMapping, wrapObjectArray(rowData), null, false);
	}

	private <K> Map<K, Iterable<Object>> wrapObjectArray(Map<K, Object[]> rowData) {
		Map<K, Iterable<Object>> out = new LinkedHashMap<K, Iterable<Object>>(rowData.size());
		for (Map.Entry<K, Object[]> e : rowData.entrySet()) {
			if (e.getValue() == null) {
				out.put(e.getKey(), Collections.emptyList());
			} else {
				out.put(e.getKey(), Arrays.asList(e.getValue()));
			}
		}
		return out;
	}

	private <K> Map<K, Iterable<String>> wrapStringArray(Map<K, String[]> rowData) {
		Map<K, Iterable<String>> out = new LinkedHashMap<K, Iterable<String>>(rowData.size());
		for (Map.Entry<K, String[]> e : rowData.entrySet()) {
			if (e.getValue() == null) {
				out.put(e.getKey(), Collections.<String>emptyList());
			} else {
				out.put(e.getKey(), Arrays.asList(e.getValue()));
			}
		}
		return out;
	}


	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void writeObjectRowsAndClose(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		try {
			writeObjectRows(headerMapping, rowData);
		} finally {
			close();
		}
	}

	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void writeStringRowsAndClose(Map<K, String> headerMapping, Map<K, String[]> rowData) {
		try {
			writeStringRows(headerMapping, rowData);
		} finally {
			close();
		}
	}

	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 */
	public final <K> void writeObjectRowsAndClose(Map<K, Object[]> rowData) {
		writeObjectRowsAndClose(null, rowData);
	}

	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 */
	public final <K> void writeStringRowsAndClose(Map<K, String[]> rowData) {
		writeStringRowsAndClose(null, rowData);
	}

	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 */
	public final <K, I extends Iterable<?>> void writeRowsAndClose(Map<K, String> headerMapping, Map<K, I> rowData) {
		try {
			writeRows(headerMapping, rowData);
		} finally {
			close();
		}
	}


	/**
	 * Writes the values of a given map to multiple output records and closes the output when finished.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}.
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 */
	public final <K, I extends Iterable<?>> void writeRowsAndClose(Map<K, I> rowData) {
		writeRowsAndClose(null, rowData);
	}

	/**
	 * Processes the values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into {@code String} formatted according to the specified output format.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a {@code List} of {@code String}.
	 *
	 * @return a {@code String} containing the given data as a formatted {@code String}
	 */
	public final String processRecordToString(Map<?, ?> rowData) {
		return processRecordToString(null, (Map) rowData);
	}

	/**
	 * Processes the values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a new output record
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a {@code List} of {@code String}.
	 */
	public final void processRecord(Map<?, ?> rowData) {
		processRecord(null, (Map) rowData);
	}


	/**
	 * Processes the values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into {@code String} formatted according to the specified output format.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 *
	 * @return a {@code String} containing the given data as a formatted {@code String}
	 */
	public final <K> String processRecordToString(Map<K, String> headerMapping, Map<K, ?> rowData) {
		writeValuesFromMap(headerMapping, rowData);
		return processValuesToString();
	}

	/**
	 * Processes the values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a new output record
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 */
	public final <K> void processRecord(Map<K, String> headerMapping, Map<K, ?> rowData) {
		writeValuesFromMap(headerMapping, rowData);
		processValuesToRow();
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K, I extends Iterable<?>> List<String> processRecordsToString(Map<K, I> rowData) {
		return processRecordsToString(null, rowData);
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output .
	 *
	 * The output will remain open for further write operations.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 */
	public final <K, I extends Iterable<?>> void processRecords(Map<K, I> rowData) {
		writeRows(null, rowData, null, true);
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K, I extends Iterable<?>> List<String> processRecordsToString(Map<K, String> headerMapping, Map<K, I> rowData) {
		List<String> writtenRows = new ArrayList<String>();
		writeRows(headerMapping, rowData, writtenRows, true);
		return writtenRows;
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output .
	 *
	 * The output will remain open for further write operations.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 */
	public final <K, I extends Iterable<?>> void processRecords(Map<K, String> headerMapping, Map<K, I> rowData) {
		writeRows(headerMapping, rowData, null, true);
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>     the key type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K> List<String> processObjectRecordsToString(Map<K, Object[]> rowData) {
		return processObjectRecordsToString(null, rowData);
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}
	 * and writes the result into a {@code List} of {@code String} formatted to according to the specified output format.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a {@code List} of {@code String}.
	 * @param <K>           the key type
	 *
	 * @return a {@code List} of formatted {@code String}, each {@code String} representing one successful iteration over at least one
	 * element of the iterators in the map.
	 */
	public final <K> List<String> processObjectRecordsToString(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		List<String> writtenRows = new ArrayList<String>();
		writeRows(headerMapping, wrapObjectArray(rowData), writtenRows, true);
		return writtenRows;
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output .
	 *
	 * The output will remain open for further write operations.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void processObjectRecords(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		writeRows(headerMapping, wrapObjectArray(rowData), null, true);
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output  and closes the writer.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 */
	public final <K> void processObjectRecordsAndClose(Map<K, String> headerMapping, Map<K, Object[]> rowData) {
		try {
			processObjectRecords(headerMapping, rowData);
		} finally {
			close();
		}
	}

	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output  and closes the writer.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 */
	public final <K> void processObjectRecordsAndClose(Map<K, Object[]> rowData) {
		processRecordsAndClose(null, wrapObjectArray(rowData));
	}


	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output  and closes the writer.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param headerMapping a mapping associating the keys of the input map to their corresponding header names.
	 * @param rowData       the map whose values will be used to generate a number of output records
	 * @param <K>           the key type
	 * @param <I>           the iterable type
	 */
	public final <K, I extends Iterable<?>> void processRecordsAndClose(Map<K, String> headerMapping, Map<K, I> rowData) {
		try {
			processRecords(headerMapping, rowData);
		} finally {
			close();
		}
	}


	/**
	 * Processes the data in all values of a map using the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()},
	 * then writes all values to the output  and closes the writer.
	 *
	 * Each value is expected to be iterable and the result of this method will produce the number of records equal to the longest iterable.
	 *
	 * A new record will be created each time at least one {@link Iterator#hasNext()} returns {@code true}. {@code Null} will be written
	 * when a iterator has been fully read.
	 *
	 * <p> A {@link TextWritingException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.</p>
	 *
	 * @param rowData the map whose values will be used to generate a number of output records
	 * @param <K>     the key type
	 * @param <I>     the iterable type
	 */
	public final <K, I extends Iterable<?>> void processRecordsAndClose(Map<K, I> rowData) {
		processRecordsAndClose(null, rowData);
	}

	private Object[] getContent(Object[] tmp) {
		return AbstractException.restrictContent(errorContentLength, tmp);
	}

	private String getContent(CharSequence tmp) {
		return AbstractException.restrictContent(errorContentLength, tmp);
	}
}