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
package com.univocity.parsers.conversions;

import com.univocity.parsers.annotations.EnumOptions;

/**
 * Identifies a property of an enumeration that should be used by {@link EnumOptions} to identify an input value.
 * When parsing a given input, values will be compared against one of the properties and if there's a match, the
 * corresponding enumeration value will be used to set the field of an annotated class.
 */
public enum EnumSelector {

	/**
	 * Matches the result of {@link Enum#ordinal()}
	 */
	ORDINAL,

	/**
	 * Matches the result of {@link Enum#name()}
	 */
	NAME,

	/**
	 * Matches the result of {@link Enum#toString()} ()}
	 */
	STRING,

	/**
	 * Matches the value of a field of the annotated enumeration
	 */
	CUSTOM_FIELD,

	/**
	 * Matches the value of a method of the annotated enumeration
	 */
	CUSTOM_METHOD
}
