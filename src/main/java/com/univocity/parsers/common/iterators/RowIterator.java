/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.iterators;

import com.univocity.parsers.common.*;

import java.io.*;

/**
 * An iterator of {@code String[]}. Created when {@link AbstractParser#iterate(File)}
 * (and its overloaded counterparts) is called
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class RowIterator extends ParserIterator<String[]> {

	public RowIterator(AbstractParser parser) {
		super(parser);
	}

	@Override
	protected final String[] nextResult() {
		return parser.parseNext();
	}
}
