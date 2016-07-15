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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;

/**
 * A {@link FieldSelector} that allows selecting fields from a given entity.
 *
 * @param <T> the type of the reference information used to uniquely identify a field (e.g. references to field indexes would use Integer, while references to field names would use String).
 */
class EntityFieldSelector<T> extends EntityFieldSet<T> implements FieldSelector {

	/**
	 * Creates a new field selector for an entity, which will determined only after a call to the {@link #of(String)} method.
	 *
	 * @param fieldSet a {@link FieldSet} that manages the actual selection of fields of an entity.
	 */
	EntityFieldSelector(FieldSet<T> fieldSet) {
		super(fieldSet);
	}

	@Override
	protected final void validate() {
		if (entity == null) {
			throw new IllegalStateException("No entity associated with " + super.describe() +
					". Please use method 'of()' after selecting your columns names/indexes" +
					" (e.g. 'settings.selectFields(...).of(<entity_name>)')");
		}
	}

	@Override
	public final int[] getFieldIndexes(String[] headers) {
		return getWrappedFieldSelector().getFieldIndexes(headers);
	}
}
