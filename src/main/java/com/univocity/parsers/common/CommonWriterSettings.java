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
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 * This is the parent class for all configuration classes used by writers ({@link AbstractWriter})
 *
 * <p>By default, all writers work with, at least, the following configuration options in addition to the ones provided by {@link CommonSettings}:
 *
 * <ul>
 * <li><b>rowWriterProcessor:</b> a implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.</li>
 * </ul>
 *
 * @param <F> the format supported by this writer.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.processor.RowWriterProcessor
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 */
public abstract class CommonWriterSettings<F extends Format> extends CommonSettings<F> {

	private RowWriterProcessor<?> rowWriterProcessor;

	private Boolean headerWritingEnabled = null;

	private String emptyValue = "";

	private boolean expandIncompleteRows = false;

	private boolean columnReorderingEnabled = false;

	/**
	 * Returns the String representation of an empty value (defaults to null)
	 *
	 * <p>When writing, if the writer has an empty String to write to the output, the emptyValue is used instead of an empty string
	 *
	 * @return the String representation of an empty value
	 */
	public String getEmptyValue() {
		return emptyValue;
	}

	/**
	 * Sets the String representation of an empty value (defaults to null)
	 *
	 * <p>If the writer has an empty String to write to the output, the emptyValue is used instead of an empty string
	 *
	 * @param emptyValue the String representation of an empty value
	 */
	public void setEmptyValue(String emptyValue) {
		this.emptyValue = emptyValue;
	}

	/**
	 * Returns the implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.
	 *
	 * @return the implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.
	 *
	 * @see com.univocity.parsers.common.processor.ObjectRowWriterProcessor
	 * @see com.univocity.parsers.common.processor.BeanWriterProcessor
	 */
	public RowWriterProcessor<?> getRowWriterProcessor() {
		return rowWriterProcessor;
	}

	/**
	 * Defines a processor for input objects that converts them into a manageable format for writing.
	 *
	 * @param rowWriterProcessor the implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.
	 *
	 * @see com.univocity.parsers.common.processor.ObjectRowWriterProcessor
	 * @see com.univocity.parsers.common.processor.BeanWriterProcessor
	 */
	public void setRowWriterProcessor(RowWriterProcessor<?> rowWriterProcessor) {
		this.rowWriterProcessor = rowWriterProcessor;
	}

	/**
	 * Returns a flag indicating whether automatic writing of headers is enabled. If enabled, and headers are defined (or derived automatically if {@link #isAutoConfigurationEnabled} evaluates to {@code true}),
	 * the writer will invoke the {@link AbstractWriter#writeHeaders()} method automatically. In this case, attempting to explicitly write the headers will result in a {@link TextWritingException}.
	 *
	 * <p>Defaults to {@code false}</p>
	 *
	 * @return returns {@code true} if automatic header writing is enabled, otherwise false.
	 */
	public final boolean isHeaderWritingEnabled() {
		return headerWritingEnabled == null ? false : headerWritingEnabled;
	}

	/**
	 * Enables automatic writing of headers when they are available. If enabled, and headers are defined (or derived automatically if {@link #isAutoConfigurationEnabled} evaluates to {@code true}),
	 * the writer will invoke the {@link AbstractWriter#writeHeaders()} method automatically. In this case, attempting to explicitly write the headers will result in a {@link TextWritingException}.
	 *
	 * <p>Defaults to {@code false}</p>
	 *
	 * @param headerWritingEnabled a flag to enable or disable automatic header writing.
	 */
	public final void setHeaderWritingEnabled(boolean headerWritingEnabled) {
		this.headerWritingEnabled = headerWritingEnabled;
	}

	/**
	 * Indicates whether the writer should expand records with less columns than the number of headers. For example, if
	 * the writer is using "A,B,C" as the headers, and the user provides a row with "V1,V2", then {@code null} will be
	 * introduced in column C, generating the output "V1,V2,null".
	 *
	 * <p>Defaults to {@code false}</p>
	 *
	 * @return a flag indicating whether records with less columns than the number of headers are to be expanded with nulls.
	 */
	public final boolean getExpandIncompleteRows() {
		return expandIncompleteRows;
	}

	/**
	 * Defines whether the writer should expand records with less columns than the number of headers.
	 * For example, if the writer is using "A,B,C" as the headers, and the user provides a row with "V1,V2", then {@code null} will be
	 * introduced in column C, generating the output "V1,V2,null".
	 *
	 * <p>Defaults to {@code false}</p>
	 *
	 * @param expandIncompleteRows a flag indicating whether records with less columns than the number of headers are to be expanded with nulls.
	 */
	public final void setExpandIncompleteRows(boolean expandIncompleteRows) {
		this.expandIncompleteRows = expandIncompleteRows;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Empty value", emptyValue);
		out.put("Header writing enabled", headerWritingEnabled);
		out.put("Row processor", rowWriterProcessor == null ? "none" : rowWriterProcessor.getClass().getName());
	}

	@Override
	final void runAutomaticConfiguration() {
		if (rowWriterProcessor instanceof BeanWriterProcessor<?>) {
			Class<?> beanClass = ((BeanWriterProcessor<?>) rowWriterProcessor).getBeanClass();
			configureFromAnnotations(beanClass);
		}
	}

	/**
	 * Configures the writer based on the annotations provided in a given class
	 *
	 * @param beanClass the classes whose annotations will be processed to derive configurations for writing.
	 */
	protected void configureFromAnnotations(Class<?> beanClass) {
		Headers headerAnnotation = AnnotationHelper.findHeadersAnnotation(beanClass);

		String[] headersFromBean = AnnotationHelper.deriveHeaderNamesFromFields(beanClass);
		boolean writeHeaders = false;

		if (headerAnnotation != null) {
			if (headerAnnotation.sequence().length > 0) {
				headersFromBean = headerAnnotation.sequence();
			}
			writeHeaders = headerAnnotation.write();
		}

		if (headerWritingEnabled == null) {
			headerWritingEnabled = writeHeaders;
		}

		if (getHeaders() == null && headersFromBean.length > 0) {
			setHeaders(headersFromBean);
		}
	}

	@Override
	protected CommonWriterSettings clone(boolean clearInputSpecificSettings) {
		return (CommonWriterSettings) super.clone(clearInputSpecificSettings);
	}

	@Override
	protected CommonWriterSettings clone() {
		return (CommonWriterSettings) super.clone();
	}

	@Override
	protected void clearInputSpecificSettings() {
		super.clearInputSpecificSettings();
		rowWriterProcessor = null;
	}

	/**
	 * Indicates whether fields selected using the field selection methods (defined by the parent class {@link CommonSettings}) should be reordered (defaults to false).
	 * <p>When disabled, each written record will contain values for all columns, in the order they are sent to the writer. Fields which were not selected will not be written but and the record will contain empty values.
	 * <p>When enabled, each written record will contain values only for the selected columns. The values will be ordered according to the selection.
	 *
	 * @return true if the selected fields should be reordered when writing with field selection enabled, false otherwise
	 */
	public boolean isColumnReorderingEnabled() {
		return columnReorderingEnabled;
	}

	/**
	 * Defines whether fields selected using the field selection methods (defined by the parent class {@link CommonSettings}) should be reordered (defaults to false).
	 * <p>When disabled, each written record will contain values for all columns, in the order they are sent to the writer. Fields which were not selected will not be written but and the record will contain empty values.
	 * <p>When enabled, each written record will contain values only for the selected columns. The values will be ordered according to the selection.
	 *
	 * @param columnReorderingEnabled the flag indicating whether or not selected fields should be reordered and written by the writer
	 */
	public void setColumnReorderingEnabled(boolean columnReorderingEnabled) {
		this.columnReorderingEnabled = columnReorderingEnabled;
	}
}
