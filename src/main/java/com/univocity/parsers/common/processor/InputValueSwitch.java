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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

/**
 * A concrete implementation of {@link RowProcessorSwitch} that allows switching among different implementations of
 * {@link RowProcessor} based on values found on the rows parsed from the input.
 */
public class InputValueSwitch extends AbstractInputValueSwitch<ParsingContext> implements RowProcessor{
	/**
	 * Creates a switch that will analyze the first column of rows found in the input to determine which
	 * {@link RowProcessor} to use for each parsed row
	 */
	public InputValueSwitch() {
		this(0);
	}

	/**
	 * Creates a switch that will analyze a column of rows parsed from the input to determine which
	 * {@link RowProcessor} to use.
	 *
	 * @param columnIndex the column index whose value will be used to determine which {@link RowProcessor} to use for each parsed row.
	 */
	public InputValueSwitch(int columnIndex) {
		super(columnIndex);
	}

	/**
	 * Creates a switch that will analyze a column in rows parsed from the input to determine which
	 * {@link RowProcessor} to use.
	 *
	 * @param columnName name of the column whose values will be used to determine which {@link RowProcessor} to use for each parsed row.
	 */
	public InputValueSwitch(String columnName) {
		super(columnName);
	}

}
