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
	private final StringBuilder freeText = new StringBuilder();
	private final WriterCharAppender rowAppender;

	private final Object[] outputRow;
	private final int[] indexesToWrite;
	private final char[] lineSeparator;

	private String[] headers;
	private int recordCount = 0;

	protected final String nullValue;
	protected final String emptyValue;
	protected final CharAppender appender;

	/**
	 * All writers must support, at the very least, the settings provided by {@link CommonWriterSettings}. The AbstractWriter requires its configuration to be properly initialized.
	 * @param writer the output resource that will receive the format-specific records as defined by subclasses of {@link AbstractWriter}.
	 * @param settings the parser configuration
	 */
	public AbstractWriter(Writer writer, S settings) {
		this.nullValue = settings.getNullValue();
		this.emptyValue = settings.getEmptyValue();

		this.lineSeparator = settings.getFormat().getLineSeparator();
		this.comment = settings.getFormat().getComment();
		this.skipEmptyLines = settings.getSkipEmptyLines();
		this.writerProcessor = settings.getRowWriterProcessor();

		this.appender = new WriterCharAppender(settings.getMaxCharsPerColumn(), "", settings.getFormat());
		this.rowAppender = new WriterCharAppender(settings.getMaxCharsPerColumn() * settings.getMaxColumns(), "", settings.getFormat());

		if (writer instanceof BufferedWriter) {
			this.writer = (BufferedWriter) writer;
		} else {
			this.writer = new BufferedWriter(writer);
		}

		this.headers = settings.getHeaders();

		FieldSelector selector = settings.getFieldSelector();
		if (headers != null && headers.length > 0 && selector != null) {
			outputRow = new Object[headers.length];
			indexesToWrite = selector.getFieldIndexes(headers);
		} else {
			outputRow = null;
			indexesToWrite = null;
		}
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
			throw new TextWritingException("No headers defined", recordCount, (Object[]) null);
		}
	}

	/**
	 * Writes the given collection of headers to the output.
	 * <p> A {@link TextWritingException} will be thrown if no headers were defined or if records were already written to the output.
	 * @param headers the headers to write to the output.
	 */
	public final void writeHeaders(String... headers) {
		if (recordCount > 0) {
			throw new TextWritingException("Cannot write headers after records have been written", recordCount, headers);
		}
		if (headers != null && headers.length > 0) {
			processRow(headers);
			writeRow();
		} else {
			throw new TextWritingException("No headers defined", recordCount, headers);
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
			throw new IllegalStateException("Cannot process record '" + record + "' without a writer processor. Please define a writer processor instance in the settings or use the 'writeRow' methods.");
		}

		Object[] row = writerProcessor.write(record, headers, indexesToWrite);
		writeRow(row);
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
	public final void writeRow(Object... row) {
		try {
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

			processRow(row);

			writeRow();
		} catch (Exception ex) {
			try {
				throw new TextWritingException("Error writing row", recordCount, row, ex);
			} finally {
				close();
			}
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
		freeText.setLength(0);
		freeText.append(row);
		writeToOutput(freeText.toString());
	}

	/**
	 * Writes an empty line to the output.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * <p> The output will remain open for further writing.
	 */
	public final void writeEmptyRow() {
		try {
			writer.write(lineSeparator);
		} catch (IOException ex) {
			close();
			throw new TextWritingException("Error writing row", recordCount, Arrays.toString(lineSeparator), ex);
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
	private final <T> void fillOutputRow(T[] row) {
		if (row.length > indexesToWrite.length) {
			String msg = "Cannot write row as it contains more elements than the number of selected fields (" + this.indexesToWrite.length + " fields selected)";
			throw new TextWritingException(msg, recordCount, row);
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
	private final void writeRow() {
		try {
			rowAppender.appendNewLine();
			rowAppender.writeCharsAndReset(writer);
			recordCount++;
		} catch (Exception ex) {
			try {
				throw new TextWritingException("Error writing row", recordCount, rowAppender.getAndReset(), ex);
			} finally {
				close();
			}
		}
	}

	/**
	 * Writes text (potentially free-text given be the user), followed by a newline, to the output.
	 * <p> The newline character sequence will conform to what is specified in {@link Format#getLineSeparator()}
	 * @param row the text to be written to the output.
	 */
	private final void writeToOutput(String row) {
		try {
			writer.write(row);
			writer.write(lineSeparator);
		} catch (IOException ex) {
			try {
				throw new TextWritingException("Error writing row", recordCount, row, ex);
			} finally {
				close();
			}
		}
	}

	/**
	 * Identifies the starting character index of a value being written if leading whitespaces are to be discarded.
	 * <p><b>Implementation note</b> whitespaces are considered all characters where {@code ch <= ' '} evaluates to {@code true}
	 * @param element the String to be scanned for leading whitespaces.
	 * @return the index of the first non-whitespace character in the given element.
	 */
	protected final int skipLeadingWhitespace(String element) {
		for (int i = 0; i < element.length(); i++) {
			char nextChar = element.charAt(i);
			if (!(nextChar <= ' ')) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Flushes the {@link java.io.Writer} given in this class constructor.
	 * <p> An IllegalStateException will be thrown in case of any errors, and the writer will be closed.
	 */
	public final void flush() {
		try {
			writer.flush();
		} catch (Exception ex) {
			close();
		}
	}

	/**
	 * Closes the {@link java.io.Writer} given in this class construtor.
	 * <p> An IllegalStateException will be thrown in case of any errors.
	 */
	public final void close() {
		try {
			try {
				writer.flush();
			} finally {
				writer.close();
			}
		} catch (Exception ex) {
			throw new IllegalStateException("Error closing the output.", ex);
		}
	}

	/**
	 * Converts a given object to its String representation for writing to the output.
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

}
