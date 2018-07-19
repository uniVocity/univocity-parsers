/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
 * A utility {@link RowProcessor} implementation that facilitates using multiple implementations of {@link RowProcessor} at the
 * same time.
 */
public class CompositeRowProcessor extends CompositeProcessor<ParsingContext> implements RowProcessor {

	/**
	 * Creates a new {@code CompositeProcessor} with the list of {@link Processor} implementations to be used.
	 *
	 * @param processors the sequence of {@link Processor} implementations to be used.
	 */
	public CompositeRowProcessor(Processor... processors) {
		super(processors);
	}
}