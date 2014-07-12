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
package com.univocity.parsers.fixed;

import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * This class provides the lengths of each field in a fixed-width record.
 * 
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FixedWidthFieldLengths {

	private final List<Integer> fieldLengths = new ArrayList<Integer>();
	private final List<String> fieldNames = new ArrayList<String>();
	private boolean noNames = true;

	/**
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}. 
	 * @param fields a {@link LinkedHashMap} containing the sequence of fields to be associated to each column in the input/output, with their respective length.
	 */
	public FixedWidthFieldLengths(LinkedHashMap<String, Integer> fields) {
		if (fields == null || fields.isEmpty()) {
			throw new IllegalArgumentException("Map of fields and their lengths cannot be null/empty");
		}

		for (Entry<String, Integer> entry : fields.entrySet()) {
			String fieldName = entry.getKey();
			Integer fieldLength = entry.getValue();
			addField(fieldName, fieldLength);
		}
	}

	/**
	 * Creates a new instance initialized with the lengths of all fields in a fixed-width record.
	 * @param fieldLengths The number lengths of all fields in a fixed-width record. All lengths must be greater than 0.
	 */
	public FixedWidthFieldLengths(int... fieldLengths) {
		for (int i = 0; i < fieldLengths.length; i++) {
			addField(fieldLengths[i]);
		}
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)... 
	 * @param length the length of the next field. It must be greater than 0.
	 * @return the FixedWidthFieldLengths instance itself for chaining. 
	 */
	public FixedWidthFieldLengths addField(int length) {
		return addField(null, length);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 * @param name the name of the next field. It is not validated. 
	 * @param length the length of the next field. It must be greater than 0.
	 * @return the FixedWidthFieldLengths instance itself for chaining. 
	 */
	public FixedWidthFieldLengths addField(String name, int length) {
		validateLength(name, length);
		fieldLengths.add(length);
		fieldNames.add(name);
		if (name != null) {
			noNames = false;
		}
		return this;
	}

	private void validateLength(String name, int length) {
		if (length < 1) {
			if (name == null) {
				throw new IllegalArgumentException("Invalid field length: " + length + " for field at index " + fieldLengths.size());
			} else {
				throw new IllegalArgumentException("Invalid field length: " + length + " for field " + name);
			}
		}

	}

	/**
	 * Returns the number of fields in a fixed-width record
	 * @return the number of fields in a fixed-width record
	 */
	public int getFieldsPerRecord() {
		return fieldLengths.size();
	}

	/**
	 * Returns the name of each field in a fixed-width record, if any
	 * @return the name of each field in a fixed-width record, or null if no name has been defined.
	 */
	public String[] getFieldNames() {
		if (noNames) {
			return null;
		}
		return fieldNames.toArray(new String[fieldNames.size()]);
	}

	/**
	 * Returns a copy of the sequence of field lengths of a fixed-width record
	 * @return a copy of the sequence of field lengths of a fixed-width record
	 */
	public int[] getFieldLengths() {
		int[] lengths = new int[fieldLengths.size()];
		for (int i = 0; i < fieldLengths.size(); i++) {
			lengths[i] = fieldLengths.get(i);
		}
		return lengths;
	}

	/**
	 * Modifies the lengths of a given field
	 * @param name the name of the field whose length must be altered
	 * @param newLength the new length of the given field
	 */
	public void setFieldLength(String name, int newLength) {
		if (name == null) {
			throw new IllegalArgumentException("Field name cannot be null");
		}
		int index = fieldNames.indexOf(name);
		if (index == -1) {
			throw new IllegalArgumentException("Cannot find field with name '" + name + "'");
		}
		validateLength(name, newLength);
		fieldLengths.set(index, newLength);
	}

	/**
	 * Modifies the lengths of a given field
	 * @param position the position of the field whose length must be altered
	 * @param newLength the new length of the given field
	 */
	public void setFieldLength(int position, int newLength) {
		if (position < 0 && position >= fieldLengths.size()) {
			throw new IllegalArgumentException("No field defined at index " + position);
		}
		validateLength("at index " + position, newLength);
		fieldLengths.set(position, newLength);
	}
}
