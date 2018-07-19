/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
import com.univocity.parsers.common.record.*;

import java.io.*;

/**
 * An iterator of {@link Record}s. Created when {@link AbstractParser#iterateRecords(File)}
 * (and its overloaded counterparts) is called
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class RecordIterator extends ParserIterator<Record> {

	public RecordIterator(AbstractParser parser) {
		super(parser);
	}

	@Override
	protected final Record nextResult() {
		return parser.parseNextRecord();
	}
}
