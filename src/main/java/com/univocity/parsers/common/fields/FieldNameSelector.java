/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
		return getFieldIndexes(new String[]{header})[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getFieldIndexes(String[] headers) {
		headers = ArgumentUtils.normalize(headers);
		List<String> var = this.get();
		ArgumentUtils.normalize(var);

		String[] chosenFields = var.toArray(new String[var.size()]);
		Object[] unknownFields = ArgumentUtils.findMissingElements(headers, chosenFields);

		//if we get a subset of the expected columns, we can parse normally, considering missing column values as null.
		if (unknownFields.length > 0 && !var.containsAll(Arrays.asList(headers))) {
			//else we make it blow up.
			throw new IllegalStateException("Unknown field names: " + Arrays.toString(unknownFields) + ". Available fields are: " + Arrays.toString(headers));
		}

		int[] out = new int[chosenFields.length];
		int i = 0;
		Set<Integer> indexesTaken = new HashSet<Integer>();
		for (String chosenField : chosenFields) {
			int index = ArgumentUtils.indexOf(headers, chosenField);
			if(index != -1) {
				indexesTaken.add(index);
			}
			out[i++] = index;
		}

		int generatedIndex = 0;
		for(i = 0; i < out.length; i++){
			if(out[i] == -1){
				while(indexesTaken.contains(generatedIndex)){
					generatedIndex++;
				}
				indexesTaken.add(generatedIndex);
				out[i] = generatedIndex;
			}
		}

		return out;
	}

}
