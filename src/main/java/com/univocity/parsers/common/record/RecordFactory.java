/*
 * Copyright 2015 Univocity Software Pty Ltd
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
 */

package com.univocity.parsers.common.record;

import com.univocity.parsers.common.*;

/**
 * A factory class that provides implementations of {@link Record} based on the current state
 * of an {@link AbstractParser}(via its {@link ParsingContext}), and raw input records.
 */
public class RecordFactory extends AbstractRecordFactory<Record, RecordMetaDataImpl> {

	/**
	 * Creates a new factory of {@link Record} based the state of a parser
	 *
	 * @param context            the parser context
	 */
	public RecordFactory(Context context) {
		super(context);
	}

	/**
	 * Creates a new {@link Record} with a row parsed from the input
	 *
	 * @param data the row parsed from the input
	 *
	 * @return a {@link Record} that provides many utility methods for consuming the data collected for a record parsed from the input.
	 */
	@Override
	public Record newRecord(String[] data) {
		return new RecordImpl(data, metaData);
	}

	@Override
	public RecordMetaDataImpl createMetaData(Context context) {
		return new RecordMetaDataImpl(context);
	}
}
