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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.*;

import java.util.*;
import java.util.Map.*;

/**
 * This is the parent class for all configuration classes used by parsers ({@link AbstractParser}) and writers ({@link AbstractWriter})
 *
 * <p>By default, all parsers and writers work with, at least, the following configuration options:
 *
 * <ul>
 * <li><b>format <i>(each file format provides its default)</i>:</b> the input/output format of a given file</li>
 * <li><b>nullValue <i>(defaults to null)</i>:</b>
 * <p>when reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
 * <p>when writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string</li>
 * <li><b>maxCharsPerColumn <i>(defaults to 4096)</i>:</b> The maximum number of characters allowed for any given value being written/read.
 * <p>You need this to avoid OutOfMemoryErrors in case a file does not have a valid format. In such cases the parser might just keep reading from the input
 * until its end or the memory is exhausted. This sets a limit which avoids unwanted JVM crashes.</li>
 * <li><b>maxColumns <i>(defaults to 512)</i>:</b> a hard limit on how many columns a record can have.
 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with</li>
 * <li><b>skipEmptyLines <i>(defaults to true)</i>:</b>
 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored</li>
 * <li><b>ignoreTrailingWhitespaces <i>(defaults to true)</i>:</b> removes trailing whitespaces from values being read/written</li>
 * <li><b>ignoreLeadingWhitespaces <i>(defaults to true)</i>:</b> removes leading whitespaces from values being read/written</li>
 * <li><b>headers <i>(defaults to null)</i>:</b> the field names in the input/output, in the sequence they occur.
 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row</li>
 * <li><b>field selection <i>(defaults to none)</i>:</b> a selection of fields for reading and writing. Fields can be selected by their name or their position.
 * <p>when reading, the selected fields only will be parsed and the remaining fields will be discarded.
 * <p>when writing, the selected fields only will be written and the remaining fields will be discarded</li>
 * </ul>
 *
 * @param <F> the format supported by this settings class.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.CommonParserSettings
 * @see com.univocity.parsers.common.CommonWriterSettings
 * @see com.univocity.parsers.csv.CsvParserSettings
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthParserSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 */

public abstract class CommonSettings<F extends Format> implements Cloneable {

	private F format;
	private String nullValue = null;
	private int maxCharsPerColumn = 4096;
	private int maxColumns = 512;
	private boolean skipEmptyLines = true;
	private boolean ignoreTrailingWhitespaces = true;
	private boolean ignoreLeadingWhitespaces = true;
	private FieldSelector fieldSelector = null;
	private boolean autoConfigurationEnabled = true;
	private ProcessorErrorHandler<? extends Context> errorHandler;
	private int errorContentLength = -1;
	private boolean skipBitsAsWhitespace = true;

	private String[] headers;

	/**
	 * Creates a new instance of this settings object using the default format specified by the concrete class that inherits from {@code CommonSettings}
	 */
	public CommonSettings() {
		setFormat(createDefaultFormat());
	}

	/**
	 * Returns the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 *
	 * @return the String representation of a null value
	 */
	public String getNullValue() {
		return nullValue;
	}

	/**
	 * Sets the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 *
	 * @param emptyValue the String representation of a null value
	 */
	public void setNullValue(String emptyValue) {
		this.nullValue = emptyValue;
	}

	/**
	 * The maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 *
	 * <p>If set to {@code -1}, then the internal internal array will expand automatically, up to the limit allowed by the JVM</p>
	 *
	 * @return The maximum number of characters allowed for any given value being written/read
	 */
	public int getMaxCharsPerColumn() {
		return maxCharsPerColumn;
	}

	/**
	 * Defines the maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 *
	 * <p>To enable auto-expansion of the internal array, set this property to -1</p>
	 *
	 * @param maxCharsPerColumn The maximum number of characters allowed for any given value being written/read
	 */
	public void setMaxCharsPerColumn(int maxCharsPerColumn) {
		this.maxCharsPerColumn = maxCharsPerColumn;
	}

	/**
	 * Returns whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 *
	 * @return true if empty lines are configured to be ignored, false otherwise
	 */
	public boolean getSkipEmptyLines() {
		return skipEmptyLines;
	}

	/**
	 * Defines whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 *
	 * @param skipEmptyLines true if empty lines should be ignored, false otherwise
	 */
	public void setSkipEmptyLines(boolean skipEmptyLines) {
		this.skipEmptyLines = skipEmptyLines;
	}

	/**
	 * Returns whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @return true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 */
	public boolean getIgnoreTrailingWhitespaces() {
		return ignoreTrailingWhitespaces;
	}

	/**
	 * Defines whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreTrailingWhitespaces true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 */
	public void setIgnoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
		this.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces;
	}

	/**
	 * Returns whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @return true if leading whitespaces from values being read/written should be skipped, false otherwise
	 */
	public boolean getIgnoreLeadingWhitespaces() {
		return ignoreLeadingWhitespaces;
	}

	/**
	 * Defines whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreLeadingWhitespaces true if leading whitespaces from values being read/written should be skipped, false otherwise
	 */
	public void setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
		this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
	}

	/**
	 * Defines the field names in the input/output, in the sequence they occur (defaults to null).
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @param headers the field name sequence associated with each column in the input/output.
	 */
	public void setHeaders(String... headers) {
		if (headers == null || headers.length == 0) {
			this.headers = null;
		} else {
			this.headers = headers;
		}
	}

	/**
	 * Returns the field names in the input/output, in the sequence they occur (defaults to null).
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @return the field name sequence associated with each column in the input/output.
	 */
	public String[] getHeaders() {
		return this.headers;
	}

	/**
	 * Returns the hard limit of how many columns a record can have (defaults to 512).
	 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with .
	 *
	 * @return The maximum number of columns a record can have.
	 */
	public int getMaxColumns() {
		return maxColumns;
	}

	/**
	 * Defines a hard limit of how many columns a record can have (defaults to 512).
	 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with.
	 *
	 * @param maxColumns The maximum number of columns a record can have.
	 */
	public void setMaxColumns(int maxColumns) {
		this.maxColumns = maxColumns;
	}

	/**
	 * The format of the file to be parsed/written (returns the format's defaults).
	 *
	 * @return The format of the file to be parsed/written
	 */
	public F getFormat() {
		return format;
	}

	/**
	 * Defines the format of the file to be parsed/written (returns the format's defaults).
	 *
	 * @param format The format of the file to be parsed/written
	 */
	public void setFormat(F format) {
		if (format == null) {
			throw new IllegalArgumentException("Format cannot be null");
		}
		this.format = format;
	}

	/**
	 * Selects a sequence of fields for reading/writing by their names.
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence provided represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting fields "H3" and "H1" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param fieldNames The field names to read/write
	 *
	 * @return the (modifiable) set of selected fields
	 */
	public FieldSet<String> selectFields(String... fieldNames) {
		return setFieldSet(new FieldNameSelector(), fieldNames);
	}

	/**
	 * Selects fields which will not be read/written, by their names
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence of non-excluded fields represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting fields "H3" and "H1" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param fieldNames The field names to exclude from the parsing/writing process
	 *
	 * @return the (modifiable) set of ignored fields
	 */
	public FieldSet<String> excludeFields(String... fieldNames) {
		return setFieldSet(new ExcludeFieldNameSelector(), fieldNames);
	}

	/**
	 * Selects a sequence of fields for reading/writing by their positions.
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence provided represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting indexes "2" and "0" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param fieldIndexes The indexes to read/write
	 *
	 * @return the (modifiable) set of selected fields
	 */
	public FieldSet<Integer> selectIndexes(Integer... fieldIndexes) {
		return setFieldSet(new FieldIndexSelector(), fieldIndexes);
	}

	/**
	 * Selects columns which will not be read/written, by their positions
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence of non-excluded fields represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting fields by index, such as  "2" and "0" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param fieldIndexes indexes of columns to exclude from the parsing/writing process
	 *
	 * @return the (modifiable) set of ignored fields
	 */
	public FieldSet<Integer> excludeIndexes(Integer... fieldIndexes) {
		return setFieldSet(new ExcludeFieldIndexSelector(), fieldIndexes);
	}

	/**
	 * Selects a sequence of fields for reading/writing by their names
	 *
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence provided represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting fields "H3" and "H1" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param columns The columns to read/write
	 *
	 * @return the (modifiable) set of selected fields
	 */
	@SuppressWarnings("rawtypes")
	public FieldSet<Enum> selectFields(Enum... columns) {
		return setFieldSet(new FieldEnumSelector(), columns);
	}

	/**
	 * Selects columns which will not be read/written, by their names
	 *
	 * <p><b>When reading</b>, only the values of the selected columns will be parsed, and the content of the other columns ignored.
	 * The resulting rows will be returned with the selected columns only, in the order specified. If you want to
	 * obtain the original row format, with all columns included and nulls in the fields that have not been selected,
	 * set {@link CommonParserSettings#setColumnReorderingEnabled(boolean)} with {@code false}.</p>
	 *
	 * <p><b>When writing</b>, the sequence of non-excluded fields represents the expected format of the input rows. For example,
	 * headers can be "H1,H2,H3", but the input data is coming with values for two columns and in a different order,
	 * such as "V_H3, V_H1". Selecting fields "H3" and "H1" will allow the writer to write values in the expected
	 * locations. Using the given example, the output row will be generated as: "V_H1,null,V_H3"</p>
	 *
	 * @param columns The columns to exclude from the parsing/writing process
	 *
	 * @return the (modifiable) set of ignored fields
	 */
	@SuppressWarnings("rawtypes")
	public FieldSet<Enum> excludeFields(Enum... columns) {
		return setFieldSet(new ExcludeFieldEnumSelector(), columns);
	}

	/**
	 * Replaces the current field selection
	 *
	 * @param fieldSet the new set of selected fields
	 * @param values   the values to include to the selection
	 *
	 * @return the set of selected fields given in as a parameter.
	 */
	private <T> FieldSet<T> setFieldSet(FieldSet<T> fieldSet, T... values) {
		this.fieldSelector = (FieldSelector) fieldSet;
		fieldSet.add(values);
		return fieldSet;
	}

	/**
	 * Returns the set of selected fields, if any
	 *
	 * @return the set of selected fields. Null if no field was selected/excluded
	 */
	FieldSet<?> getFieldSet() {
		return (FieldSet<?>) fieldSelector;
	}

	/**
	 * Returns the FieldSelector object, which handles selected fields.
	 *
	 * @return the FieldSelector object, which handles selected fields. Null if no field was selected/excluded
	 */
	FieldSelector getFieldSelector() {
		return this.fieldSelector;
	}

	/**
	 * Indicates whether this settings object can automatically derive configuration options. This is used, for example, to define the headers when the user
	 * provides a {@link BeanWriterProcessor} where the bean class contains a {@link Headers} annotation, or to enable header extraction when the bean class of a
	 * {@link BeanProcessor} has attributes mapping to header names.
	 *
	 * <p>Defaults to {@code true}</p>
	 *
	 * @return {@code true} if the automatic configuration feature is enabled, false otherwise
	 */
	public final boolean isAutoConfigurationEnabled() {
		return autoConfigurationEnabled;
	}

	/**
	 * Indicates whether this settings object can automatically derive configuration options. This is used, for example, to define the headers when the user
	 * provides a {@link BeanWriterProcessor} where the bean class contains a {@link Headers} annotation, or to enable header extraction when the bean class of a
	 * {@link BeanProcessor} has attributes mapping to header names.
	 *
	 * @param autoConfigurationEnabled a flag to turn the automatic configuration feature on/off.
	 */
	public final void setAutoConfigurationEnabled(boolean autoConfigurationEnabled) {
		this.autoConfigurationEnabled = autoConfigurationEnabled;
	}

	/**
	 * Returns the custom error handler to be used to capture and handle errors that might happen while processing records with a {@link RowProcessor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing/writing process won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @return the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @deprecated Use the {@link #getProcessorErrorHandler()} method as it allows format-specific error handlers to be built to work with different implementations of {@link Context}.
	 * Implementations based on {@link RowProcessorErrorHandler} allow only parsers who provide a {@link ParsingContext} to be used.
	 */
	@Deprecated
	public RowProcessorErrorHandler getRowProcessorErrorHandler() {
		return errorHandler == null ? NoopRowProcessorErrorHandler.instance : (RowProcessorErrorHandler) errorHandler;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link RowProcessor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param rowProcessorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @deprecated Use the {@link #setProcessorErrorHandler(ProcessorErrorHandler)} method as it allows format-specific error handlers to be built to work with different implementations of {@link Context}.
	 * Implementations based on {@link RowProcessorErrorHandler} allow only parsers who provide a {@link ParsingContext} to be used.
	 */
	@Deprecated
	public void setRowProcessorErrorHandler(RowProcessorErrorHandler rowProcessorErrorHandler) {
		this.errorHandler = rowProcessorErrorHandler;
	}

	/**
	 * Returns the custom error handler to be used to capture and handle errors that might happen while processing records with a {@link com.univocity.parsers.common.processor.core.Processor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing/writing process won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param <T> the {@code Context} type provided by the parser implementation.
	 *
	 * @return the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 */
	public <T extends Context> ProcessorErrorHandler<T> getProcessorErrorHandler() {
		return errorHandler == null ? NoopProcessorErrorHandler.instance : (ProcessorErrorHandler<T>) errorHandler;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link com.univocity.parsers.common.processor.core.Processor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param processorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 */
	public void setProcessorErrorHandler(ProcessorErrorHandler<? extends Context> processorErrorHandler) {
		this.errorHandler = processorErrorHandler;
	}


	/**
	 * Returns a flag indicating whether or not a {@link ProcessorErrorHandler} has been defined through the use of method {@link #setProcessorErrorHandler(ProcessorErrorHandler)}
	 *
	 * @return {@code true} if the parser/writer is configured to use a {@link ProcessorErrorHandler}
	 */
	public boolean isProcessorErrorHandlerDefined() {
		return errorHandler != null;
	}

	/**
	 * Extending classes must implement this method to return the default format settings for their parser/writer
	 *
	 * @return Default format configuration for the given parser/writer settings.
	 */
	protected abstract F createDefaultFormat();

	final void autoConfigure() {
		if (!this.autoConfigurationEnabled) {
			return;
		}

		runAutomaticConfiguration();
	}

	/**
	 * Configures the parser/writer to trim or keep leading and trailing whitespaces around values
	 * This has the same effect as invoking both {@link #setIgnoreLeadingWhitespaces(boolean)} and {@link #setIgnoreTrailingWhitespaces(boolean)}
	 * with the same value.
	 *
	 * @param trim a flag indicating whether the whitespaces should remove whitespaces around values parsed/written.
	 */
	public final void trimValues(boolean trim) {
		this.setIgnoreLeadingWhitespaces(trim);
		this.setIgnoreTrailingWhitespaces(trim);
	}

	/**
	 * Configures the parser/writer to limit the length of displayed contents being parsed/written in the exception message when an error occurs
	 *
	 * <p>If set to {@code 0}, then no exceptions will include the content being manipulated in their attributes,
	 * and the {@code "<omitted>"} string will appear in error messages as the parsed/written content.</p>
	 *
	 * <p>defaults to {@code -1} (no limit)</p>.
	 *
	 * @return the maximum length of contents displayed in exception messages in case of errors while parsing/writing.
	 */
	public int getErrorContentLength() {
		return errorContentLength;
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
	 */
	public void setErrorContentLength(int errorContentLength) {
		this.errorContentLength = errorContentLength;
	}

	void runAutomaticConfiguration() {

	}

	/**
	 * Returns a flag indicating whether the parser/writer should skip bit values as whitespace.
	 *
	 * By default the parser/writer
	 * removes control characters and considers a whitespace any character where {@code character <= ' '} evaluates to
	 * {@code true}. This includes bit values, i.e. {@code 0} (the \0 character) and {@code 1} which might
	 * be produced by database dumps. Disabling this flag will prevent the parser/writer from discarding these characters
	 * when {@link #getIgnoreLeadingWhitespaces()} or {@link #getIgnoreTrailingWhitespaces()} evaluate to {@code true}.
	 *
	 * <p>defaults to {@code true}</p>
	 *
	 * @return a flag indicating whether bit values (0 or 1) should be considered whitespace.
	 */
	public final boolean getSkipBitsAsWhitespace() {
		return skipBitsAsWhitespace;
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
	 */
	public final void setSkipBitsAsWhitespace(boolean skipBitsAsWhitespace) {
		this.skipBitsAsWhitespace = skipBitsAsWhitespace;
	}

	/**
	 * Returns the starting decimal range for {@code characters <= ' '} that should be skipped as whitespace, as
	 * determined by {@link #getSkipBitsAsWhitespace()}
	 *
	 * @return the starting range after which characters will be considered whitespace
	 */
	protected final int getWhitespaceRangeStart() {
		return skipBitsAsWhitespace ? -1 : 1;
	}

	@Override
	public final String toString() {
		StringBuilder out = new StringBuilder();

		out.append(getClass().getSimpleName()).append(':');

		TreeMap<String, Object> config = new TreeMap<String, Object>();
		addConfiguration(config);

		for (Entry<String, Object> e : config.entrySet()) {
			out.append("\n\t");
			out.append(e.getKey()).append('=').append(e.getValue());
		}

		out.append("Format configuration:\n\t").append(getFormat().toString());

		return out.toString();
	}

	protected void addConfiguration(Map<String, Object> out) {
		out.put("Null value", nullValue);
		out.put("Maximum number of characters per column", maxCharsPerColumn);
		out.put("Maximum number of columns", maxColumns);
		out.put("Skip empty lines", skipEmptyLines);
		out.put("Ignore trailing whitespaces", ignoreTrailingWhitespaces);
		out.put("Ignore leading whitespaces", ignoreLeadingWhitespaces);
		out.put("Selected fields", fieldSelector == null ? "none" : fieldSelector.describe());
		out.put("Headers", Arrays.toString(headers));
		out.put("Auto configuration enabled", autoConfigurationEnabled);
		out.put("RowProcessor error handler", errorHandler);
		out.put("Length of content displayed on error", errorContentLength);
		out.put("Restricting data in exceptions", errorContentLength == 0);
		out.put("Skip bits as whitespace", skipBitsAsWhitespace);
	}


	/**
	 * Clones this configuration object to reuse user-provided settings.
	 *
	 * Properties that are specific to a given input (such as header names and selection of fields) can be reset to their defaults
	 * if the {@code clearInputSpecificSettings} flag is set to {@code true}
	 *
	 * @param clearInputSpecificSettings flag indicating whether to clear settings that are likely to be associated with a given input.
	 *
	 * @return a copy of the configurations applied to the current instance.
	 */
	protected CommonSettings clone(boolean clearInputSpecificSettings) {
		try {
			CommonSettings out = (CommonSettings) super.clone();
			if (out.format != null) {
				out.format = out.format.clone();
			}
			if (clearInputSpecificSettings) {
				out.clearInputSpecificSettings();
			}
			return out;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Clones this configuration object. Use alternative {@link #clone(boolean)} method to reset properties that are
	 * specific to a given input, such as header names and selection of fields.
	 *
	 * @return a copy of all configurations applied to the current instance.
	 */
	@Override
	protected CommonSettings clone() {
		return clone(false);
	}

	/**
	 * Clears settings that are likely to be specific to a given input.
	 */
	protected void clearInputSpecificSettings() {
		fieldSelector = null;
		headers = null;
	}
}
