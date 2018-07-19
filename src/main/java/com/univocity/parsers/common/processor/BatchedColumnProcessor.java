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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

/**
 * A {@link RowProcessor} implementation that stores values of columns in batches. Use this implementation in favor of {@link ColumnProcessor}
 * when processing large inputs to avoid running out of memory.
 *
 * Values parsed in each row will be split into columns of Strings. Each column has its own list of values.
 *
 * <p> During the execution of the process, the {@link #batchProcessed(int)} method will be invoked after a given number of rows has been processed.</p>
 * <p> The user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 * <p> After {@link #batchProcessed(int)} is invoked, all values will be discarded and the next batch of column values will be accumulated.
 * This process will repeat until there's no more rows in the input.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @see AbstractParser
 * @see RowProcessor
 * @see AbstractBatchedColumnProcessor
 */
public abstract class BatchedColumnProcessor extends AbstractBatchedColumnProcessor<ParsingContext> implements RowProcessor {

	/**
	 * Constructs a batched column processor configured to invoke the {@link #batchesProcessed} method after a given number of rows has been processed.
	 * @param rowsPerBatch the number of rows to process in each batch.
	 */
	public BatchedColumnProcessor(int rowsPerBatch) {
		super(rowsPerBatch);
	}


}
