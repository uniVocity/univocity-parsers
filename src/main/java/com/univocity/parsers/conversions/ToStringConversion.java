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

package com.univocity.parsers.conversions;

/**
 * Converts any non-null object to its String representation.
 */
public class ToStringConversion extends NullConversion<Object, Object> {

	public ToStringConversion() {
	}

	public ToStringConversion(Object valueOnNullInput, Object valueOnNullOutput) {
		super(valueOnNullInput, valueOnNullOutput);
	}

	@Override
	protected Object fromInput(Object input) {
		if (input != null) {
			return input.toString();
		}
		return null;
	}

	@Override
	protected Object undo(Object input) {
		return execute(input);
	}
}
