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
package com.univocity.parsers.conversions;

import com.univocity.parsers.annotations.*;

/**
 * This interface identifies conversions associated with the {@link Format} annotation.
 * It is used when {@link Format#options()} is defined to set any give properties of the underlying formatter.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @param <T> The type of the formatter object (typically {@link java.text.DecimalFormat} for numeric values, and {@link java.text.SimpleDateFormat} for dates)
 *
 */
public interface FormattedConversion<T> {

	/**
	 * Returns the formatter objects
	 * @return the formatter objects used to apply formatting to values to generate formatted Strings, and parsing formatted Strings into values
	 */
	T[] getFormatterObjects();
}
