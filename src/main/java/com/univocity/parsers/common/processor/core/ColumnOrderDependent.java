/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.processor.*;

/**
 * A marker interface used by special implementations of {@link RowProcessor} to indicate columns should not
 * be reordered by the parser. Conflicting settings provided in {@link com.univocity.parsers.common.CommonParserSettings#setColumnReorderingEnabled(boolean)} will be prevented.
 * <p> This marker is used to configure the parser automatically based on the specific {@link RowProcessor} implementation used.
 */
public interface ColumnOrderDependent {

	/**
	 * Returns a flag indicating whether or not columns should be reordered by the parser
	 *
	 * @return a flag indicating whether or not columns should be reordered by the parser
	 */
	boolean preventColumnReordering();
}
