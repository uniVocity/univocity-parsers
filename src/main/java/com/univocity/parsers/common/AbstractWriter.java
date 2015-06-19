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

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.processor.*;

/**
 * The AbstractWriter class provides a common ground for all writers in uniVocity-parsers.
 *
 * It handles all settings defined by {@link CommonWriterSettings}, and delegates the writing algorithm implementation to its subclasses through the abstract method {@link AbstractWriter#processRow(Object[])}
 *
 * <p> The following (absolutely required) attributes are exposed to subclasses:
 * <ul>
 * 	<li><b>appender (<i>{@link WriterCharAppender}</i>):</b> the character writer that appends characters from a given input into an internal buffer</li>
 * </ul>
 *
 * @see com.univocity.parsers.csv.CsvWriter
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriter
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 * @see com.univocity.parsers.common.input.WriterCharAppender
 * @see com.univocity.parsers.common.processor.RowWriterProcessor
 *
 * @param <S> The specific writer settings configuration class, which can potentially provide additional configuration options supported by the writer implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class AbstractWriter<S extends CommonWriterSettings<?>> {

	@SuppressWarnings("rawtypes")
	private final RowWriterProcessor writerProcessor;

	private final BufferedWriter writer;
	private final boolean skipEmptyLines;
	private final char comment;
	private final WriterCharAppender rowAppender;
	private final boolean isHeaderWritingEnabled;

	private Object[] outputRow;
	private int[] indexesToWrite;
	private final char[] lineSeparator;

	private String[] headers;
	private long recordCount = 0;

	protected final String nullValue;
	protected final String emptyValue;
	protected final CharAppender appender;

	private final Object[] partialLine;
	private int partialLineIndex = 0;
	private Map<String, Integer> headerIndexes;
	private int largestRowLength = -1;

	private String[] dummyHeaderRow;
	private final int maxColumns;

	private final CommonSettings<DummyFormat> internalSettings = new CommonSettings<DummyFormat>() {
		@Override
		protected DummyFormat createDefaultFormat() {
			return DummyFormat.instance;
		}
	};

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 * @param settings the writer configuration
	 */
	public AbstractWriter(S settings) {
		this((Writer)null, settings);
	}


	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param file the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, S settings) {
		this(ArgumentUtils.newWriter(file), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param file the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the file
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, String encoding, S settings) {
		this(ArgumentUtils.newWriter(file, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param file the output file that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the file
	 * @param settings the writer configuration
	 */
	public AbstractWriter(File file, Charset encoding, S settings) {
		this(ArgumentUtils.newWriter(file, encoding), settings);
	}


	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param output the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, S settings) {
		this(ArgumentUtils.newWriter(output), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param output the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the stream
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, String encoding, S settings) {
		this(ArgumentUtils.newWriter(output, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param output the output stream that will be written with the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param encoding the encoding of the stream
	 * @param settings the writer configuration
	 */
	public AbstractWriter(OutputStream output, Charset encoding, S settings) {
		this(ArgumentUtils.newWriter(output, encoding), settings);
	}

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param writer the output resource that will receive the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the writer configuration
	 */
	public AbstractWriter(Writer writer, S settings) {
		settings.autoConfigure();
		internalSettings.setMaxColumns(settings.getMaxColumns());
		this.nullValue = settings.getNullValue();
		this.emptyValue = settings.getEmptyValue();

		this.lineSeparator = settings.getFormat().getLineSeparator();
		this.comment = settings.getFormat().getComment();
		this.skipEmptyLines = settings.getSkipEmptyLines();
		this.writerProcessor = settings.getRowWriterProcessor();
		this.maxColumns = settings.getMaxColumns();

		this.appender = new WriterCharAppender(settings.getMaxCharsPerColumn(), "", settings.getFormat());
		this.rowAppender = new WriterCharAppender(settings.getMaxCharsPerColumn() * settings.getMaxColumns(), "", settings.getFormat());

		if (writer != null) {
			if (writer instanceof BufferedWriter) {
				this.writer = (BufferedWriter) writer;
			} else {
				this.writer = new BufferedWriter(writer);
			}
		} else {
			this.writer = null;
		}

		this.headers = settings.getHeaders();

		updateIndexesToWrite(settings);

		this.partialLine = new Object[settings.getMaxColumns()];
		this.isHeaderWritingEnabled = settings.isHeaderWritingEnabled();

		if (writerProcessor instanceof ConversionProcessor) {
			ConversionProcessor conversionProcessor = (ConversionProcessor) writerProcessor;
			conversionProcessor.context = null;
			conversionProcessor.errorHandler = settings.getRowProcessorErrorHandler();
		}

		initialize(settings);
	}

	/**
	 * Initializes the concrete implementation of this class with format-specific settings.
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
				outputRow = new Object[headers.length];
				indexesToWrite = selector.getFieldIndexes(headers);
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
				outputRow = new Object[rowLength];
				indexesToWrite = selector.getFieldIndexes(new String[rowLength]); //generates a dummy header array - only the indexes matter so we are good
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
	 * Format-specific implementation for writing a single record into the output.
	 *
	 * <p> The AbstractWriter handles the initialization and processing of the output until it is ready to be written (generally, reorganizing it and passing it on to a {@link RowWriterProcessor}).
	 * <p> It then delegates the record to the writer-specific implementation defined by {@link AbstractWriter#processRow(Object[])}. In general, an implementation of {@link AbstractWriter#processRow(Object[])} will perform the following steps:
	 * <ul>
	 * 	<li>Iterate over each object in the given input and convert it to the expected String representation.</li>
	 *  <li>The conversion <b>must</b> happen using the provided {@link AbstractWriter#appender} object. The an individual value is processed, the {@link AbstractWriter#appendValueToRow()} method must be called. This will clear the accumulated value in {@link AbstractWriter#appender} and add it to the output row.</li>
	 *  <li>Format specific separators and other characters must be introduced to the output row using {@link AbstractWriter#appendToRow(char)}</li>
	 * </ul>
	 * <p> Once the {@link AbstractWriter#processRow(Object[])} method returns, a row will be written to the output with the processed information, and a newline will be automatically written after the given contents. The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> This cycle repeats until the writing process is stopped by the user or an error happens.
	 * <p> In case of errors, the unchecked exception {@link TextWritingException} will be thrown and all resources in use will be closed automatically. The exception should contain the cause and more information about the output state when the error happened.
	 *
	 * @see com.univocity.parsers.common.input.CharAppender
	 * @see com.univocity.parsers.common.CommonWriterSettings
	 *
	 * @param row the data to be written to the output in the expected format.
	 */
	protected abstract void processRow(Object[] row);

	/**
	 * Submits a row for processing by the format-specific implementation.
	 * @param row the data to be written for a single record in the output.
	 */
	private void submitRow(Object[] row) {
		if (largestRowLength < row.length) {
			largestRowLength = row.length;
		}
		processRow(row);
	}

	/**
	 * Appends the processed sequence of characters in {@link AbstractWriter#appender} to the output row.
	 */
	protected final void appendValueToRow() {
		rowAppender.append((WriterCharAppender) appender);
	}

	/**
	 * Appends the given character to the output row.
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
	 * @param headers the headers to write to the output.
	 */
	public final void writeHeaders(Collection<String> headers) {
		if (headers != null && headers.size() > 0) {
			writeHeaders(headers.toArray(new String[headers.size()]));
		} else {
			throwExceptionAndClose("No headers defined.", (Object[]) null, null);
		}
	}

	/**
	 * Writes the given collection of headers to the output.
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to the output.
	 * @param headers the headers to write to the output.
	 */
	public final void writeHeaders(String... headers) {
		if (recordCount > 0) {
			throwExceptionAndClose("Cannot write headers after records have been written.", headers, null);
		}
		if (headers != null && headers.length > 0) {
			submitRow(headers);
			this.headers = headers;
			writeRow();
		} else {
			throwExceptionAndClose("No headers defined.", headers, null);
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them, then finally and closes the output
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
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
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
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
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
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
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} was provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
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
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	public final void processRecord(Object... record) {
		processRecord((Object) record);
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it.
	 * <p> The output will remain open for further writing.
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to the output
	 */
	@SuppressWarnings("unchecked")
	public final void processRecord(Object record) {
		if (this.writerProcessor == null) {
			try {
				throw new IllegalStateException("Cannot process record '" + record + "' without a writer processor. Please define a writer processor instance in the settings or use the 'writeRow' methods.");
			} finally {
				close();
			}
		}

		Object[] row = writerProcessor.write(record, getRowProcessorHeaders(), indexesToWrite);
		if (row != null) {
			writeRow(row);
		}
	}

	private String[] getRowProcessorHeaders(){
		if(headers == null && indexesToWrite == null){
			if(dummyHeaderRow == null) {
				dummyHeaderRow = new String[maxColumns];
			}
			return dummyHeaderRow;
		}
		return headers;
	}

	/**
	 * Iterates over all records, writes them and closes the output.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecordsAndClose(Iterable)} for that.
	 * @param <C> Collection of objects containing values of a row
	 *
	 * @param allRows the rows to be written to the output
	 */
	public final <C extends Collection<Object>> void writeRowsAndClose(Iterable<C> allRows) {
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
	 * @param <C> Collection of objects containing values of a row
	 *
	 * @param rows the rows to be written to the output
	 */
	public final <C extends Collection<Object>> void writeRows(Iterable<C> rows) {
		for (Collection<Object> row : rows) {
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
	 * @param <C> Collection of objects containing values of a row
	 *
	 * @param rows the rows to be written to the output
	 */
	public final <C extends Collection<String>> void writeStringRows(Iterable<C> rows) {
		for (Collection<String> row : rows) {
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
	public final void writeRow(Collection<Object> row) {
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
			if (row == null || row.length == 0) {
				if (skipEmptyLines) {
					return;
				} else {
					writeEmptyRow();
					return;
				}
			}

			if (outputRow != null) {
				fillOutputRow(row);
				row = outputRow;
			}

			submitRow(row);

			writeRow();
		} catch (Throwable ex) {
			throwExceptionAndClose("Error writing row.", row, ex);
		}
	}

	/**
	 * Writes a plain (potentially free-text) String as a line to the output.
	 * <p> A newline will automatically written after the given contents. The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The writer implementation has no control over the format of this content.
	 * <p> The output will remain open for further writing.
	 *
	 * @param row the line to be written to the output
	 */
	public final void writeRow(String row) {
		try {
			writer.write(row);
			writer.write(lineSeparator);
		} catch (Throwable ex) {
			throwExceptionAndClose("Error writing row.", row, ex);
		}
	}

	/**
	 * Writes an empty line to the output.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The output will remain open for further writing.
	 */
	public final void writeEmptyRow() {
		try {
			writer.write(lineSeparator);
		} catch (Throwable ex) {
			throwExceptionAndClose("Error writing empty row.", Arrays.toString(lineSeparator), ex);
		}
	}

	/**
	 * Writes a comment row to the output.
	 * <p> A newline will automatically written after the given contents. The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
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
		if (row.length > indexesToWrite.length) {
			String msg = "Cannot write row as it contains more elements than the number of selected fields (" + this.indexesToWrite.length + " fields selected but row has " + row.length + " elements).";
			throwExceptionAndClose(msg, headers, null);
		}

		for (int i = 0; i < indexesToWrite.length && i < row.length; i++) {
			outputRow[indexesToWrite[i]] = row[i];
		}
	}

	/**
	 * Writes the accumulated value of a record to the output, followed by a newline, and increases the record count.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * The contents of {@link AbstractWriter#rowAppender} depend on the concrete implementation of {@link AbstractWriter#processRow(Object[])}
	 */
	private void writeRow() {
		try {
			rowAppender.appendNewLine();
			rowAppender.writeCharsAndReset(writer);
			recordCount++;
		} catch (Throwable ex) {
			throwExceptionAndClose("Error writing row.", rowAppender.getAndReset(), ex);
		}
	}

	/**
	 * Identifies the starting character index of a value being written if leading whitespaces are to be discarded.
	 * <p><b>Implementation note</b> whitespaces are considered all characters where {@code ch <= ' '} evaluates to {@code true}
	 * @param element the String to be scanned for leading whitespaces.
	 * @return the index of the first non-whitespace character in the given element.
	 */
	protected static int skipLeadingWhitespace(String element) {
		if (element.isEmpty()) {
			return 0;
		}

		for (int i = 0; i < element.length(); i++) {
			char nextChar = element.charAt(i);
			if (!(nextChar <= ' ')) {
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
			throwExceptionAndClose("Error flushing output.", rowAppender.getAndReset(), ex);
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
				try {
					writer.flush();
				} finally {
					writer.close();
				}
			}
		} catch (Throwable ex) {
			throw new IllegalStateException("Error closing the output.", ex);
		}
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 * @param message Description of the error
	 * @param recordCharacters characters used to write to the output at the time the exception happened
	 * @param cause the exception to be wrapped by a {@link TextWritingException}
	 */
	private TextWritingException throwExceptionAndClose(String message, String recordCharacters, Throwable cause) {
		try {
			if (cause instanceof NullPointerException && writer == null) {
				message = message + " No writer provided in the constructor of " + getClass().getName() + ". You can only use operations that write to Strings.";
			}
			throw new TextWritingException(message, recordCount, recordCharacters, cause);
		} finally {
			close();
		}
	}

	/**
	 * In case of any exceptions, a {@link TextWritingException} is thrown, and the output {@link java.io.Writer} is closed.
	 * @param message Description of the error
	 * @param recordValues values used to write to the output at the time the exception happened
	 * @param cause the exception to be wrapped by a {@link TextWritingException}
	 */
	private TextWritingException throwExceptionAndClose(String message, Object[] recordValues, Throwable cause) {
		try {
			throw new TextWritingException(message, recordCount, recordValues, cause);
		} finally {
			close();
		}
	}

	/**
	 * Converts a given object to its String representation for writing to a {@code String}
	 * <ul>
	 * 	<li>If the object is null, then {@link AbstractWriter#nullValue} is returned.</li>
	 *  <li>If the String representation of this object is an empty String, then {@link AbstractWriter#emptyValue} is returned</li>
	 * </ul>
	 *
	 * @param element the object to be converted into a String.
	 * @return the String representation of the given object
	 */
	protected String getStringValue(Object element) {
		if (element == null) {
			element = nullValue;
			if (element == null) {
				return null;
			}
		}
		String string = String.valueOf(element);
		if (string.isEmpty()) {
			return emptyValue;
		}
		return string;
	}

	/**
	 * Writes as sequence of values to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 * @param values the values to be written
	 */
	public final void writeValues(Object... values) {
		System.arraycopy(values, 0, partialLine, partialLineIndex, values.length);
		partialLineIndex += values.length;
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 * @param value the value to be written
	 */
	public final void writeValue(Object value) {
		partialLine[partialLineIndex++] = value;
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #writeValues(Object...) or #writeValue()} to a new record in the output.
	 */
	public final void writeValuesToRow() {
		writeRow(Arrays.copyOf(partialLine, partialLineIndex + 1));
		partialLineIndex = 0;
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 * @param index the position in the row that should receive the value.
	 * @param value the value to be written
	 */
	public final void writeValue(int index, Object value) {
		partialLine[index] = value;
		if (partialLineIndex < index) {
			partialLineIndex = index;
		}
	}

	/**
	 * Writes a value to a row in memory. Subsequent calls to this method will add the given values in a new column of the same row, until {@link #writeValuesToRow} is called to flush
	 * all values accumulated and effectively write a new record to the output
	 * @param headerName the name of the column of the new row that should receive the value.
	 * @param value the value to be written
	 */
	public final void writeValue(String headerName, Object value) {
		writeValue(getFieldIndex(headerName), value);
	}

	/**
	 * Calculates the index of a header name in relation to the original {@link #headers} array defined in this writer
	 * @param headerName the name of the header whose position will be identified
	 * @return the position of the given header
	 */
	private int getFieldIndex(String headerName) {
		if (headerIndexes == null) {
			headerIndexes = new HashMap<String, Integer>();
		}
		Integer index = headerIndexes.get(headerName);
		if (index == null) {
			if (headers == null) {
				throw new IllegalArgumentException("Cannot calculate position of header '" + headerName + "' as no headers were defined.");
			}
			index = ArgumentUtils.indexOf(ArgumentUtils.normalize(headers), ArgumentUtils.normalize(headerName));
			if (index == -1) {
				throw new IllegalArgumentException("Header '" + headerName + "' could not be found. Defined headers are: " + Arrays.toString(headers) + '.');
			}
			headerIndexes.put(headerName, index);
		}
		return index;
	}

	/**
	 * Discards the contents written to the internal in-memory row (using {@link #writeValues(Object...) or #writeValue()}.
	 */
	public final void discardValues() {
		partialLineIndex = 0;
	}

	/**
	 * Writes the headers defined in {@link CommonSettings#getHeaders()} to a {@code String}
	 * @return a formatted {@code String} containing the headers defined in {@link CommonSettings#getHeaders()}
	 */
	public final String writeHeadersToString() {
		return writeHeadersToString(this.headers);
	}

	/**
	 * Writes the given collection of headers to a {@code String}
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined.
	 * @param headers the headers to write to a {@code String}
	 * @return a formatted {@code String} containing the given headers
	 */
	public final String writeHeadersToString(Collection<String> headers) {
		if (headers != null && headers.size() > 0) {
			return writeHeadersToString(headers.toArray(new String[headers.size()]));
		} else {
			throw throwExceptionAndClose("No headers defined", (Object[]) null, null);
		}
	}

	/**
	 * Writes the given collection of headers to a {@code String}
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to a {@code String}
	 * @param headers the headers to write to a {@code String}
	 * @return a formatted {@code String} containing the given headers
	 */
	public final String writeHeadersToString(String... headers) {
		if (headers != null && headers.length > 0) {
			submitRow(headers);
			this.headers = headers;
			return writeRowToString();
		} else {
			throw throwExceptionAndClose("No headers defined.", headers, null);
		}
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them to a {@code List} of {@code String}.
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param records the records to be transformed by a {@link RowWriterProcessor} and then written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the information transformed from the given records as formatted {@code String}s
	 */
	public final List<String> processRecordsToString(Iterable<?> records) {
		List<String> out = new ArrayList<String>(1000);
		for (Object record : records) {
			out.add(processRecordToString(record));
		}
		return out;
	}

	/**
	 * Iterates over all records, processes each one with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, and writes them them to a {@code List} of {@code String}.
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} was provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param records the records to transformed by a {@link RowWriterProcessor} and then written a {@code String}.
	 * @return a {@code List} containing the information transformed from the given records as formatted {@code String}s
	 */
	public final List<String> processRecordsToString(Object[] records) {
		List<String> out = new ArrayList<String>(1000);
		for (Object record : records) {
			out.add(processRecordToString(record));
		}
		return out;
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it to a {@code String}.
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to a {@code String}.
	 * @return a formatted {@code String} containing the information transformed from the given record
	 */
	public final String processRecordToString(Object... record) {
		return processRecordToString((Object) record);
	}

	/**
	 * Processes the data given for an individual record with the {@link RowWriterProcessor} provided by {@link CommonWriterSettings#getRowWriterProcessor()}, then writes it.
	 * <p> The output will remain open for further writing.
	 * <p> An {@link IllegalStateException} will be thrown if no {@link RowWriterProcessor} is provided by {@link CommonWriterSettings#getRowWriterProcessor()}.
	 *
	 * @param record the information of a single record to be transformed by a {@link RowWriterProcessor} and then written to a {@code String}.
	 * @return a formatted {@code String} containing the information transformed from the given record
	 */
	@SuppressWarnings("unchecked")
	public final String processRecordToString(Object record) {
		if (this.writerProcessor == null) {
			try {
				throw new IllegalStateException("Cannot process record '" + record + "' without a writer processor. Please define a writer processor instance in the settings or use the 'writeRow' methods.");
			} finally {
				close();
			}
		}

		Object[] row = writerProcessor.write(record, getRowProcessorHeaders(), indexesToWrite);
		if (row != null) {
			return writeRowToString(row);
		}
		return null;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Object[])} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeRowsToString(Object[][] rows) {
		List<String> out = new ArrayList<String>(rows.length);
		for (Object[] row : rows) {
			out.add(writeRowToString(row));
		}
		return out;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 * @param <C> Collection of objects containing values of a row
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final <C extends Collection<Object>> List<String> writeRowsToString(Iterable<C> rows) {
		List<String> out = new ArrayList<String>(1000);
		for (Collection<Object> row : rows) {
			out.add(writeRowToString(row));
		}
		return out;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 * @param <C> Collection of objects containing values of a row
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final <C extends Collection<String>> List<String> writeStringRowsToString(Iterable<C> rows) {
		List<String> out = new ArrayList<String>(1000);
		for (Collection<String> row : rows) {
			out.add(writeRowToString(row));
		}
		return out;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeRowsToString(Collection<Object[]> rows) {
		List<String> out = new ArrayList<String>(rows.size());
		for (Object[] row : rows) {
			out.add(writeRowToString(row));
		}
		return out;
	}

	/**
	 * Iterates over all records and writes them to a {@code List} of {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecords(Iterable)} for that.
	 *
	 * @param rows the rows to be written to a {@code List} of {@code String}.
	 * @return a {@code List} containing the given rows as formatted {@code String}s
	 */
	public final List<String> writeStringRowsToString(Collection<String[]> rows) {
		List<String> out = new ArrayList<String>(rows.size());
		for (String[] row : rows) {
			out.add(writeRowToString(row));
		}
		return out;
	}

	/**
	 * Writes the data given for an individual record to a {@code String}.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to a {@code String}
	 * @return a formatted {@code String} containing the information of the given record
	 *
	 */
	public final String writeRowToString(Collection<Object> row) {
		if (row == null) {
			return null;
		}
		return writeRowToString(row.toArray());
	}

	/**
	 * Writes the data given for an individual record to a {@code String}.
	 * <p> If the given data is null or empty, and {@link CommonSettings#getSkipEmptyLines()} is true, {@code null} will be returned
	 * <p> In case of any errors, a {@link TextWritingException} will be thrown.
	 * <p><b>Note</b> this method will not use the {@link RowWriterProcessor}. Use {@link AbstractWriter#processRecord(Object)} for that.
	 *
	 * @param row the information of a single record to be written to a {@code String}.
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
	 * @return a formatted {@code String} containing the information of the given record
	 */
	public final String writeRowToString(Object... row) {
		try {
			if (row == null || row.length == 0) {
				return null;
			}

			if (outputRow != null) {
				fillOutputRow(row);
				row = outputRow;
			}

			submitRow(row);

			return writeRowToString();
		} catch (Throwable ex) {
			throw throwExceptionAndClose("Error writing row.", row, ex);
		}
	}

	/**
	 * Writes a comment row to a {@code String}
	 *
	 * @param comment the contents to be written as a comment to a {@code String}.
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
	private String writeRowToString() {
		String out = rowAppender.getAndReset();
		recordCount++;
		return out;
	}

	/**
	 * Writes the contents accumulated in an internal in-memory row (using {@link #writeValues(Object...) or #writeValue()} as a {@code String}
	 * @return a formatted {@code String} containing the information accumulated in the internal in-memory row.
	 */
	public final String writeValuesToString() {
		String out = writeRowToString(Arrays.copyOf(partialLine, partialLineIndex + 1));
		partialLineIndex = 0;
		return out;
	}

	/**
	 * Returns the number of records written to the output so far
	 * @return the number of records written to the output so far
	 */
	public final long getRecordCount() {
		return recordCount;
	}

}
