/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
package com.univocity.parsers.common.input;

import com.univocity.parsers.common.*;

/**
 * A (very) basic character input definition.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Format
 */
public interface CharInput {

	/**
	 * Returns the next character in the input.
	 *
	 * @return the next character in the input. '\0' if there are no more characters in the input or if the CharInput is stopped.
	 */
	char nextChar();

	/**
	 * Returns the last character returned by the {@link #nextChar()} method.
	 *
	 * @return the last character returned by the {@link #nextChar()} method.'\0' if there are no more characters in the input or if the CharInput is stopped.
	 */
	char getChar();

}