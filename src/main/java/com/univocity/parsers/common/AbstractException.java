/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

/**
 * Parent class of the Exception classes thrown by uniVocity-parsers. This class provides utility methods to print out the internal state of the parser/writer
 * at the time an error occurred.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
abstract class AbstractException extends RuntimeException {

	private static final long serialVersionUID = -2993096896413328423L;

	protected int errorContentLength = -1;

	protected AbstractException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Returns a detailed message describing the error, and the internal state of the parser/writer.
	 *
	 * @return a detailed message describing the error
	 */
	@Override
	public final String getMessage() {
		String msg = super.getMessage();
		msg = msg == null ? getErrorDescription() + ": " : msg;

		msg = updateMessage(msg);

		String details = getDetails();
		if (details == null || details.isEmpty()) {
			return msg;
		}

		return msg + "\nInternal state when error was thrown: " + details;
	}

	/**
	 * Allows subclasses to alter the exception message that should be displayed to end users.
	 * By default the original message is kept unchanged.
	 *
	 * @param msg the original message
	 * @return the updated message.
	 */
	protected String updateMessage(String msg){
		return msg;
	}

	/**
	 * Subclasses must implement this method to return as much information as possible about the internal state of the parser/writer.
	 * Use {@link #printIfNotEmpty(String, String, Object)} to create a comma-separated list of relevant properties and their (non null) values.
	 *
	 * The result of this method is used by the {@link #getMessage()} method to print out these details after the error message.
	 *
	 * @return a String describing the internal state of the parser/writer.
	 */
	protected abstract String getDetails();

	/**
	 * Returns a generic description of the error. The result of this method is used by {@link #getMessage()} to print out a general description of the error before a detailed message of the root cause.
	 *
	 * @return a generic description of the error.
	 */
	protected abstract String getErrorDescription();

	protected static String printIfNotEmpty(String previous, String description, Object o) {
		String value;
		if (o == null || o.toString().isEmpty()) {
			return previous;
		} else if (o instanceof Number && ((Number) o).intValue() < 0) {
			return previous;
		} else if (o.getClass().isArray()) {
			//noinspection ConstantConditions
			value = Arrays.toString((Object[]) o);
		} else {
			value = String.valueOf(o);
		}

		String out = description + '=' + value;

		if (!previous.isEmpty()) {
			out = previous + ", " + out;
		}
		return out;
	}

	public static String restrictContent(int errorContentLength, CharSequence content) {
		return ArgumentUtils.restrictContent(errorContentLength,content);
	}

	public static Object[] restrictContent(int errorContentLength, Object[] content) {
		if (content == null || errorContentLength == 0) {
			return null;
		 }
		return content;
	}

	public void setErrorContentLength(int errorContentLength) {
		this.errorContentLength = errorContentLength;
		Throwable cause = this.getCause();
		if(cause != null && cause instanceof AbstractException){
			AbstractException e = ((AbstractException) cause);
			if(e.errorContentLength != errorContentLength) { //prevents an unintended recursion cycle.
				e.setErrorContentLength(errorContentLength);
			}
		}
	}

	protected String restrictContent(CharSequence content) {
		return restrictContent(errorContentLength, content);
	}

	protected String restrictContent(Object content) {
		return ArgumentUtils.restrictContent(errorContentLength, content);
	}

	protected Object[] restrictContent(Object[] content) {
		return restrictContent(errorContentLength, content);
	}
}
