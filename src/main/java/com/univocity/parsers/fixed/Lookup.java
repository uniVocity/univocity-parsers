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
package com.univocity.parsers.fixed;

class Lookup {
	
	final char[] value;
	final int[] lengths;
	final FieldAlignment[] alignments;
	final String[] fieldNames;

	Lookup(String value, FixedWidthFieldLengths config) {
		this.value = value.toCharArray();
		this.lengths = config.getFieldLengths();
		this.alignments = config.getFieldAlignments();
		this.fieldNames = config.getFieldNames();
	}	

	boolean matches(char[] lookup) {
		if (value.length  > lookup.length) {
			return false;
		}
		for (int i = 0; i < value.length; i++) {
			if (value[i] != lookup[i]) {
				return false;
			}
		}
		return true;
	}
}
