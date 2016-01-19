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
package com.univocity.parsers.common.fields;

import com.univocity.parsers.common.*;

/**
 * A FieldSelector capable of deselecting fields in a record.
 * <p> This selector stores undesired fields, represented by values of an enumeration,
 * and will return the indexes of those fields that are not part of the selection.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see FieldSelector
 * @see FieldSet
 */
@SuppressWarnings("rawtypes")
public class ExcludeFieldEnumSelector extends FieldSet<Enum> implements FieldSelector {

	private final ExcludeFieldNameSelector names = new ExcludeFieldNameSelector();

	/**
	 * Returns the indexes of any that are part of a sequence of headers but not part of the selection.
	 *
	 * @param headers the sequence of headers that might have some elements selected by this FieldSelector
	 * @return the positions of all elements which were not selected.
	 */
	@Override
	public int[] getFieldIndexes(String[] headers) {
		if(headers == null){
			return null;
		}
		names.set(ArgumentUtils.toArray(this.get()));
		return names.getFieldIndexes(headers);
	}

	@Override
	public String describe() {
		return "undesired " + super.describe();
	}
}
