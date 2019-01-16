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

/**
 * A FieldSelector that selects all indexes of a record.
 *
 * @see FieldSelector
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class AllIndexesSelector implements FieldSelector {

	@Override
	public int[] getFieldIndexes(String[] headers) {
		if(headers == null){
			return null;
		}
		int[] out = new int[headers.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = i;
		}
		return out;
	}

	@Override
	public String describe() {
		return "all fields";
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		}catch (CloneNotSupportedException e){
			throw new IllegalStateException(e);
		}
	}
}
