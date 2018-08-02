/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

/**
 * Defines a custom validation process to be executed when reading or writing
 * values into a field of a java bean that is annotated with {@link com.univocity.parsers.annotations.Validate}
 *
 * @param <T> the expected type of the value to be validated
 */
public interface Validator<T> {

	/**
	 * Executes the required validations over a given value, returning
	 * any validation error messages that are applicable.
	 *
	 * If no validation errors are found, returns a blank {@code String} or {@code null}
	 *
	 * @param value the value to be validated
	 * @return a validation error message if the given value fails the validation process.
	 *         If the value is acceptable this method can return either a blank {@code String} or {@code null}
	 */
	String validate(T value);
}

