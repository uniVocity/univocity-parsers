/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

/**
 * Basic support operations for mapping attributes/methods to columns in a {@link ColumnMapper}
 * @param <K> the type of key (attribute/method names or specific method definition with parameter and return types)
 */
abstract class AbstractColumnMapping<K> implements Cloneable {

	final String prefix;
	Map<K, Object> mapping;

	/**
	 * Creates a mapping with a prefix.
	 *
	 * @param prefix a dot separated sequence of names that represents the nesting of complex attributes inside a class (e.g customer.contact.phone).
	 * @param parent the parent mapping of columns, relevant only when nested objects' attributes or methods are being mapped.
	 */
	AbstractColumnMapping(String prefix, AbstractColumnMapping parent) {
		if (parent != null) {
			mapping = parent.mapping;
			this.prefix = parent.prefix.isEmpty() ? prefix : parent.prefix + '.' + prefix;
		} else {
			mapping = new LinkedHashMap<K, Object>();
			this.prefix = prefix;
		}
	}

	/**
	 * Maps a attribute or method to a column name
	 * @param key attribute/method name or specific method definition with parameter and return type
	 * @param columnName name of column associated with the given key
	 */
	void mapToColumnName(K key, String columnName) {
		mapping.put(key, columnName);
	}

	/**
	 * Maps a attribute or method to a column name
	 * @param key attribute/method name or specific method definition with parameter and return type
	 * @param column enumeration representing the column associated with the given key
	 */
	void mapToColumn(K key, Enum<?> column) {
		mapping.put(key, column);
	}

	/**
	 * Maps a attribute or method to a column name
	 * @param key attribute/method name or specific method definition with parameter and return type
	 * @param columnIndex number representing the position of the column associated with the given key
	 */
	void mapToColumnIndex(K key, int columnIndex) {
		mapping.put(key, columnIndex);
	}

	/**
	 * Maps multiple attributes or methods to multiple column names
	 * @param mappings the mappings to be added
	 */
	void mapToColumnNames(Map<K, String> mappings) {
		mapping.putAll(mappings);
	}

	/**
	 * Maps multiple attributes or methods to multiple column names
	 * @param mappings the mappings to be added
	 */
	void mapToColumns(Map<K, Enum<?>> mappings) {
		mapping.putAll(mappings);
	}

	/**
	 * Maps multiple attributes or methods to multiple column names
	 * @param mappings the mappings to be added
	 */
	void mapToColumnIndexes(Map<K, Integer> mappings) {
		mapping.putAll(mappings);
	}

	/**
	 * Tests whether a given attribute or method is mapped to a column
	 * @param key the attribute or method name/descriptor
	 * @return {@code true} if the key is mapped.
	 */
	boolean isMapped(K key) {
		return getMappedColumn(key) != null;
	}

	/**
	 * Transforms the key so it can work with the given prefix.
	 *
	 * @param prefix the current object nesting level, denoted by a dot-separated string of nested attribute names.
	 * @param key the key to transform.
	 * @return the transformed key or {@code null} if the key can't be used with the given prefix
	 */
	abstract K prefixKey(String prefix, K key);

	private Object getMappedColumn(K key) {
		if (key == null) {
			return null;
		}
		key = prefixKey(prefix, key);
		Object out = mapping.get(key);

		return out;
	}

	/**
	 * Updates the mapping of a attribute/method so a mapped class member can target
	 * a user provided column.
	 *
	 * @param fieldMapping a class member that has should be mapped to a column
	 * @param key the attribute name or method specification that matches with the given field.
	 *
	 * @return {@code true} if the mapping has been successfully updated.
	 */
	boolean updateFieldMapping(FieldMapping fieldMapping, K key) {
		Object mappedColumn = getMappedColumn(key);
		if (mappedColumn != null) {
			if (mappedColumn instanceof Enum) {
				mappedColumn = ((Enum) mappedColumn).name();
			}
			if (mappedColumn instanceof String) {
				fieldMapping.setFieldName((String) mappedColumn);
				fieldMapping.setIndex(-1);
				return true;
			} else if (mappedColumn instanceof Integer) {
				fieldMapping.setIndex((Integer) mappedColumn);
				return true;
			}
			throw new IllegalStateException("Unexpected mapping of '" + key + "' to " + mappedColumn);
		}
		return false;
	}

	/**
	 * Returns all prefixes used by the keys in this mapping. These represent names of
	 * nested objects that will be navigated through to access their attributes/methods.
	 * @param out the set of prefixes to populate.
	 */
	void extractPrefixes(Set<String> out) {
		for (K key : mapping.keySet()) {
			String keyPrefix = getKeyPrefix(prefix, key);
			if (keyPrefix != null) {
				out.add(keyPrefix);
			}
		}
	}

	/**
	 * Returns the prefix of a given key, i.e. the current nested object that is
	 * being targeted.
	 *
	 * @param prefix the current prefix - a dot separated string with nested attribute names
	 * @param key the attribute name or method definition. If its own prefix starts with the given prefix, the next element after the dot will be returned (if any)
	 * @return the name of the next nested object relative to the current prefix.
	 */
	abstract String getKeyPrefix(String prefix, K key);

	/**
	 * Creates a deep copy of this mapping that is independent from the original.
	 * @return the duplicate of this object.
	 */
	public AbstractColumnMapping<K> clone() {
		try {
			AbstractColumnMapping<K> out = (AbstractColumnMapping<K>) super.clone();
			out.mapping = new LinkedHashMap<K, Object>(mapping);
			return out;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Locates a given key based on an attribute or method name.
	 *
	 * @param nameWithPrefix name of the attribute or method, prefixed with nested object names that identify the path to the target class member.
	 * @return the key formed with the given attribute name or method definition
	 */
	abstract K findKey(String nameWithPrefix);

	/**
	 * Removes any mappings containing keys that have a given attribute or method name.
	 *
	 * @param nameWithPrefix name of the attribute or method, prefixed with nested object names that identify the path to the target class member.
	 */
	void remove(String nameWithPrefix) {
		K key;
		while ((key = findKey(nameWithPrefix)) != null) {
			if (mapping.remove(key) == null) {
				return;
			}
		}
	}
}
