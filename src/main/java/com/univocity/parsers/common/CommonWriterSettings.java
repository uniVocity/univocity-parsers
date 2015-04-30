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

import com.univocity.parsers.common.processor.*;

/**
 * This is the parent class for all configuration classes used by writers ({@link AbstractWriter})
 *
 * <p>By default, all writers work with, at least, the following configuration options in addition to the ones provided by {@link CommonSettings}:
 *
 * <ul>
 * 	<li><b>rowWriterProcessor:</b> a implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.</li>
 * </ul>
 *
 * @param <F> the format supported by this writer.
 *
 * @see com.univocity.parsers.common.processor.RowWriterProcessor
 * @see com.univocity.parsers.csv.CsvWriterSettings
 * @see com.univocity.parsers.fixed.FixedWidthWriterSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class CommonWriterSettings<F extends Format> extends CommonSettings<F> {

	private RowWriterProcessor<?> rowWriterProcessor;

	private String emptyValue = "";

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
	 * @param rowWriterProcessor the implementation of the interface {@link RowWriterProcessor} which processes input objects into a manageable format for writing.
	 *
	 * @see com.univocity.parsers.common.processor.ObjectRowWriterProcessor
	 * @see com.univocity.parsers.common.processor.BeanWriterProcessor
	 */
	public void setRowWriterProcessor(RowWriterProcessor<?> rowWriterProcessor) {
		this.rowWriterProcessor = rowWriterProcessor;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Empty value", emptyValue);
		out.put("Row processor", rowWriterProcessor == null ? "none" : rowWriterProcessor.getClass().getName());
	}
}
