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

import com.univocity.parsers.common.*;

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
	private final List<FieldAlignment> fieldAlignment = new ArrayList<FieldAlignment>();
	private boolean noNames = true;

	/**
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}.
	 * @param fields a {@link LinkedHashMap} containing the sequence of fields to be associated with each column in the input/output, with their respective length.
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
		return addField(null, length, FieldAlignment.LEFT);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)...
	 * @param length the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 * @return the FixedWidthFieldLengths instance itself for chaining.
	 */
	public FixedWidthFieldLengths addField(int length, FieldAlignment alignment) {
		return addField(null, length, alignment);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 * @param name the name of the next field. It is not validated.
	 * @param length the length of the next field. It must be greater than 0.
	 * @return the FixedWidthFieldLengths instance itself for chaining.
	 */
	public FixedWidthFieldLengths addField(String name, int length) {
		return addField(name, length, FieldAlignment.LEFT);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 * @param name the name of the next field. It is not validated.
	 * @param length the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 * @return the FixedWidthFieldLengths instance itself for chaining.
	 */
	public FixedWidthFieldLengths addField(String name, int length, FieldAlignment alignment) {
		validateLength(name, length);
		fieldLengths.add(length);
		fieldNames.add(name);
		if (name != null) {
			noNames = false;
		}
		fieldAlignment.add(alignment);
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
	 * Modifies the length of a given field
	 * @param name the name of the field whose length must be altered
	 * @param newLength the new length of the given field
	 */
	public void setFieldLength(String name, int newLength) {
		if (name == null) {
			throw new IllegalArgumentException("Field name cannot be null");
		}
		int index = fieldNames.indexOf(name);
		if (index == -1) {
			throw new IllegalArgumentException("Cannot find field with name '" + name + '\'');
		}
		validateLength(name, newLength);
		fieldLengths.set(index, newLength);
	}

	/**
	 * Modifies the length of a given field
	 * @param position the position of the field whose length must be altered
	 * @param newLength the new length of the given field
	 */
	public void setFieldLength(int position, int newLength) {
		validateIndex(position);
		validateLength("at index " + position, newLength);
		fieldLengths.set(position, newLength);
	}

	/**
	 * Applies alignment to a given list of fields
	 * @param alignment the alignment to apply
	 * @param positions the positions of the fields that should be aligned
	 */
	public void setAlignment(FieldAlignment alignment, int... positions) {
		for (int position : positions) {
			setAlignment(position, alignment);
		}
	}

	/**
	 * Applies alignment to a given list of fields
	 * @param alignment the alignment to apply
	 * @param names the names of the fields that should be aligned
	 */
	public void setAlignment(FieldAlignment alignment, String... names) {
		for (String name : names) {
			int position = indexOf(name);
			setAlignment(position, alignment);
		}
	}

	private void validateIndex(int position) {
		if (position < 0 && position >= fieldLengths.size()) {
			throw new IllegalArgumentException("No field defined at index " + position);
		}
	}

	/**
	 * Returns the index of a field name. An {@code IllegalArgumentException} will be thrown if no names have been defined.
	 * @param fieldName the name of the field to be searched
	 * @return the index of the field, or -1 if it does not exist.
	 */
	public int indexOf(String fieldName) {
		if (noNames) {
			throw new IllegalArgumentException("No field names defined");
		}
		if (fieldName == null || fieldName.trim().isEmpty()) {
			throw new IllegalArgumentException("Field name cannot be null/empty");
		}
		fieldName = ArgumentUtils.normalize(fieldName);
		int i = 0;
		for (String name : this.fieldNames) {
			name = ArgumentUtils.normalize(name);
			if (name.equals(fieldName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private void setAlignment(int position, FieldAlignment alignment) {
		if (alignment == null) {
			throw new IllegalArgumentException("Alignment cannot be null");
		}
		validateIndex(position);
		this.fieldAlignment.set(position, alignment);
	}

	/**
	 * Returns the alignment of a given field.
	 * @param position the index of the field whose alignment will be returned
	 * @return the alignment of the field
	 */
	public FieldAlignment getAlignment(int position) {
		validateIndex(position);
		return fieldAlignment.get(position);
	}

	/**
	 * Returns the alignment of a given field.  An {@code IllegalArgumentException} will be thrown if no names have been defined.
	 * @param fieldName the name of the field whose alignment will be returned
	 * @return the alignment of the given field
	 */
	public FieldAlignment getAlignment(String fieldName) {
		int index = indexOf(fieldName);
		if (index == -1) {
			throw new IllegalArgumentException("Field '" + fieldName + "' does not exist. Available field names are: " + this.fieldNames);
		}
		return getAlignment(index);
	}

	/**
	 * Returns a copy of the sequence of alignment settings to apply over each field in the fixed-width record.
	 * @return the sequence of alignment settings to apply over each field in the fixed-width record.
	 */
	public FieldAlignment[] getFieldAlignments() {
		return fieldAlignment.toArray(new FieldAlignment[fieldAlignment.size()]);
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		int i = 0;
		for (Integer length : fieldLengths) {
			out.append("\n\t\t").append(i + 1).append('\t');
			if (i < fieldNames.size()) {
				out.append(fieldNames.get(i));
			}
			out.append("length: ").append(length);
			out.append(", align: ").append(this.fieldAlignment.get(i));
			i++;
		}

		return out.toString();
	}
}
