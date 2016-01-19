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

/**
 * A FieldSelector capable of selecting fields by their position in a record.
 *
 * @see FieldSelector
 * @see FieldSet
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FieldIndexSelector extends FieldSet<Integer> implements FieldSelector {

	@Override
	public int[] getFieldIndexes(String[] columns) {
		List<Integer> chosenIndexes = this.get();
		int[] out = new int[chosenIndexes.size()];

		int i = 0;
		for (Integer index : chosenIndexes) {
			out[i++] = index;
		}

		return out;
	}
}
