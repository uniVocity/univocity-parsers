/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.*;

/**
 * This class provides the name, length, alignment and padding of each field in a fixed-width record.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class FixedWidthFields implements Cloneable {

	private List<Integer> fieldLengths = new ArrayList<Integer>();
	private List<String> fieldNames = new ArrayList<String>();
	private List<FieldAlignment> fieldAlignment = new ArrayList<FieldAlignment>();
	private List<Character> fieldPadding = new ArrayList<Character>();
	private boolean noNames = true;

	/**
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}.
	 *
	 * @param fields a {@link LinkedHashMap} containing the sequence of fields to be associated with each column in the input/output, with their respective length.
	 */
	public FixedWidthFields(LinkedHashMap<String, Integer> fields) {
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
	 * Defines a sequence of field names used to refer to columns in the input/output text of an entity, along with their lengths.
	 * The field names defined will be used as headers, having the same effect of a call to {@link FixedWidthParserSettings#setHeaders(String...)}.
	 *
	 * @param headers the sequence of fields to be associated with each column in the input/output
	 * @param lengths the sequence of lengths to be associated with each given header. The size of this array must match the number of given headers.
	 */
	public FixedWidthFields(String[] headers, int[] lengths) {
		if (headers == null || headers.length == 0) {
			throw new IllegalArgumentException("Headers cannot be null/empty");
		}
		if (lengths == null || lengths.length == 0) {
			throw new IllegalArgumentException("Field lengths cannot be null/empty");
		}
		if (headers.length != lengths.length) {
			throw new IllegalArgumentException("Sequence of headers and their respective lengths must match. Got " + headers.length + " headers but " + lengths.length + " lengths");
		}

		for (int i = 0; i < headers.length; i++) {
			addField(headers[i], lengths[i]);
		}
	}

	/**
	 * Creates a new instance initialized with the lengths of all fields in a fixed-width record.
	 *
	 * @param fieldLengths The number lengths of all fields in a fixed-width record. All lengths must be greater than 0.
	 */
	public FixedWidthFields(int... fieldLengths) {
		for (int i = 0; i < fieldLengths.length; i++) {
			addField(fieldLengths[i]);
		}
	}

	/**
	 * Creates a new instance initialized from {@link FixedWidth} annotations in the fields of a given class. Note that
	 * all fields should additionally have the {@link Parsed} annotation to configure header names and/or their positions.
	 *
	 * @param beanClass the class whose {@link FixedWidth} annotations will be processed to configure this field list.
	 */
	public FixedWidthFields(Class beanClass) {
		if (beanClass == null) {
			throw new IllegalArgumentException("Class must not be null.");
		}

		List<Field> fieldSequence = AnnotationHelper.getFieldSequence(beanClass, true);
		if (fieldSequence.isEmpty()) {
			throw new IllegalArgumentException("Can't derive fixed-width fields from class '" + beanClass.getName() + "'. No @Parsed annotations found.");
		}

		Set<String> fieldNamesWithoutConfig = new LinkedHashSet<String>();

		for (Field field : fieldSequence) {
			if (field == null) {
				continue;
			}
			String fieldName = AnnotationHelper.getHeaderName(field);

			FixedWidth fw = AnnotationHelper.findAnnotation(field, FixedWidth.class);
			if (fw == null) {
				fieldNamesWithoutConfig.add(field.getName());
				continue;
			}

			if (fieldName == null) {
				addField(fw.value(), fw.alignment(), fw.padding());
			} else {
				addField(fieldName, fw.value(), fw.alignment(), fw.padding());
			}
		}

		if (fieldNamesWithoutConfig.size() > 0) {
			throw new IllegalArgumentException("Can't derive fixed-width fields from class '" + beanClass.getName() + "'. " +
					"The following fields don't have a @FixedWidth annotation: " + fieldNamesWithoutConfig);
		}
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)...
	 *
	 * @param length the length of the next field. It must be greater than 0.
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(int length) {
		return addField(null, length, FieldAlignment.LEFT, '\0');
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)...
	 *
	 * @param length    the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(int length, FieldAlignment alignment) {
		return addField(null, length, alignment, '\0');
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 *
	 * @param name   the name of the next field. It is not validated.
	 * @param length the length of the next field. It must be greater than 0.
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(String name, int length) {
		return addField(name, length, FieldAlignment.LEFT, '\0');
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 *
	 * @param name      the name of the next field. It is not validated.
	 * @param length    the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(String name, int length, FieldAlignment alignment) {
		return addField(name, length, alignment, '\0');
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)...
	 *
	 * @param length  the length of the next field. It must be greater than 0.
	 * @param padding the representation of unused space in this field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(int length, char padding) {
		return addField(null, length, FieldAlignment.LEFT, padding);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField(5).addField(6)...
	 *
	 * @param length    the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 * @param padding   the representation of unused space in this field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(int length, FieldAlignment alignment, char padding) {
		return addField(null, length, alignment, padding);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 *
	 * @param name    the name of the next field. It is not validated.
	 * @param length  the length of the next field. It must be greater than 0.
	 * @param padding the representation of unused space in this field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(String name, int length, char padding) {
		return addField(name, length, FieldAlignment.LEFT, padding);
	}

	/**
	 * Adds the length of the next field in a fixed-width record. This method can be chained like this: addField("field_1", 5).addField("field_2", 6)...
	 *
	 * @param name      the name of the next field. It is not validated.
	 * @param length    the length of the next field. It must be greater than 0.
	 * @param alignment the alignment of the field
	 * @param padding   the representation of unused space in this field
	 *
	 * @return the FixedWidthFields instance itself for chaining.
	 */
	public FixedWidthFields addField(String name, int length, FieldAlignment alignment, char padding) {
		validateLength(name, length);
		fieldLengths.add(length);
		fieldNames.add(name);
		fieldPadding.add(padding);
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
	 *
	 * @return the number of fields in a fixed-width record
	 */
	public int getFieldsPerRecord() {
		return fieldLengths.size();
	}

	/**
	 * Returns the name of each field in a fixed-width record, if any
	 *
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
	 *
	 * @return a copy of the sequence of field lengths of a fixed-width record
	 */
	public int[] getFieldLengths() {
		return ArgumentUtils.toIntArray(fieldLengths);
	}

	/**
	 * Modifies the length of a given field
	 *
	 * @param name      the name of the field whose length must be altered
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
	 *
	 * @param position  the position of the field whose length must be altered
	 * @param newLength the new length of the given field
	 */
	public void setFieldLength(int position, int newLength) {
		validateIndex(position);
		validateLength("at index " + position, newLength);
		fieldLengths.set(position, newLength);
	}

	/**
	 * Applies alignment to a given list of fields
	 *
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
	 *
	 * @param alignment the alignment to apply
	 * @param names     the names of the fields that should be aligned
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
	 *
	 * @param fieldName the name of the field to be searched
	 *
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
	 *
	 * @param position the index of the field whose alignment will be returned
	 *
	 * @return the alignment of the field
	 */
	public FieldAlignment getAlignment(int position) {
		validateIndex(position);
		return fieldAlignment.get(position);
	}

	/**
	 * Returns the alignment of a given field.  An {@code IllegalArgumentException} will be thrown if no names have been defined.
	 *
	 * @param fieldName the name of the field whose alignment will be returned
	 *
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
	 *
	 * @return the sequence of alignment settings to apply over each field in the fixed-width record.
	 */
	public FieldAlignment[] getFieldAlignments() {
		return fieldAlignment.toArray(new FieldAlignment[fieldAlignment.size()]);
	}

	/**
	 * Returns a copy of the sequence of padding characters to apply over each field in the fixed-width record.
	 *
	 * The null character ({@code '\0'}) is used to inform no padding has been explicitly set for a field, and that the
	 * default padding character defined in {@link FixedWidthFormat#getPadding()} should be used.
	 *
	 * @return the sequence of padding characters to apply over each field in the fixed-width record.
	 */
	public char[] getFieldPaddings() {
		return ArgumentUtils.toCharArray(fieldPadding);
	}

	char[] getFieldPaddings(FixedWidthFormat format) {
		char[] out = getFieldPaddings();
		for (int i = 0; i < out.length; i++) {
			if (out[i] == '\0') {
				out[i] = format.getPadding();
			}
		}
		return out;
	}

	/**
	 * Applies a custom padding character to a given list of fields
	 *
	 * @param padding   the padding to apply
	 * @param positions the positions of the fields that should use the given padding character
	 */
	public void setPadding(char padding, int... positions) {
		for (int position : positions) {
			setPadding(position, padding);
		}
	}

	/**
	 * Applies a custom padding character to a given list of fields
	 *
	 * @param padding the padding to apply
	 * @param names   the names of the fields that should use the given padding character
	 */
	public void setPadding(char padding, String... names) {
		for (String name : names) {
			int position = indexOf(name);
			setPadding(position, padding);
		}
	}

	private void setPadding(int position, char padding) {
		if (padding == '\0') {
			throw new IllegalArgumentException("Cannot use the null character as padding");
		}
		validateIndex(position);
		fieldPadding.set(position, padding);
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
			out.append(", align: ").append(fieldAlignment.get(i));
			out.append(", padding: ").append(fieldPadding.get(i));
			i++;
		}

		return out.toString();
	}

	static void setHeadersIfPossible(FixedWidthFields fieldLengths, CommonSettings settings) {
		if (fieldLengths != null && settings.getHeaders() == null) {
			String[] headers = fieldLengths.getFieldNames();
			if (headers != null) {
				int[] lengths = fieldLengths.getFieldLengths();
				if (lengths.length == headers.length) {
					settings.setHeaders(headers);
				}
			}
		}
	}

	@Override
	protected FixedWidthFields clone() {
		try {
			FixedWidthFields out = (FixedWidthFields) super.clone();
			out.fieldLengths = new ArrayList<Integer>(fieldLengths);
			out.fieldNames = new ArrayList<String>(fieldNames);
			out.fieldAlignment = new ArrayList<FieldAlignment>(fieldAlignment);
			out.fieldPadding = new ArrayList<Character>(fieldPadding);
			return out;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
}
