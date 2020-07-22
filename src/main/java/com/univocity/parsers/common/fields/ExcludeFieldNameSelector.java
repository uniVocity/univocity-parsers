/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import java.util.*;

/**
 * A FieldSelector capable of deselecting fields by their name in a record.
 *
 * <p> This selector stores undesired fields and will return the indexes of those fields that are not part of the selection.
 *
 * @see FieldSelector
 * @see FieldSet
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class ExcludeFieldNameSelector extends FieldSet<String> implements FieldSelector, Cloneable {

	/**
	 * Returns the indexes of any that are part of a sequence of headers but not part of the selection.
	 * @param headers the sequence of headers that might have some elements selected by this FieldSelector
	 * @return the positions of all elements which were not selected.
	 */
	@Override
	public int[] getFieldIndexes(NormalizedString[] headers) {
		if(headers == null){
			return null;
		}
		NormalizedString[] normalizedHeaders = headers;

		// removes duplicates if any
		Set<NormalizedString> chosenFields = NormalizedString.toHashSet(this.get());

		Object[] unknownFields = ArgumentUtils.findMissingElements(normalizedHeaders, chosenFields);
		int[] out = new int[normalizedHeaders.length - (chosenFields.size() - unknownFields.length)];

		int j = 0;
		for (int i = 0; i < normalizedHeaders.length; i++) {
			if (!chosenFields.contains(normalizedHeaders[i])) {
				out[j++] = i;
			}
		}

		return out;
	}

	@Override
	public String describe() {
		return "undesired " + super.describe();
	}

	@Override
	public int[] getFieldIndexes(String[] headers) {
		return getFieldIndexes(NormalizedString.toIdentifierGroupArray(headers));
	}
}
