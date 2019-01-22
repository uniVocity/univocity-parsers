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
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;

/**
 * The essential callback interface to convert input objects into a manageable format for writing. Used by any writer that extends {@link AbstractWriter}.
 *
 * <p>When writing to an output, the writer will obtain the RowWriterProcessor from {@link CommonWriterSettings#getRowWriterProcessor()}, and
 * invoke {@link RowWriterProcessor#write(Object, NormalizedString[], int[])} to convert the input to an array of objects. This array of objects will in turn be handed to the writer to produce a record in the expected format.
 *
 * <p>univocity-parsers provides some useful default implementations of this interface in the package {@link com.univocity.parsers.common.processor}, namely:
 *
 * <ul>
 * <li>{@link ObjectRowWriterProcessor}: used for executing conversions of Object values on input rows using instances of {@link Conversion} before writing to the output</li>
 * <li>{@link BeanWriterProcessor}: used for converting javabeans annotated with the annotations provided in package {@link com.univocity.parsers.annotations} into an object row before writing to the output</li>
 * </ul>
 *
 * @see com.univocity.parsers.common.AbstractWriter
 * @see com.univocity.parsers.common.CommonWriterSettings
 *
 * @param <T> the type that is converted by this implementation into an Object array, suitable for writing to the output.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface RowWriterProcessor<T> {

	/**
	 *
	 * Converts the given input into an Object array that is suitable for writing. Used by format-specific writers that extend {@link AbstractWriter}.
	 *
	 * @param input The original input record that must be converted into an Object array before writing to an output.
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return an Object array that is suitable for writing. If null or an empty array is returned then the writer might either skip this value or write an empty record (if {@link CommonSettings#getSkipEmptyLines()} is false)
	 *
	 * @see CsvWriter
	 * @see FixedWidthWriter
	 * @see CommonSettings
	 * @see AbstractWriter
	 */
	Object[] write(T input, NormalizedString[] headers, int[] indexesToWrite);
}
