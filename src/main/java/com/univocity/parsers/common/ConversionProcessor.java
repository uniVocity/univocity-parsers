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
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

/**
 * The base class for {@link RowProcessor} and {@link RowWriterProcessor} implementations that support value conversions provided by {@link Conversion} instances.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class ConversionProcessor {

	private FieldConversionMapping conversions;
	private boolean conversionsInitialized;

	private int[] fieldIndexes;
	private boolean fieldsReordered;

	RowProcessorErrorHandler errorHandler = NoopRowProcessorErrorHandler.instance;
	ParsingContext context;

	/**
	 * Applies a set of {@link Conversion} objects over indexes of a record.
	 *
	 * <p>The idiom to define which indexes should have these conversions applies is as follows:
	 * <hr><blockquote><pre>
	 *
	 * processor.convertIndexes(Conversions.trim(), Conversions.toUpperCase()).add(2, 5); // applies trim and uppercase conversions to fields in indexes 2 and 5
	 * </pre></blockquote><hr>
	 *
	 * @param conversions The sequence of conversions to be executed in a set of field indexes.
	 * @return A {@link FieldSet} for indexes.
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final FieldSet<Integer> convertIndexes(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldIndexes(conversions);
	}

	/**
	 * Applies a set of {@link Conversion} objects over all elements of a record
	 *
	 * @param conversions The sequence of conversions to be executed in all elements of a record
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void convertAll(Conversion... conversions) {
		getConversions().applyConversionsOnAllFields(conversions);
	}

	/**
	 * Applies a set of {@link Conversion} objects over fields of a record by name.
	 *
	 * <p>The idiom to define which fields should have these conversions applied is as follows:
	 * <hr><blockquote><pre>
	 *
	 * processor.convertFields(Conversions.trim(), Conversions.toUpperCase()).add("name", "position"); // applies trim and uppercase conversions to fields with headers "name" and "position"
	 * </pre></blockquote><hr>
	 *
	 * @param conversions The sequence of conversions to be executed in a set of field indexes.
	 * @return A {@link FieldSet} for field names.
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final FieldSet<String> convertFields(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldNames(conversions);
	}

	private FieldConversionMapping getConversions() {
		if (conversions == null) {
			conversions = new FieldConversionMapping();
		}
		return conversions;
	}

	private void initializeConversions(String[] row, ParsingContext context) {
		conversionsInitialized = true;

		this.fieldIndexes = null;
		this.fieldsReordered = false;
		this.conversionsInitialized = false;

		if (context.headers() != null && context.headers().length > 0) {
			conversions.prepareExecution(context.headers());
		} else {
			conversions.prepareExecution(row);
		}

		this.fieldIndexes = context.extractedFieldIndexes();
		this.fieldsReordered = context.columnsReordered();
	}

	/**
	 * Executes the sequences of conversions defined using {@link ConversionProcessor#convertFields(Conversion...)}, {@link ConversionProcessor#convertIndexes(Conversion...)} and {@link ConversionProcessor#convertAll(Conversion...)}, for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#execute(Object)} method.
	 * <p>In general the conversions will process a String and convert it to some object value (such as booleans, dates, etc).
	 *
	 * @param row the parsed record with its individual records as extracted from the original input.
	 * @param context the current state of the parsing process.
	 * @return an row of Object instances containing the values obtained after the execution of all conversions.
	 * <p> Fields that do not have any conversion defined will just be copied to the object array into their original positions.
	 */
	public final Object[] applyConversions(String[] row, ParsingContext context) {
		boolean keepRow = true;
		Object[] objectRow = new Object[row.length];
		System.arraycopy(row, 0, objectRow, 0, row.length);

		if (conversions != null) {
			if (!conversionsInitialized) {
				initializeConversions(row, context);
			}

			final int length = !fieldsReordered && fieldIndexes == null ? objectRow.length : fieldIndexes.length;

			for (int i = 0; i < length; i++) {
				try {
					if (!fieldsReordered) {
						if (fieldIndexes == null) {
							objectRow[i] = conversions.applyConversions(i, row[i]);
						} else {
							int index = fieldIndexes[i];
							objectRow[index] = conversions.applyConversions(index, row[index]);
						}
					} else {
						objectRow[i] = conversions.applyConversions(fieldIndexes[i], row[i]);
					}
				} catch (Throwable ex) {
					keepRow = false;
					handleConversionError(ex, objectRow, i);
				}
			}
		}

		if (keepRow) {
			return objectRow;
		}

		return null;
	}

	/**
	 *
	 * Executes the sequences of reverse conversions defined using {@link ConversionProcessor#convertFields(Conversion...)}, {@link ConversionProcessor#convertIndexes(Conversion...)} and {@link ConversionProcessor#convertAll(Conversion...)}, for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#revert(Object)} method.
	 * <p>In general the conversions will process an Object (such as a Boolean, Date, etc), and convert it to a String representation.
	 *
	 * @param executeInReverseOrder flag to indicate whether the conversion sequence should be executed in the reverse order of its declaration.
	 * @param row the row of objects that will be converted
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 *
	 * @return {@code true} if the the row should be discarded
	 */
	public final boolean reverseConversions(boolean executeInReverseOrder, Object[] row, String[] headers, int[] indexesToWrite) {
		boolean keepRow = true;
		if (conversions != null) {
			if (!conversionsInitialized) {
				conversionsInitialized = true;
				conversions.prepareExecution(headers);
				this.fieldIndexes = indexesToWrite;
			}

			final int last = fieldIndexes == null ? row.length : fieldIndexes.length;
			for (int i = 0; i < last; i++) {
				try {
					if (fieldIndexes == null) {
						row[i] = conversions.reverseConversions(executeInReverseOrder, i, row[i]);
					} else {
						int index = fieldIndexes[i];
						row[index] = conversions.reverseConversions(executeInReverseOrder, index, row[index]);
					}
				} catch (Throwable ex) {
					keepRow = false;
					handleConversionError(ex, row, i);
				}
			}
		}
		return keepRow;
	}

	protected final void handleConversionError(Throwable ex, Object[] row, int column) {
		DataProcessingException error;
		if (ex instanceof DataProcessingException) {
			error = (DataProcessingException) ex;
			error.setRow(row);
			error.setColumnIndex(column);
		} else {
			error = new DataProcessingException("Error processing data conversions", column, row, ex);
		}
		error.markAsNonFatal();
		error.setContext(context);
		errorHandler.handleError(error, row, context);
	}
}
