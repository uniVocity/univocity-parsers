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

import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.input.concurrent.*;
import com.univocity.parsers.common.processor.*;

/**
 * This is the parent class for all configuration classes used by parsers ({@link AbstractParser})
 *
 * <p>By default, all parsers work with, at least, the following configuration options in addition to the ones provided by {@link CommonSettings}:
 *
 * <ul>
 * 	<li><b>rowProcessor:</b> a callback implementation of the interface {@link RowProcessor} which handles the life cycle of the parsing process and processes each record extracted from the input</li>
 *  <li><b>headerExtractionEnabled <i>(defaults to false)</i>:</b> indicates whether or not the first valid record parsed from the input should be considered as the row containing the names of each column</li>
 *  <li><b>columnReorderingEnabled <i>(defaults to true)</i>:</b> indicates whether fields selected using the field selection methods (defined by the parent class {@link CommonSettings}) should be reordered.
 *  	<p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not be parsed but and the record will contain empty values.
 *  	<p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
 *  <li><b>inputBufferSize <i>(defaults to 1024*1024 characters)</i>:</b> The number of characters held by the parser's buffer when processing the input.
 *  <li><b>readInputOnSeparateThread <i>(defaults true if the number of available processors at runtime is greater than 1)</i>:</b>
 *  	<p>When enabled, a reading thread (in <code>input.concurrent.ConcurrentCharInputReader</code>) will be started and load characters from the input, while the parser is processing its input buffer. This yields better performance, especially when reading from big input (>100 mb)
		<p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted (in {@link DefaultCharInputReader} it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
 *  <li><b>numberOfRecordsToRead <i>(defaults to -1)</i>:</b> Defines how many (valid) records are to be parsed before the process is stopped. A negative value indicates there's no limit.</li>
 * </ul>
 *
 * @param <F> the format supported by this parser.
 *
 * @see com.univocity.parsers.common.processor.RowProcessor
 * @see com.univocity.parsers.csv.CsvParserSettings
 * @see com.univocity.parsers.fixed.FixedWidthParserSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class CommonParserSettings<F extends Format> extends CommonSettings<F> {

	private boolean headerExtractionEnabled = false;
	private RowProcessor rowProcessor;
	private boolean columnReorderingEnabled = true;
	private int inputBufferSize = 1024 * 1024;
	private boolean readInputOnSeparateThread = Runtime.getRuntime().availableProcessors() > 1;
	private int numberOfRecordsToRead = -1;

	/**
	 * Indicates whether or not a separate thread will be used to read characters from the input while parsing (defaults true if the number of available processors at runtime is greater than 1)
	 * 	<p>When enabled, a reading thread (in <code>com.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader</code>) will be started and load characters from the input, while the parser is processing its input buffer. This yields better performance, especially when reading from big input (>100 mb)
	 *  <p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted (in {@link DefaultCharInputReader} it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
	 * @return true if the input should be read on a separate thread, false otherwise
	 */
	public boolean getReadInputOnSeparateThread() {
		return readInputOnSeparateThread;
	}

	/**
	 * Defines whether or not a separate thread will be used to read characters from the input while parsing (defaults true if the number of available processors at runtime is greater than 1)
	 * 	<p>When enabled, a reading thread (in <code>com.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader</code>) will be started and load characters from the input, while the parser is processing its input buffer. This yields better performance, especially when reading from big input (>100 mb)
	 *  <p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted (in {@link DefaultCharInputReader} it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
	 * @param readInputOnSeparateThread the flag indicating whether or not the input should be read on a separate thread
	 */
	public void setReadInputOnSeparateThread(boolean readInputOnSeparateThread) {
		this.readInputOnSeparateThread = readInputOnSeparateThread;
	}

	/**
	 * Indicates whether or not the first valid record parsed from the input should be considered as the row containing the names of each column
	 * @return true if the first valid record parsed from the input should be considered as the row containing the names of each column, false otherwise
	 */
	public boolean isHeaderExtractionEnabled() {
		return headerExtractionEnabled;
	}

	/**
	 * Defines whether or not the first valid record parsed from the input should be considered as the row containing the names of each column
	 * @param headerExtractionEnabled a flag indicating whether the first valid record parsed from the input should be considered as the row containing the names of each column
	 */
	public void setHeaderExtractionEnabled(boolean headerExtractionEnabled) {
		this.headerExtractionEnabled = headerExtractionEnabled;
	}

	/**
	 * Returns the callback implementation of the interface {@link RowProcessor} which handles the lifecyle of the parsing process and processes each record extracted from the input
	 * @return Returns the RowProcessor used by the parser to handle each record
	 *
	 * @see com.univocity.parsers.common.processor.ObjectRowProcessor
	 * @see com.univocity.parsers.common.processor.ObjectRowListProcessor
	 * @see com.univocity.parsers.common.processor.MasterDetailProcessor
	 * @see com.univocity.parsers.common.processor.MasterDetailListProcessor
	 * @see com.univocity.parsers.common.processor.BeanProcessor
	 * @see com.univocity.parsers.common.processor.BeanListProcessor
	 */
	public RowProcessor getRowProcessor() {
		if (rowProcessor == null) {
			return new AbstractRowProcessor();
		}
		return rowProcessor;
	}

	/**
	 * Defines the callback implementation of the interface {@link RowProcessor} which handles the lifecyle of the parsing process and processes each record extracted from the input
	 * @param processor the RowProcessor instance which should used by the parser to handle each record
	 *
	 * @see com.univocity.parsers.common.processor.ObjectRowProcessor
	 * @see com.univocity.parsers.common.processor.ObjectRowListProcessor
	 * @see com.univocity.parsers.common.processor.MasterDetailProcessor
	 * @see com.univocity.parsers.common.processor.MasterDetailListProcessor
	 * @see com.univocity.parsers.common.processor.BeanProcessor
	 * @see com.univocity.parsers.common.processor.BeanListProcessor
	 */
	public void setRowProcessor(RowProcessor processor) {
		this.rowProcessor = processor;
	}

	/**
	 * An implementation of {@link CharInputReader} which loads the parser buffer in parallel or sequentially, as defined by the readInputOnSeparateThread property
	 * @return The input reader as chosen with the readInputOnSeparateThread property.
	 */
	CharInputReader newCharInputReader() {
		if (readInputOnSeparateThread) {
			return new ConcurrentCharInputReader(getFormat().getLineSeparator(), getFormat().getNormalizedNewline(), this.getInputBufferSize(), 10);
		} else {
			return new DefaultCharInputReader(getFormat().getLineSeparator(), getFormat().getNormalizedNewline(), this.getInputBufferSize());
		}
	}

	/**
	 * The number of valid records to be parsed before the process is stopped. A negative value indicates there's no limit (defaults to -1).
	 * @return the number of records to read before stopping the parsing process.
	 */
	public int getNumberOfRecordsToRead() {
		return numberOfRecordsToRead;
	}

	/**
	 * Defines the number of valid records to be parsed before the process is stopped. A negative value indicates there's no limit (defaults to -1).
	 * @param numberOfRecordsToRead the number of records to read before stopping the parsing process.
	 */
	public void setNumberOfRecordsToRead(int numberOfRecordsToRead) {
		this.numberOfRecordsToRead = numberOfRecordsToRead;
	}

	/**
	 * Indicates whether fields selected using the field selection methods (defined by the parent class {@link CommonSettings}) should be reordered (defaults to true).
	 * 	<p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not be parsed but and the record will contain empty values.
	 * 	<p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
	 * @return true if the selected fields should be reordered and returned by the parser, false otherwise
	 */
	public boolean isColumnReorderingEnabled() {
		return columnReorderingEnabled;
	}

	/**
	 * Defines whether fields selected using the field selection methods (defined by the parent class {@link CommonSettings}) should be reordered (defaults to true).
	 * 	<p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not be parsed but and the record will contain empty values.
	 * 	<p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
	 * @param columnReorderingEnabled the flag indicating whether or not selected fields should be reordered and returned by the parser
	 */
	public void setColumnReorderingEnabled(boolean columnReorderingEnabled) {
		this.columnReorderingEnabled = columnReorderingEnabled;
	}

	/**
	 * Informs the number of characters held by the parser's buffer when processing the input (defaults to 1024*1024 characters).
	 * @return the number of characters held by the parser's buffer when processing the input
	 */
	public int getInputBufferSize() {
		return inputBufferSize;
	}

	/**
	 * Defines the number of characters held by the parser's buffer when processing the input (defaults to 1024*1024 characters).
	 * @param inputBufferSize the new input buffer size (in number of characters)
	 */
	public void setInputBufferSize(int inputBufferSize) {
		this.inputBufferSize = inputBufferSize;
	}

	/**
	 * Returns an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent a null value (when the String parsed from the input is empty)
	 * @return an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent a null value (when the String parsed from the input is empty)
	 */
	protected CharAppender newCharAppender() {
		return new DefaultCharAppender(getMaxCharsPerColumn(), getNullValue());
	}
}
