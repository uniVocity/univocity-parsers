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

class EntityFieldSelector<T> extends EntityFieldSet<T> implements FieldSelector {
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
