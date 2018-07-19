/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
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

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

/**
 * A basic interface for classes that associate {@link Conversion} implementations with fields of a given input/output.
 */
public interface ConversionProcessor {
	/**
	 * Applies a set of {@link Conversion} objects over indexes of a record.
	 *
	 * <p>The idiom to define which indexes should have these conversions applies is as follows:
	 * <hr><blockquote><pre>
	 *
	 * processor.convertIndexes(Conversions.trim(), Conversions.toUpperCase()).add(2, 5); // applies trim and uppercase conversions to fields in indexes 2 and 5
	 * </pre></blockquote><hr>
	 *
	 * @param conversions The sequence of conversions to be executed in a set of field indexes.
	 *
	 * @return A {@link FieldSet} for indexes.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	FieldSet<Integer> convertIndexes(Conversion... conversions);

	/**
	 * Applies a set of {@link Conversion} objects over all elements of a record
	 *
	 * @param conversions The sequence of conversions to be executed in all elements of a record
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	void convertAll(Conversion... conversions);

	/**
	 * Applies a set of {@link Conversion} objects over fields of a record by name.
	 *
	 * <p>The idiom to define which fields should have these conversions applied is as follows:
	 * <hr><blockquote><pre>
	 *
	 * processor.convertFields(Conversions.trim(), Conversions.toUpperCase()).add("name", "position"); // applies trim and uppercase conversions to fields with headers "name" and "position"
	 * </pre></blockquote><hr>
	 *
	 * @param conversions The sequence of conversions to be executed in a set of field indexes.
	 *
	 * @return A {@link FieldSet} for field names.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	FieldSet<String> convertFields(Conversion... conversions);

	/**
	 * Applies a sequence of conversions over values of a given type. Used for writing.
	 * @param type the type over which a sequence of conversions should be applied
	 * @param conversions the sequence of conversions to apply over values of the given type.
	 */
	void convertType(Class<?> type, Conversion... conversions);
}
