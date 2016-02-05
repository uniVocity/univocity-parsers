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
package com.univocity.parsers.fixed;

import java.util.*;

/**
 * This class provides the name, length, alignment and padding of each field in a fixed-width record.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @deprecated This class has been modified over time and its name became misleading. Use {@link FixedWidthFields} instead.
 */


@Deprecated
public class FixedWidthFieldLengths extends FixedWidthFields {
	/**
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}.
	 *
	 * @param fields a {@link LinkedHashMap} containing the sequence of fields to be associated with each column in the input/output, with their respective length.
	 */
	public FixedWidthFieldLengths(LinkedHashMap<String, Integer> fields) {
		super(fields);
	}

	/**
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}.
	 *
	 * @param headers the sequence of fields to be associated with each column in the input/output
	 * @param lengths the sequence of lengths to be associated with each given header. The size of this array must match the number of given headers.
	 */
	public FixedWidthFieldLengths(String[] headers, int[] lengths) {
		super(headers, lengths);
	}

	/**
	 * Creates a new instance initialized with the lengths of all fields in a fixed-width record.
	 *
	 * @param fieldLengths The number lengths of all fields in a fixed-width record. All lengths must be greater than 0.
	 */
	public FixedWidthFieldLengths(int... fieldLengths) {
		super(fieldLengths);
	}
}
