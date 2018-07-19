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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.common.processor.core.*;

/**
 * The {@code ProcessorErrorHandler} is a callback used by the parser/writer to handle non-fatal {@link DataProcessingException}s that may occur when
 * processing rows using a {@link Processor} or {@link RowWriterProcessor}. This leaves the responsibility of error handling to the user. If the user does not
 * rethrow the {@code DataProcessingException}, the parsing/writing process won't stop and will proceed normally.
 *
 * <p>This error handler WILL NOT handle {@code TextParsingException}s or other errors that prevent the parser to reliably extract rows from a given input,
 * or the writer to proceed writing data. </p>
 *
 * <p>When parsing, the {@link #handleError(DataProcessingException, Object[], Context)} method will be called only when a valid record has been parsed, but the
 * subsequent processing executed by a {@link Processor} fails.</p>
 *
 * <p>When writing, the {@link #handleError(DataProcessingException, Object[], Context)} method will be called only when a using
 * the {@link AbstractWriter#processRecord(Object)} methods, and {@link RowWriterProcessor} fails to execute.</p>
 *
 * @see RowProcessor
 * @see RowWriterProcessor
 * @see DataProcessingException
 * @see TextParsingException
 * @see AbstractParser
 * @see AbstractWriter
 * @see CommonSettings
 * @see Context
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface ProcessorErrorHandler<T extends Context> {

	/**
	 * Handles non-fatal instances of {@code DataProcessingException} that are thrown by a {@link Processor} while processing a record parsed from the input,
	 * or from a {@link RowWriterProcessor} when processing records for writing.
	 *
	 * @param error the exception thrown during the processing an input record. Rethrow the error to abort the parsing process.
	 * 	When parsing, you can also invoke {@link ParsingContext#stop()} to stop the parser silently.
	 * @param inputRow the record that could not be processed. When writing, the original input object (i.e. {@code null}, java bean or object array) will be sent by the writer.
	 * @param context the parsing context with information about the state of the parser at the time the error occurred. Will be null when writing.
	 */
	void handleError(DataProcessingException error, Object[] inputRow, T context);

}
