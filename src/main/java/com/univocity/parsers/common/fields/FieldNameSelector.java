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

import java.util.*;

import com.univocity.parsers.common.*;

/**
 * A FieldSelector capable of selecting fields by their name.
 *
 * @see FieldSelector
 * @see FieldSet
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FieldNameSelector extends FieldSet<String> implements FieldSelector {

	/**
	 * Returns the position of a given header
	 * @param header the header whose position will be returned
	 * @return the position of the given header.
	 */
	public int getFieldIndex(String header) {
		return getFieldIndexes(new String[] { header })[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getFieldIndexes(String[] headers) {
		headers = ArgumentUtils.normalize(headers);
		List<String> var = this.get();
		String[] chosenFields = ArgumentUtils.normalize(var.toArray(new String[var.size()]));

		Object[] unknownFields = ArgumentUtils.findMissingElements(headers, chosenFields);

		if (unknownFields.length > 0) {
			throw new IllegalStateException("Unknown field names: " + Arrays.toString(unknownFields) + ". Available fields are: " + Arrays.toString(headers));
		}

		int[] out = new int[chosenFields.length];
		int i = 0;
		for (String chosenField : chosenFields) {
			out[i++] = ArgumentUtils.indexOf(headers, chosenField);
		}

		return out;
	}

}
