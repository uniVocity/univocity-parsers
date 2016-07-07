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
import com.univocity.parsers.common.processor.core.*;

import java.util.*;
import java.util.Map.*;

public abstract class AbstractEntityParserSettings<F extends Format, C extends Context> extends CommonParserSettings<F> {

	protected final Map<String, Processor<C>> entityProcessors = new HashMap<String, Processor<C>>();
	protected final Set<String> entitiesToRead = new TreeSet<String>();
	protected final Set<String> entitiesToSkip = new TreeSet<String>();

	@SuppressWarnings("rawtypes")
	private final List<EntityFieldSet> entityFieldSets = new ArrayList<EntityFieldSet>();

	public AbstractEntityParserSettings() {
		setHeaderExtractionEnabled(false);
		setMaxCharsPerColumn(-1);
		setCommentCollectionEnabled(true);
	}

	private static String[] toArray(Collection<String> collection) {
		return collection == null ? ArgumentUtils.EMPTY_STRING_ARRAY : collection.toArray(new String[0]);
	}

	public final void setEntityProcessor(Processor<C> entityProcessor, Collection<String> entities) {
		setEntityProcessor(entityProcessor, toArray(entities));
	}

	public final void setEntityProcessor(Processor<C> entityProcessor, String... entities) {
		ArgumentUtils.noNulls("Names of entities to be processed", entities);
		ArgumentUtils.noNulls("Processor for entities " + Arrays.toString(entities), entityProcessor);
		for (String entity : entities) {
			addEntityProcessor(entityProcessor, entity);
		}
	}

	private void addEntityProcessor(Processor<C> entityProcessor, String entity) {
		ArgumentUtils.noNulls("Name of entity to be processed", entity);
		ArgumentUtils.noNulls("Processor for entity " + entity, entityProcessor);

		entity = ArgumentUtils.normalize(entity);
		this.entityProcessors.put(entity, entityProcessor);
	}

	public final void addEntitiesToRead(Collection<String> entitiesToRead) {
		addEntitiesToRead(toArray(entitiesToRead));
	}

	public final void addEntitiesToRead(String... entitiesToRead) {
		for (String entity : entitiesToRead) {
			ArgumentUtils.noNulls("Name of entity to be read", entity);
			this.entitiesToRead.add(entity);
		}
	}

	public final void addEntitiesToSkip(Collection<String> entitiesToSkip) {
		setEntitiesToSkip(toArray(entitiesToSkip));
	}

	public final void setEntitiesToSkip(Collection<String> entitiesToSkip) {
		setEntitiesToSkip(toArray(entitiesToSkip));
	}

	public final Set<String> getEntitiesToRead() {
		return Collections.unmodifiableSet(entitiesToRead);
	}

	public final void setEntitiesToRead(Collection<String> entitiesToRead) {
		setEntitiesToRead(toArray(entitiesToRead));
	}

	public final void setEntitiesToRead(String... entitiesToRead) {
		this.entitiesToRead.clear();
		addEntitiesToRead(entitiesToRead);
	}

	public final Set<String> getEntitiesToSkip() {
		return Collections.unmodifiableSet(entitiesToSkip);
	}

	public final void setEntitiesToSkip(String... entitiesToSkip) {
		this.entitiesToSkip.clear();
		addEntitiesToSkip(entitiesToSkip);
	}

	public final void addEntitiesToSkip(String... entitiesToSkip) {
		for (String entity : entitiesToSkip) {
			ArgumentUtils.noNulls("Name of entity to be skipped", entity);
			this.entitiesToSkip.add(entity);
		}
	}

	public Processor<?> getEntityProcessor(String entityName) {
		entityName = ArgumentUtils.normalize(entityName);
		Processor out = entityProcessors.get(entityName);
		if (out == null) {
			return NoopProcessor.instance;
		}
		return out;
	}


	/**
	 * Selects a sequence of fields for reading/writing by their names
	 *
	 * @param fieldNames The field names to read/write
	 *
	 * @return the (modifiable) set of selected fields
	 */
	@Override
	public final EntityFieldSet<String> selectFields(String... fieldNames) {
		setHeaders(fieldNames);
		return newEntityFieldSet(super.selectFields(fieldNames));
	}

	private <T> EntityFieldSet<T> newEntityFieldSet(FieldSet<T> fieldSet) {
		if (!entityFieldSets.isEmpty()) {
			EntityFieldSet previous = entityFieldSets.get(entityFieldSets.size() - 1);
			((EntityFieldSelector)previous).validate();
		}
		EntityFieldSet<T> out = new EntityFieldSelector<T>(fieldSet);
		entityFieldSets.add(out);
		return out;
	}

	/**
	 * Selects fields which will not be read/written by their names
	 *
	 * @param fieldNames The field names to exclude from the parsing/writing process
	 *
	 * @return the (modifiable) set of ignored fields
	 */
	@Override
	public final EntityFieldSet<String> excludeFields(String... fieldNames) {
		return newEntityFieldSet(super.excludeFields(fieldNames));
	}

	/**
	 * Selects a sequence of fields for reading/writing by their indexes
	 *
	 * @param fieldIndexes The field indexes to read/write
	 *
	 * @return the (modifiable) set of selected fields
	 */
	@Override
	public final EntityFieldSet<Integer> selectIndexes(Integer... fieldIndexes) {
		return newEntityFieldSet(super.selectIndexes(fieldIndexes));
	}

	/**
	 * Selects fields which will not be read/written by their indexes
	 *
	 * @param fieldIndexes The field indexes to exclude from the parsing/writing process
	 *
	 * @return the (modifiable) set of ignored fields
	 */
	@Override
	public final EntityFieldSet<Integer> excludeIndexes(Integer... fieldIndexes) {
		return newEntityFieldSet(super.excludeIndexes(fieldIndexes));
	}

	protected Map<String, EntityFieldSet> getEntityFieldSelection() {
		if (entityFieldSets.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, EntityFieldSet> out = new HashMap<String, EntityFieldSet>();

		//if a new selection has been made over an entity, it will be used. Previous selections are discarded.
		List<EntityFieldSet> reversed = new ArrayList<EntityFieldSet>(entityFieldSets);
		Collections.reverse(reversed);

		for (EntityFieldSet selection : reversed) {
			((EntityFieldSelector)selection).validate();
			String entityName = selection.getEntityName();
			if (out.containsKey(entityName)) {
				continue;
			}
			out.put(ArgumentUtils.normalize(entityName), selection);
		}

		return out;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		for (Entry<String, Processor<C>> e : entityProcessors.entrySet()) {
			out.put("\tProcessor of " + e.getKey(), e.getValue().getClass().getName() + "(" + e.getValue() + ")");
		}
		out.put("Entities to read", entitiesToRead.isEmpty() ? "all" : entitiesToRead);
		out.put("Entities to skip", entitiesToSkip.isEmpty() ? "none" : entitiesToSkip);
		for (Entry<String, EntityFieldSet> e : getEntityFieldSelection().entrySet()) {
			if (printConfiguration(e.getKey())) {
				out.put("\tField selection of " + e.getKey(), e.getValue().toString());
			}
		}
	}

	protected boolean printConfiguration(String entityName) {
		return true;
	}

	public final boolean shouldSkip(String entityName){
		return entitiesToSkip.contains(entityName) || (!entitiesToRead.isEmpty() && !entitiesToRead.contains(entityName));
	}

	public final boolean shouldRead(String entityName){
		return !entitiesToRead.isEmpty() && entitiesToRead.contains(entityName);
	}
}