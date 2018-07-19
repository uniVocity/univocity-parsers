/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

/**
 * An (singleton) implementation of {@link ProcessorErrorHandler} that simply rethrows any {@link DataProcessingException}
 * that comes into its {@link #handleError(DataProcessingException, Object[], Context)}} method
 *
 * @see ProcessorErrorHandler
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @param <T> the {@code Context} type provided by the parser implementation.
 *
 */
public final class NoopProcessorErrorHandler<T extends Context> implements ProcessorErrorHandler<T> {

	public static final ProcessorErrorHandler instance = new NoopProcessorErrorHandler();

	private NoopProcessorErrorHandler() {
	}

	/**
	 * Rethrows the {@link DataProcessingException}
	 *
	 * @param error the exception thrown during the processing an input record. Will be rethrown to abort the parsing process.
	 * @param inputRow ignored
	 * @param context ignored
	 */
	@Override
	public void handleError(DataProcessingException error, Object[] inputRow, T context) {
		throw error;
	}
}
