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
 * A set of selected fields.
 *
 * <p> Used by {@link CommonSettings} to select fields for reading/writing
 * <p> Also used by {@code com.univocity.parsers.common.processor.ConversionProcessor} to select fields that have to be converted.
 *
 * @see FieldNameSelector
 * @see FieldIndexSelector
 *
 * @see CommonSettings
 *
 * @param <T> the type of the reference information used to uniquely identify a field (e.g. references to field indexes would use Integer, while references to field names would use String).
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class FieldSet<T> {

	private final List<T> fields = new ArrayList<T>();

	private final List<FieldSet<T>> wrappedFieldSets;

	/**
	 * Creates am empty field set. For internal use only.
	 */
	public FieldSet(){
		this.wrappedFieldSets = Collections.emptyList();
	}

	/**
	 * Creates a field set that wraps a collection of other field sets. For internal use only.
	 * @param wrappedFieldSets the field sets to be wrapped.
	 */
	public FieldSet(List<FieldSet<T>> wrappedFieldSets){
		this.wrappedFieldSets = wrappedFieldSets;
		if(this.wrappedFieldSets.contains(this)){
			this.wrappedFieldSets.remove(this);
		}
	}

	/**
	 * Returns a copy of the fields in this set
	 * @return a copy of the fields in this set
	 */
	public List<T> get() {
		return new ArrayList<T>(fields);
	}

	/**
	 * Validates and sets multiple field references. Any existing reference will be discarded.
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> set(T... fields) {
		this.fields.clear();
		add(fields);
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.set(fields);
		}
		return this;
	}

	/**
	 * Validates and adds multiple field references
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> add(T... fields) {
		for (T field : fields) {
			addElement(field);
		}
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.add(fields);
		}
		return this;
	}

	/**
	 * Validates and adds a reference to a field.
	 * @param field information that uniquely identifies a field
	 */
	private void addElement(T field) {
		fields.add(field);
	}

	/**
	 * Validates and sets multiple field references. Any existing reference will be discarded.
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> set(Collection<T> fields) {
		this.fields.clear();
		add(fields);
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.set(fields);
		}
		return this;
	}

	/**
	 * Validates and adds multiple field references
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> add(Collection<T> fields) {
		for (T field : fields) {
			addElement(field);
		}
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.add(fields);
		}
		return this;
	}

	/**
	 * Removes multiple field references in the selection
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> remove(T... fields) {
		for (T field : fields) {
			this.fields.remove(field);
		}
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.remove(fields);
		}
		return this;
	}

	/**
	 * Removes multiple field references in the selection
	 * @param fields information that uniquely identifies each field
	 * @return the set of currently selected fields
	 */
	public FieldSet<T> remove(Collection<T> fields) {
		this.fields.removeAll(fields);
		for(FieldSet<T> wrapped : wrappedFieldSets){
			wrapped.remove(fields);
		}
		return this;
	}

	/**
	 * Returns a string that represents the current field selection
	 * @return a string that represents the current field selection
	 */
	public String describe() {
		return "field selection: " + fields.toString();
	}

	@Override
	public String toString() {
		return fields.toString();
	}
}
