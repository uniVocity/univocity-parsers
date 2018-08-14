/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

import java.util.*;
import java.util.regex.*;

/**
 * Performs one or more validations against the values of a given record.
 */
public class ValidatedConversion implements Conversion<Object, Object> {

	private final String regexToMatch;
	private final boolean nullable;
	private final boolean allowBlanks;
	private final Set<String> oneOf;
	private final Set<String> noneOf;
	private final Matcher matcher;
	private final Validator[] validators;

	public ValidatedConversion() {
		this(false, false, null, null, null);
	}

	public ValidatedConversion(String regexToMatch) {
		this(false, false, null, null, regexToMatch);
	}

	public ValidatedConversion(boolean nullable, boolean allowBlanks) {
		this(nullable, allowBlanks, null, null, null);
	}

	public ValidatedConversion(boolean nullable, boolean allowBlanks, String[] oneOf, String[] noneOf, String regexToMatch) {
		this(nullable, allowBlanks, oneOf, noneOf, regexToMatch, null);
	}


	public ValidatedConversion(boolean nullable, boolean allowBlanks, String[] oneOf, String[] noneOf, String regexToMatch, Class[] validators) {
		this.regexToMatch = regexToMatch;
		this.matcher = regexToMatch == null || regexToMatch.isEmpty() ? null : Pattern.compile(regexToMatch).matcher("");
		this.nullable = nullable;
		this.allowBlanks = allowBlanks;
		this.oneOf = oneOf == null || oneOf.length == 0 ? null : new HashSet<String>(Arrays.asList(oneOf));
		this.noneOf = noneOf == null || noneOf.length == 0 ? null : new HashSet<String>(Arrays.asList(noneOf));
		this.validators = validators == null || validators.length == 0 ? new Validator[0] : instantiateValidators(validators);
	}

	private Validator[] instantiateValidators(Class[] validators) {
		Validator[] out = new Validator[validators.length];

		for (int i = 0; i < validators.length; i++) {
			out[i] = (Validator) AnnotationHelper.newInstance(Validator.class, validators[i], ArgumentUtils.EMPTY_STRING_ARRAY);
		}

		return out;
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

	protected void validate(Object value) {
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

			if (matcher != null && e == null) {
				boolean match;
				synchronized (matcher) {
					match = matcher.reset(str).matches();
				}
				if (!match) {
					e = new DataValidationException("Value '{value}' does not match expected pattern: '" + regexToMatch + "'");
				}
			}
		}

		if (oneOf != null && !oneOf.contains(str)) {
			e = new DataValidationException("Value '{value}' is not allowed. Expecting one of: " + oneOf);
		}

		if (e == null && noneOf != null && noneOf.contains(str)) {
			e = new DataValidationException("Value '{value}' is not allowed.");
		}

		for (int i = 0; e == null && i < validators.length; i++) {
			String error = validators[i].validate(value);
			if (error != null && !error.trim().isEmpty()) {
				e = new DataValidationException("Value '{value}' didn't pass validation: " + error);
			}
		}

		if (e != null) {
			e.setValue(value);
			throw e;
		}
	}
}
