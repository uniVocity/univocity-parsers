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

import java.util.*;
import java.util.Map.Entry;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.*;

/**
 * This is the parent class for all configuration classes used by parsers ({@link AbstractParser}) and writers ({@link AbstractWriter})
 *
 * <p>By default, all parsers and writers work with, at least, the following configuration options:
 *
 * <ul>
 *  <li><b>format <i>(each file format provides its default)</i>:</b> the input/output format of a given file</li>
 *  <li><b>nullValue <i>(defaults to null)</i>:</b>
 *  	<p>when reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
 *  	<p>when writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string</li>
 *  <li><b>maxCharsPerColumn <i>(defaults to 4096)</i>:</b> The maximum number of characters allowed for any given value being written/read.
 *  	<p>You need this to avoid OutOfMemoryErrors in case a file does not have a valid format. In such cases the parser might just keep reading from the input
 * 		until its end or the memory is exhausted. This sets a limit which avoids unwanted JVM crashes.</li>
 *  <li><b>maxColumns <i>(defaults to 512)</i>:</b> a hard limit on how many columns a record can have.
 *  	You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing width</li>
 *  <li><b>skipEmptyLines <i>(defaults to true)</i>:</b>
 *  	<p>when reading, if the parser reads a line that is empty, it will be skipped.
 *  	<p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored</li>
 *  <li><b>ignoreTrailingWhitespaces <i>(defaults to true)</i>:</b> removes trailing whitespaces from values being read/written</li>
 *  <li><b>ignoreLeadingWhitespaces <i>(defaults to true)</i>:</b> removes leading whitespaces from values being read/written</li>
 *  <li><b>headers <i>(defaults to null)</i>:</b> the field names in the input/output, in the sequence they occur.
 *  	<p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
 *  	<p>when writing, the given header names will be used to refer to each column and can be used for writing the header row</li>
 *  <li><b>field selection <i>(defaults to none)</i>:</b> a selection of fields for reading and writing. Fields can be selected by their name or their position.
 *  	<p>when reading, the selected fields only will be parsed and the remaining fields will be discarded.
 *  	<p>when writing, the selected fields only will be written and the remaining fields will be discarded</li>
 * </ul>
 *
 * @param <F> the format supported by this settings class.
 *
 * @see com.univocity.parsers.common.CommonParserSettings
 * @see com.univocity.parsers.common.CommonWriterSettings
 * @see com.univocity.parsers.csv.CsvParserSettings
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthParserSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */

public abstract class CommonSettings<F extends Format> {

	private F format;
	private String nullValue = null;
	private int maxCharsPerColumn = 4096;
	private int maxColumns = 512;
	private boolean skipEmptyLines = true;
	private boolean ignoreTrailingWhitespaces = true;
	private boolean ignoreLeadingWhitespaces = true;
	private FieldSelector fieldSelector = null;
	private boolean autoConfigurationEnabled = true;
	private RowProcessorErrorHandler errorHandler;

	private String[] headers;

	public CommonSettings() {
		setFormat(createDefaultFormat());
	}

	/**
	 * Returns the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 * @return the String representation of a null value
	 */
	public String getNullValue() {
		return nullValue;
	}

	/**
	 * Sets the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 * @param emptyValue the String representation of a null value
	 */
	public void setNullValue(String emptyValue) {
		this.nullValue = emptyValue;
	}

	/**
	 * The maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 * @return The maximum number of characters allowed for any given value being written/read
	 */
	public int getMaxCharsPerColumn() {
		return maxCharsPerColumn;
	}

	/**
	 * Defines the maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 * @param maxCharsPerColumn The maximum number of characters allowed for any given value being written/read
	 */
	public void setMaxCharsPerColumn(int maxCharsPerColumn) {
		this.maxCharsPerColumn = maxCharsPerColumn;
	}

	/**
	 * Returns whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 * @return true if empty lines are configured to be ignored, false otherwise
	 */
	public boolean getSkipEmptyLines() {
		return skipEmptyLines;
	}

	/**
	 * Defines whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 * @param skipEmptyLines true if empty lines should be ignored, false otherwise
	 */
	public void setSkipEmptyLines(boolean skipEmptyLines) {
		this.skipEmptyLines = skipEmptyLines;
	}

	/**
	 * Returns whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 * @return true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 */
	public boolean getIgnoreTrailingWhitespaces() {
		return ignoreTrailingWhitespaces;
	}

	/**
	 * Defines whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 * @param ignoreTrailingWhitespaces true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 */
	public void setIgnoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
		this.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces;
	}

	/**
	 * Returns whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 * @return true if leading whitespaces from values being read/written should be skipped, false otherwise
	 */
	public boolean getIgnoreLeadingWhitespaces() {
		return ignoreLeadingWhitespaces;
	}

	/**
	 * Defines whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 * @param ignoreLeadingWhitespaces true if leading whitespaces from values being read/written should be skipped, false otherwise
	 */
	public void setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
		this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
	}

	/**
	 * Defines the field names in the input/output, in the sequence they occur (defaults to null).
	 * 	<p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * 	<p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
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
	 * 	<p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * 	<p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 * @return the field name sequence associated with each column in the input/output.
	 */
	public String[] getHeaders() {
		return this.headers;
	}

	/**
	 *  Returns the hard limit of how many columns a record can have (defaults to 512).
	 * 	You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing width .
	 * @return The maximum number of columns a record can have.
	 */
	public int getMaxColumns() {
		return maxColumns;
	}

	/**
	 *  Defines a hard limit of how many columns a record can have (defaults to 512).
	 * 	You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing width.
	 * @param maxColumns The maximum number of columns a record can have.
	 */
	public void setMaxColumns(int maxColumns) {
		this.maxColumns = maxColumns;
	}

	/**
	 * The format of the file to be parsed/written (returns the format's defaults).
	 * @return The format of the file to be parsed/written
	 */
	public F getFormat() {
		return format;
	}

	/**
	 * Defines the format of the file to be parsed/written (returns the format's defaults).
	 * @param format The format of the file to be parsed/written
	 */
	public void setFormat(F format) {
		if (format == null) {
			throw new IllegalArgumentException("Format cannot be null");
		}
		this.format = format;
	}

	/**
	 * Selects a sequence of fields for reading/writing by their names
	 * @param fieldNames The field names to read/write
	 * @return the (modifiable) set of selected fields
	 */
	public FieldSet<String> selectFields(String... fieldNames) {
		return setFieldSet(new FieldNameSelector(), fieldNames);
	}

	/**
	 * Selects fields which will not be read/written by their names
	 * @param fieldNames The field names to exclude from the parsing/writing process
	 * @return the (modifiable) set of ignored fields
	 */
	public FieldSet<String> excludeFields(String... fieldNames) {
		return setFieldSet(new ExcludeFieldNameSelector(), fieldNames);
	}

	/**
	 * Selects a sequence of fields for reading/writing by their indexes
	 * @param fieldIndexes The field indexes to read/write
	 * @return the (modifiable) set of selected fields
	 */
	public FieldSet<Integer> selectIndexes(Integer... fieldIndexes) {
		return setFieldSet(new FieldIndexSelector(), fieldIndexes);
	}

	/**
	 * Selects fields which will not be read/written by their indexes
	 * @param fieldIndexes The field indexes to exclude from the parsing/writing process
	 * @return the (modifiable) set of ignored fields
	 */
	public FieldSet<Integer> excludeIndexes(Integer... fieldIndexes) {
		return setFieldSet(new ExcludeFieldIndexSelector(), fieldIndexes);
	}

	/**
	 * Replaces the current field selection
	 * @param fieldSet the new set of selected fields
	 * @param values the values to include to the selection
	 * @return the set of selected fields given in as a parameter.
	 */
	private <T> FieldSet<T> setFieldSet(FieldSet<T> fieldSet, T... values) {
		this.fieldSelector = (FieldSelector) fieldSet;
		fieldSet.add(values);
		return fieldSet;
	}

	/**
	 * Returns the set of selected fields, if any
	 * @return the set of selected fields. Null if no field was selected/excluded
	 */
	FieldSet<?> getFieldSet() {
		return (FieldSet<?>) fieldSelector;
	}

	/**
	 * Returns the FieldSelector object, which handles selected fields.
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
	 */
	public RowProcessorErrorHandler getRowProcessorErrorHandler() {
		return errorHandler == null ? NoopRowProcessorErrorHandler.instance : errorHandler;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link RowProcessor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param rowProcessorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 */
	public void setRowProcessorErrorHandler(RowProcessorErrorHandler rowProcessorErrorHandler) {
		this.errorHandler = rowProcessorErrorHandler;
	}

	/**
	 * Extending classes must implement this method to return the default format settings for their parser/writer
	 * @return Default format configuration for the given parser/writer settings.
	 *
	 */
	protected abstract F createDefaultFormat();

	final void autoConfigure() {
		if (!this.autoConfigurationEnabled) {
			return;
		}

		runAutomaticConfiguration();
	}

	void runAutomaticConfiguration() {

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
	}
}
