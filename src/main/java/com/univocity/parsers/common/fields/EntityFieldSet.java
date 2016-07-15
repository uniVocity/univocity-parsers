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
package com.univocity.parsers.common.fields;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * A set of fields selected from an entity. Once a selection of fields is made, the {@link #of(String)} method must
 * be called to determine the name of the entity associated with the selection, otherwise an {@code IllegalStateException} will be thrown
 *
 * <p> Used by {@link EntityFieldSelector} to select fields for reading/writing
 * <p> Also used by {@code com.univocity.parsers.common.processor.ConversionProcessor} to select fields that have to be converted.
 *
 * @see EntityFieldSelector
 * @see FieldNameSelector
 * @see FieldIndexSelector
 *
 *
 * @param <T> the type of the reference information used to uniquely identify a field (e.g. references to field indexes would use Integer, while references to field names would use String).
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class EntityFieldSet<T> extends FieldSet<T> {

	private final FieldSet<T> wrappedFieldSet;
	protected String entity;

	/**
	 * Creates a new field set for an entity, which will determined only after a call to the {@link #of(String)} method.
	 *
	 * @param fieldSet a {@link FieldSet} that manages the actual selection of fields of an entity.
	 */
	public EntityFieldSet(FieldSet<T> fieldSet) {
		this.wrappedFieldSet = fieldSet;
	}

	/**
	 * Defines the name of the entity associated with this field seleciton.
	 * @param entity the entity name whose fields were selected.
	 */
	public final void of(String entity) {
		ArgumentUtils.noNulls("Entity associated with field selection " + super.describe(), entity);
		this.entity = entity;
	}

	@Override
	public final List<T> get() {
		validate();
		return wrappedFieldSet.get();
	}

	/**
	 * Validates the current field selection.
	 */
	protected abstract void validate();

	@Override
	public final FieldSet<T> set(T... fields) {
		return wrappedFieldSet.set(fields);
	}

	@Override
	public final FieldSet<T> add(T... fields) {
		return wrappedFieldSet.add(fields);
	}

	@Override
	public final FieldSet<T> set(Collection<T> fields) {
		return this.wrappedFieldSet.set(fields);
	}

	@Override
	public final FieldSet<T> add(Collection<T> fields) {
		return wrappedFieldSet.add(fields);
	}

	@Override
	public final FieldSet<T> remove(T... fields) {
		return wrappedFieldSet.remove(fields);
	}

	@Override
	public final FieldSet<T> remove(Collection<T> fields) {
		return wrappedFieldSet.remove(fields);
	}

	@Override
	public final String describe() {
		if (this.entity != null) {
			return "Entity " + entity + ", " + wrappedFieldSet.describe();
		} else {
			return wrappedFieldSet.describe();
		}
	}

	/**
	 * The wrapped {@link FieldSet} provided in the constructor of this class, which contains the actual
	 * field selection of the current entity.
	 * @return the {@link FieldSet} provided in the constructor of this class
	 */
	protected final FieldSelector getWrappedFieldSelector() {
		return (FieldSelector) wrappedFieldSet;
	}

	/**
	 * Returns the name of the entity whose fields were selected by this {@code EntityFieldSet}
	 * @return the entity name
	 */
	public final String getEntityName() {
		return entity;
	}

	@Override
	public final String toString() {
		return describe();
	}
}
