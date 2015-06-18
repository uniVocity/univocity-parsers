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
package com.univocity.parsers.common;

import java.util.*;

/**
 * A concrete (dummy) implementation of {@code Format}. Used by {@link AbstractWriter} to manage its internal configuration of field selections
 *
 *  @see AbstractWriter
 *  @see CommonSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
final class DummyFormat extends Format {

	static final DummyFormat instance = new DummyFormat();

	private DummyFormat() {
	}

	@Override
	protected final TreeMap<String, Object> getConfiguration() {
		return new TreeMap<String, Object>();
	}

}
