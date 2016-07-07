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

public abstract class EntityFieldSet<T> extends FieldSet<T> {

	private final FieldSet<T> wrappedFieldSet;
	protected String entity;

	public EntityFieldSet(FieldSet<T> fieldSet) {
		this.wrappedFieldSet = fieldSet;
	}

	public final void of(String entity) {
		ArgumentUtils.noNulls("Entity associated with field selection " + super.describe(), entity);
		this.entity = entity;
	}

	@Override
	public final List<T> get() {
		validate();
		return wrappedFieldSet.get();
	}

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

	protected final FieldSelector getWrappedFieldSelector() {
		return (FieldSelector) wrappedFieldSet;
	}

	public final String getEntityName() {
		return entity;
	}

	@Override
	public final String toString() {
		return describe();
	}
}
