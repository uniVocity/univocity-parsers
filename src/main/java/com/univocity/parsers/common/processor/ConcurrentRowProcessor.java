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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

/**
 * A {@link RowProcessor} implementation to perform row processing tasks in parallel. The {@code ConcurrentRowProcessor} wraps another {@link RowProcessor}, and collects rows read from the input.
 * The actual row processing is performed in by wrapped {@link RowProcessor} in a separate thread.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see RowProcessor
 */
public class ConcurrentRowProcessor extends AbstractConcurrentProcessor<ParsingContext> implements RowProcessor {


	/**
	 * Creates a non-blocking {@code ConcurrentRowProcessor}, to perform processing of rows parsed from the input in a separate thread.
	 *
	 * @param rowProcessor a regular {@link RowProcessor} implementation which will be executed in a separate thread.
	 */
	public ConcurrentRowProcessor(RowProcessor rowProcessor) {
		super(rowProcessor);
	}

	/**
	 * Creates a blocking {@code ConcurrentRowProcessor}, to perform processing of rows parsed from the input in a separate thread.
	 *
	 * @param rowProcessor a regular {@link RowProcessor} implementation which will be executed in a separate thread.
	 * @param limit        the limit of rows to be kept in memory before the input parsing process is blocked.
	 */
	public ConcurrentRowProcessor(RowProcessor rowProcessor, int limit) {
		super(rowProcessor, limit);
	}

	@Override
	protected ParsingContext copyContext(ParsingContext context) {
		return new ParsingContextSnapshot(context);
	}

	@Override
	protected ParsingContext wrapContext(ParsingContext context) {
		return new ParsingContextWrapper(context) {
			@Override
			public long currentRecord() {
				return getRowCount();
			}
		};
	}
}
