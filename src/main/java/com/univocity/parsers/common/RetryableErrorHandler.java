package com.univocity.parsers.common;


/**
 * A {@link ProcessorErrorHandler} implementation that gives the user a chance to provide a default value for
 * columns that could not be processed due to an exception, through the method {@link #setDefaultValue(Object)}.
 * This must be called from within the implementation of the
 * {@link #handleError(DataProcessingException, Object[], Context)} method, and will prevent the record from being
 * discarded. The value provided by the user will be assigned to the problematic input row, at the column defined by
 * {@link DataProcessingException#getColumnIndex()}.
 *
 * <strong>NOTE:</strong>If the column index is {@code < 0}, then the record can't be
 * salvaged and it will be discarded regardless of the user calling {@link #setDefaultValue(Object)} or not.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see DataProcessingException
 * @see TextParsingException
 * @see AbstractParser
 * @see AbstractWriter
 * @see CommonSettings
 * @see Context
 */
public abstract class RetryableErrorHandler<T extends Context> implements ProcessorErrorHandler<T> {

	private Object defaultValue;
	private boolean skipRecord = true;

	/**
	 * Assigns a default value to be assigned to the problematic column that raised the current {@link DataProcessingException}.
	 *
	 * The current column is available from the exception itself, i.e. {@link DataProcessingException#getColumnIndex()}.
	 *
	 * <strong>NOTE:</strong>If the column index is {@code < 0}, then the record can't be
	 * salvaged and it will be discarded regardless of the user calling {@link #setDefaultValue(Object)} or not.
	 *
	 * @param defaultValue the value to be used for the current column. It will be discarded after handling the current
	 *                     {@link DataProcessingException}.
	 */
	public final void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
		keepRecord();
	}

	/**
	 * Ignores the {@link DataProcessingException} and instructs the parser/writer to continue processing the record.
	 */
	public final void keepRecord(){
		skipRecord = false;
	}

	/**
	 * Returns the default value to be assigned to the problematic column that raised the current {@link DataProcessingException}.
	 *
	 * The current column is available from the exception itself, i.e. {@link DataProcessingException#getColumnIndex()}.
	 *
	 * <strong>NOTE:</strong>If the column index is {@code < 0}, then the record can't be
	 * salvaged and it will be discarded regardless of the user calling {@link #setDefaultValue(Object)} or not.
	 *
	 * @return the value to be used for the current column. It will be discarded after handling the current
	 * {@link DataProcessingException}.
	 */
	public final Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Prepares this error handler to be executed. This is called automatically by the library prior to invoking
	 * method {@link #handleError(DataProcessingException, Object[], Context)}
	 */
	final void prepareToRun() {
		skipRecord = true;
		defaultValue = null;
	}

	/**
	 * Flag indicating whether the current record will be skipped. Returns {@code true} by default unless
	 * the user invokes {@link #setDefaultValue(Object)} from within the {@link #handleError(DataProcessingException, Object[], Context)}
	 * method implementation, in which case the current record will continue to be processed.
	 *
	 * @return {@code true} if the record originating the current {@link DataProcessingException} should be skipped,
	 * otherwise {@code false}
	 */
	public final boolean isRecordSkipped() {
		return skipRecord;
	}
}
