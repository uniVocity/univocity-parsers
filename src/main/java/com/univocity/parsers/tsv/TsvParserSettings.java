/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
package com.univocity.parsers.tsv;

import com.univocity.parsers.common.*;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 * This is the configuration class used by the TSV parser ({@link TsvParser})
 *
 * <p>It supports the configuration options provided by {@link CommonParserSettings} only
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.tsv.TsvParser
 * @see com.univocity.parsers.tsv.TsvFormat
 * @see com.univocity.parsers.common.CommonParserSettings
 */
public class TsvParserSettings extends CommonParserSettings<TsvFormat> {

	private boolean lineJoiningEnabled = false;

	/**
	 * Identifies whether or lines ending with the escape character (defined by {@link TsvFormat#getEscapeChar()}
	 * and followed by a line separator character should be joined with the following line.
	 *
	 * Typical examples include inputs where lines end with sequences such as: {@code '\'+'\n'} and {@code '\'+'\r'+'\n'}.
	 *
	 * When line joining is disabled (the default), the {@link TsvParser} converts sequences containing
	 * the escape character (typically '\') followed by characters 'n' or 'r' into a '\n' or '\r' character.
	 * It will continue processing the contents found in the same line, until a new line character is found.
	 *
	 * If line joining is enabled, the {@link TsvParser} will convert sequences containing
	 * the escape character, followed by characters '\n', '\r' or '\r\n', into a '\n' or '\r' character.
	 * It will continue processing the contents found in the next line, until a new line character is found, given it is
	 * not preceded by another escape character.
	 *
	 * @return {@code true} if line joining is enabled, otherwise {@code false}
	 */
	public boolean isLineJoiningEnabled() {
		return lineJoiningEnabled;
	}

	/**
	 * Defines how the parser should handle escaped line separators. By enabling lines joining,
	 * lines ending with the escape character (defined by {@link TsvFormat#getEscapeChar()}
	 * and followed by a line separator character will be joined with the following line.
	 *
	 * Typical examples include inputs where lines end with sequences such as: {@code '\'+'\n'} and {@code '\'+'\r'+'\n'}.
	 *
	 * When line joining is disabled (the default), the {@link TsvParser} converts sequences containing
	 * the escape character (typically '\') followed by characters 'n' or 'r' into a '\n' or '\r' character.
	 * It will continue processing the contents found in the same line, until a new line character is found.
	 *
	 * If line joining is enabled, the {@link TsvParser} will convert sequences containing
	 * the escape character, followed by characters '\n', '\r' or '\r\n', into a '\n' or '\r' character.
	 * It will continue processing the contents found in the next line, until a new line character is found, given it is
	 * not preceded by another escape character.
	 *
	 * @param lineJoiningEnabled a flag indicating whether or not to enable line joining.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setLineJoiningEnabled(boolean lineJoiningEnabled) {
		this.lineJoiningEnabled = lineJoiningEnabled;
		return this;
	}

	/**
	 * Sets the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 *
	 * @param emptyValue the String representation of a null value
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setNullValue(String emptyValue) {
		super.setNullValue(emptyValue);
		return this;
	}

	/**
	 * Defines the maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 *
	 * <p>To enable auto-expansion of the internal array, set this property to -1</p>
	 *
	 * @param maxCharsPerColumn The maximum number of characters allowed for any given value being written/read
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setMaxCharsPerColumn(int maxCharsPerColumn) {
		super.setMaxCharsPerColumn(maxCharsPerColumn);
		return this;
	}

	/**
	 * Defines whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 *
	 * @param skipEmptyLines true if empty lines should be ignored, false otherwise
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setSkipEmptyLines(boolean skipEmptyLines) {
		super.setSkipEmptyLines(skipEmptyLines);
		return this;
	}

	/**
	 * Defines whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreTrailingWhitespaces true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setIgnoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
		super.setIgnoreTrailingWhitespaces(ignoreTrailingWhitespaces);
		return this;
	}

	/**
	 * Defines whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreLeadingWhitespaces true if leading whitespaces from values being read/written should be skipped, false otherwise
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
		super.setIgnoreLeadingWhitespaces(ignoreLeadingWhitespaces);
		return this;
	}

	/**
	 * Defines the field names in the input/output, in the sequence they occur (defaults to null).
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @param headers the field name sequence associated with each column in the input/output.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setHeaders(String... headers) {
		super.setHeaders(headers);
		return this;
	}

	/**
	 * Defines the field names in the input/output derived from a given class with {@link Parsed} annotated attributes/methods.
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @param headerSourceClass the class from which the headers have been derived.
	 * @param headers           the field name sequence associated with each column in the input/output.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setHeadersDerivedFromClass(Class<?> headerSourceClass, String... headers) {
		super.setHeadersDerivedFromClass(headerSourceClass, headers);
		return this;
	}

	/**
	 * Defines a hard limit of how many columns a record can have (defaults to 512).
	 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with.
	 *
	 * @param maxColumns The maximum number of columns a record can have.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setMaxColumns(int maxColumns) {
		super.setMaxColumns(maxColumns);
		return this;
	}

	/**
	 * Defines the format of the file to be parsed/written (returns the format's defaults).
	 *
	 * @param format The format of the file to be parsed/written
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setFormat(TsvFormat format) {
		super.setFormat(format);
		return this;
	}

	/**
	 * Indicates whether this settings object can automatically derive configuration options. This is used, for example, to define the headers when the user
	 * provides a {@link BeanWriterProcessor} where the bean class contains a {@link Headers} annotation, or to enable header extraction when the bean class of a
	 * {@link BeanProcessor} has attributes mapping to header names.
	 *
	 * @param autoConfigurationEnabled a flag to turn the automatic configuration feature on/off.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public final TsvParserSettings setAutoConfigurationEnabled(boolean autoConfigurationEnabled) {
		super.setAutoConfigurationEnabled(autoConfigurationEnabled);
		return this;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link RowProcessor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param rowProcessorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @return this {@code TsvParserSettings} instance
	 *
	 * @deprecated Use the {@link #setProcessorErrorHandler(ProcessorErrorHandler)} method as it allows format-specific error handlers to be built to work with different implementations of {@link Context}.
	 * Implementations based on {@link RowProcessorErrorHandler} allow only parsers who provide a {@link ParsingContext} to be used.
	 */
	@Deprecated
	public TsvParserSettings setRowProcessorErrorHandler(RowProcessorErrorHandler rowProcessorErrorHandler) {
		super.setRowProcessorErrorHandler(rowProcessorErrorHandler);
		return this;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link com.univocity.parsers.common.processor.core.Processor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param processorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setProcessorErrorHandler(ProcessorErrorHandler<? extends Context> processorErrorHandler) {
		super.setProcessorErrorHandler(processorErrorHandler);
		return this;
	}

	/**
	 * Configures the parser/writer to limit the length of displayed contents being parsed/written in the exception message when an error occurs.
	 *
	 * <p>If set to {@code 0}, then no exceptions will include the content being manipulated in their attributes,
	 * and the {@code "<omitted>"} string will appear in error messages as the parsed/written content.</p>
	 *
	 * <p>defaults to {@code -1} (no limit)</p>.
	 *
	 * @param errorContentLength maximum length of contents displayed in exception messages in case of errors while parsing/writing.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public TsvParserSettings setErrorContentLength(int errorContentLength) {
		super.setErrorContentLength(errorContentLength);
		return this;
	}

	/**
	 * Configures the parser to skip bit values as whitespace.
	 *
	 * By default the parser/writer removes control characters and considers a whitespace any character where {@code character <= ' '} evaluates to
	 * {@code true}. This includes bit values, i.e. {@code 0} (the \0 character) and {@code 1} which might
	 * be produced by database dumps. Disabling this flag will prevent the parser/writer from discarding these characters
	 * when {@link #getIgnoreLeadingWhitespaces()} or {@link #getIgnoreTrailingWhitespaces()} evaluate to {@code true}.
	 *
	 * <p>defaults to {@code true}</p>
	 *
	 * @param skipBitsAsWhitespace a flag indicating whether bit values (0 or 1) should be considered whitespace.
	 *
	 * @return this {@code TsvParserSettings} instance
	 */
	public final TsvParserSettings setSkipBitsAsWhitespace(boolean skipBitsAsWhitespace) {
		super.setSkipBitsAsWhitespace(skipBitsAsWhitespace);
		return this;
	}

	/**
	 * Returns the default TsvFormat configured to handle TSV inputs
	 *
	 * @return and instance of TsvFormat configured to handle TSV
	 */
	@Override
	protected TsvFormat createDefaultFormat() {
		return new TsvFormat();
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
	}

	@Override
	public final TsvParserSettings clone() {
		return (TsvParserSettings) super.clone();
	}

	@Override
	public final TsvParserSettings clone(boolean clearInputSpecificSettings) {
		return (TsvParserSettings) super.clone(clearInputSpecificSettings);
	}
}
