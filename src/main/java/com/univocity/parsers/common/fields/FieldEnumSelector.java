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

package com.univocity.parsers.common.fields;

import com.univocity.parsers.common.*;

/**
 * A FieldSelector capable of selecting fields represented by values of an enumeration type.
 * The {@code toString()} output of the enumeration value will be used to match name of the fields.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see FieldSelector
 * @see FieldSet
 */
@SuppressWarnings("rawtypes")
public class FieldEnumSelector extends FieldSet<Enum> implements FieldSelector {

	private final FieldNameSelector names = new FieldNameSelector();

	/**
	 * Returns the position of a given column represented by an enumeration value.
	 *
	 * @param column the column whose position will be returned
	 * @return the position of the given column.
	 */
	public int getFieldIndex(Enum column) {
		return names.getFieldIndex(column.toString());
	}

	@Override
	public int[] getFieldIndexes(String[] headers) {
		if(headers == null){
			return null;
		}
		names.set(ArgumentUtils.toArray(this.get()));
		return names.getFieldIndexes(headers);
	}

}
