/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.common;

/**
 * A {@code DataValidationException} is an error thrown during the processing of a record successfully parsed,
 * but whose data failed to pass a validation defined by annotation {@link com.univocity.parsers.annotations.Validate}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class DataValidationException extends DataProcessingException {

	private static final long serialVersionUID = 3110975527111918123L;

	/**
	 * Creates a new validation exception with an error message only.
	 *
	 * @param message the error message
	 */
	public DataValidationException(String message) {
		super(message, -1, null, null);
	}

	/**
	 * Creates a new validation exception with an error message and error cause
	 *
	 * @param message the error message
	 * @param cause   the cause of the error
	 */
	public DataValidationException(String message, Throwable cause) {
		super(message, -1, null, cause);
	}

	/**
	 * Creates a new validation exception with an error message and the row that could not be validated.
	 *
	 * @param message the error message
	 * @param row     the row that could not be processed.
	 */
	public DataValidationException(String message, Object[] row) {
		super(message, -1, row, null);
	}

	/**
	 * Creates a new validation exception with an error message, the row that could not be validated, and the error cause.
	 *
	 * @param message the error message
	 * @param row     the row that could not be processed.
	 * @param cause   the cause of the error
	 */
	public DataValidationException(String message, Object[] row, Throwable cause) {
		super(message, -1, row, cause);
	}

	/**
	 * Creates a new validation exception with an error message and the column that could not be validated.
	 *
	 * @param message     the error message
	 * @param columnIndex index of the column that could not be validated.
	 */
	public DataValidationException(String message, int columnIndex) {
		super(message, columnIndex, null, null);
	}

	@Override
	protected String getErrorDescription() {
		return "Error validating parsed input";
	}
}
