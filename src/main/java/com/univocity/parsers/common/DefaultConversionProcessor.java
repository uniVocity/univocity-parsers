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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

import java.util.*;

/**
 * The base class for {@link RowProcessor} and {@link RowWriterProcessor} implementations that support value conversions provided by {@link Conversion} instances.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class DefaultConversionProcessor implements ConversionProcessor {

	private Map<Class<?>, Conversion[]> conversionsByType;
	protected FieldConversionMapping conversions;
	private boolean conversionsInitialized;

	private int[] fieldIndexes;
	private int[] reverseFieldIndexes;
	private boolean fieldsReordered;

	ProcessorErrorHandler errorHandler = NoopProcessorErrorHandler.instance;
	Context context;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public final FieldSet<Integer> convertIndexes(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldIndexes(conversions);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final void convertAll(Conversion... conversions) {
		getConversions().applyConversionsOnAllFields(conversions);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public final FieldSet<String> convertFields(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldNames(conversions);
	}

	private FieldConversionMapping getConversions() {
		if (conversions == null) {
			conversions = new FieldConversionMapping();
		}
		return conversions;
	}

	protected void initializeConversions(String[] row, Context context) {
		conversionsInitialized = true;

		this.fieldIndexes = null;
		this.fieldsReordered = false;

		String[] contextHeaders = context == null ? null : context.headers();
		if (contextHeaders != null && contextHeaders.length > 0) {
			getConversions().prepareExecution(false, contextHeaders);
		} else {
			getConversions().prepareExecution(false, row);
		}
		if (context != null) {
			this.fieldIndexes = context.extractedFieldIndexes();
			this.fieldsReordered = context.columnsReordered();
		}
	}

	/**
	 * Executes the sequences of conversions defined using {@link DefaultConversionProcessor#convertFields(Conversion...)}, {@link DefaultConversionProcessor#convertIndexes(Conversion...)} and {@link DefaultConversionProcessor#convertAll(Conversion...)}, for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#execute(Object)} method.
	 * <p>In general the conversions will process a String and convert it to some object value (such as booleans, dates, etc).
	 *
	 * @param row     the parsed record with its individual records as extracted from the original input.
	 * @param context the current state of the parsing process.
	 *
	 * @return an row of Object instances containing the values obtained after the execution of all conversions.
	 * <p> Fields that do not have any conversion defined will just be copied to the object array into their original positions.
	 */
	public final Object[] applyConversions(String[] row, Context context) {
		boolean keepRow = true;
		Object[] objectRow = new Object[row.length];
		boolean[] convertedFlags = conversionsByType != null ? new boolean[row.length] : null;
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
							objectRow[i] = conversions.applyConversions(i, row[i], convertedFlags);
						} else {
							int index = fieldIndexes[i];
							objectRow[index] = conversions.applyConversions(index, row[index], convertedFlags);
						}
					} else {
						objectRow[i] = conversions.applyConversions(fieldIndexes[i], row[i], convertedFlags);
					}
				} catch (Throwable ex) {
					keepRow = handleConversionError(ex, objectRow, i);
				}
			}
		}

		if (keepRow && convertedFlags != null) {
			keepRow = applyConversionsByType(false, objectRow, convertedFlags);
		}

		if (keepRow && validateAllValues(objectRow)) {
			return objectRow;
		}

		return null;
	}

	private void populateReverseFieldIndexes() {
		int max = 0;
		for (int i = 0; i < fieldIndexes.length; i++) {
			if (fieldIndexes[i] > max) {
				max = fieldIndexes[i];
			}
		}

		reverseFieldIndexes = new int[max + 1];
		Arrays.fill(reverseFieldIndexes, -1);

		for (int i = 0; i < fieldIndexes.length; i++) {
			reverseFieldIndexes[fieldIndexes[i]] = i;
		}
	}

	private boolean validateAllValues(Object[] objectRow) {
		if (conversions != null && conversions.validatedIndexes != null) {
			boolean keepRow = true;
			for (int i = 0; keepRow && i < conversions.validatedIndexes.length; i++) {
				final int index = conversions.validatedIndexes[i];

				int valueIndex = index;
				if (fieldsReordered) {
					if (reverseFieldIndexes == null) {
						populateReverseFieldIndexes();
					}
					valueIndex = reverseFieldIndexes[index];
				}

				try {
					Object value = index < objectRow.length ? objectRow[valueIndex] : null;
					conversions.executeValidations(index, value);
				} catch (Throwable ex) {
					keepRow = handleConversionError(ex, objectRow, index);
				}
			}
			return keepRow;
		}
		return true;
	}

	/**
	 * Executes the sequences of reverse conversions defined using {@link DefaultConversionProcessor#convertFields(Conversion...)}, {@link DefaultConversionProcessor#convertIndexes(Conversion...)} and {@link DefaultConversionProcessor#convertAll(Conversion...)}, for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#revert(Object)} method.
	 * <p>In general the conversions will process an Object (such as a Boolean, Date, etc), and convert it to a String representation.
	 *
	 * @param executeInReverseOrder flag to indicate whether the conversion sequence should be executed in the reverse order of its declaration.
	 * @param row                   the row of objects that will be converted
	 * @param headers               All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite        The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 *
	 * @return {@code true} if the the row should be discarded
	 */
	public final boolean reverseConversions(boolean executeInReverseOrder, Object[] row, NormalizedString[] headers, int[] indexesToWrite) {
		boolean keepRow = true;
		boolean[] convertedFlags = conversionsByType != null ? new boolean[row.length] : null;
		if (conversions != null) {
			if (!conversionsInitialized) {
				conversionsInitialized = true;
				conversions.prepareExecution(true, headers != null ? NormalizedString.toArray(headers) : new String[row.length]);
				this.fieldIndexes = indexesToWrite;
			}

			if (executeInReverseOrder) {
				keepRow = validateAllValues(row);
			}


			final int last = fieldIndexes == null ? row.length : fieldIndexes.length;

			for (int i = 0; i < last; i++) {
				try {
					if (fieldIndexes == null || fieldIndexes[i] == -1) {
						row[i] = conversions.reverseConversions(executeInReverseOrder, i, row[i], convertedFlags);
					} else {
						int index = fieldIndexes[i];
						row[index] = conversions.reverseConversions(executeInReverseOrder, index, row[index], convertedFlags);
					}
				} catch (Throwable ex) {
					keepRow = handleConversionError(ex, row, i);
				}
			}
		}

		if (keepRow && convertedFlags != null) {
			keepRow = applyConversionsByType(true, row, convertedFlags);
		}

		if (executeInReverseOrder) {
			return keepRow;
		}

		return keepRow && validateAllValues(row);
	}

	private boolean applyConversionsByType(boolean reverse, Object[] row, boolean[] convertedFlags) {
		boolean keepRow = true;
		for (int i = 0; i < row.length; i++) {
			if (convertedFlags[i]) {
				continue; //conversions already applied. Prevent default type conversion.
			}
			try {
				row[i] = applyTypeConversion(reverse, row[i]);
			} catch (Throwable ex) {
				keepRow = handleConversionError(ex, row, i);
			}
		}
		return keepRow;
	}

	/**
	 * Handles an error that occurred when applying conversions over a value. If the user defined a
	 * {@link ProcessorErrorHandler} the user will receive the exception and is able to determine whether or not
	 * processing should continue, discarding the record. If the error handler is an instance of
	 * {@link RetryableErrorHandler}, the user can provide a default value to use in place of the one that could not
	 * be converted, and decide whether or not the record should be kept with the use of the
	 * {@link RetryableErrorHandler#keepRecord()} method.
	 *
	 * @param ex     the exception that occurred when applying a conversion
	 * @param row    the record being processed at the time the exception happened.
	 * @param column the column if the given row, whose value could not be converted. If negative, it's not possible to
	 *               keep the record.
	 *
	 * @return {@code true} if the error has been handled by the user and the record can still be processed, otherwise
	 * {@code false} if the record should be discarded.
	 */
	protected final boolean handleConversionError(Throwable ex, Object[] row, int column) {
		if (row != null && row.length < column) {
			//expand row so column index won't make error handlers blow up.
			row = Arrays.copyOf(row, column + 1);
		}
		DataProcessingException error = toDataProcessingException(ex, row, column);

		if (column > -1 && errorHandler instanceof RetryableErrorHandler) {
			((RetryableErrorHandler) errorHandler).prepareToRun();
		}

		error.markAsHandled(errorHandler);
		errorHandler.handleError(error, row, context);

		if (column > -1 && errorHandler instanceof RetryableErrorHandler) {
			RetryableErrorHandler retry = ((RetryableErrorHandler) errorHandler);
			Object defaultValue = retry.getDefaultValue();
			row[column] = defaultValue;
			return !retry.isRecordSkipped();
		}
		return false;
	}

	protected DataProcessingException toDataProcessingException(Throwable ex, Object[] row, int column) {
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
		return error;
	}

	@Override
	public final void convertType(Class<?> type, Conversion... conversions) {
		ArgumentUtils.noNulls("Type to convert", type);
		ArgumentUtils.noNulls("Sequence of conversions to apply over data of type " + type.getSimpleName(), conversions);

		if (conversionsByType == null) {
			conversionsByType = new HashMap<Class<?>, Conversion[]>();
		}

		conversionsByType.put(type, conversions);
	}

	private Object applyTypeConversion(boolean revert, Object input) {
		if (conversionsByType == null || input == null) {
			return input;
		}

		Conversion[] conversionSequence = conversionsByType.get(input.getClass());
		if (conversionSequence == null) {
			return input;
		}

		if (revert) {
			for (int i = 0; i < conversionSequence.length; i++) {
				input = conversionSequence[i].revert(input);
			}
		} else {
			for (int i = 0; i < conversionSequence.length; i++) {
				input = conversionSequence[i].execute(input);
			}
		}
		return input;
	}
}
