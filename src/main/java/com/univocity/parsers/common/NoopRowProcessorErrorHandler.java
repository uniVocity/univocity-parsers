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
 * An (singleton) implementation of {@link RowProcessorErrorHandler} that simply rethrows any {@link DataProcessingException}
 * that comes into its {@link #handleError(DataProcessingException, Object[], ParsingContext)} method
 *
 * @see RowProcessorErrorHandler
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
final class NoopRowProcessorErrorHandler implements RowProcessorErrorHandler {

	public static final RowProcessorErrorHandler instance = new NoopRowProcessorErrorHandler();

	private NoopRowProcessorErrorHandler() {
	}

	/**
	 * Rethrows the {@link DataProcessingException}
	 */
	@Override
	public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
		throw error;
	}
}
