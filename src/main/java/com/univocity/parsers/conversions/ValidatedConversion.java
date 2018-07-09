/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
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

package com.univocity.parsers.conversions;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * Performs one or more validations against the values of a given record.
 */
public class ValidatedConversion implements Conversion<Object, Object> {

	private final boolean nullable;
	private final boolean allowBlanks;
	private final Set<String> oneOf;
	private final Set<String> noneOf;

	public ValidatedConversion(boolean nullable, boolean allowBlanks) {
		this(nullable, allowBlanks, null, null);
	}


	public ValidatedConversion(boolean nullable, boolean allowBlanks, String[] oneOf, String[] noneOf) {
		this.nullable = nullable;
		this.allowBlanks = allowBlanks;
		this.oneOf = oneOf == null || oneOf.length == 0 ? null : new HashSet<String>(Arrays.asList(oneOf));
		this.noneOf = noneOf == null || noneOf.length == 0 ? null : new HashSet<String>(Arrays.asList(noneOf));
	}

	@Override
	public Object execute(Object input) {
		validate(input);
		return input;
	}

	@Override
	public Object revert(Object input) {
		validate(input);
		return input;
	}

	private void validate(Object value) {
		DataValidationException e = null;
		String str = null;
		if (value == null) {
			if (nullable) {
				if (noneOf != null && noneOf.contains(null)) {
					e = new DataValidationException("Value '{value}' is not allowed.");
				} else {
					return;
				}
			} else {
				if (oneOf != null && oneOf.contains(null)) {
					return;
				} else {
					e = new DataValidationException("Null values not allowed.");
				}
			}
		} else {
			str = String.valueOf(value);
			if (str.trim().isEmpty()) {
				if (allowBlanks) {
					if (noneOf != null && noneOf.contains(str)) {
						e = new DataValidationException("Value '{value}' is not allowed.");
					} else {
						return;
					}
				} else {
					if (oneOf != null && oneOf.contains(str)) {
						return;
					} else {
						e = new DataValidationException("Blanks are not allowed. '{value}' is blank.");
					}
				}
			}
		}

		if (oneOf != null && !oneOf.contains(str)) {
			e = new DataValidationException("Value '{value}' is not allowed. Expecting one of: " + oneOf);
		}

		if (e == null && noneOf != null && noneOf.contains(str)) {
			e = new DataValidationException("Value '{value}' is not allowed.");
		}

		if (e != null) {
			e.setValue(value);
			throw e;
		}
	}
}
