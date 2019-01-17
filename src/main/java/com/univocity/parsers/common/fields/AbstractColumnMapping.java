package com.univocity.parsers.common.fields;

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

abstract class AbstractColumnMapping<K> implements Cloneable {

	private final String prefix;
	private Map<K, Object> mapping;

	public AbstractColumnMapping(String prefix, AbstractColumnMapping parent) {
		if (parent != null) {
			mapping = parent.mapping;
			this.prefix = parent.prefix.isEmpty() ? prefix : parent.prefix + '.' + prefix;
		} else {
			mapping = new LinkedHashMap<K, Object>();
			this.prefix = prefix;
		}
	}

	public void mapToColumnName(K key, String columnName) {
		mapping.put(key, columnName);
	}

	public void mapToColumn(K key, Enum<?> column) {
		mapping.put(key, column);
	}

	public void mapToColumnIndex(K key, int columnIndex) {
		mapping.put(key, columnIndex);
	}

	public void mapToColumnNames(Map<K, String> mappings) {
		mapping.putAll(mappings);
	}

	public void mapToColumns(Map<K, Enum<?>> mappings) {
		mapping.putAll(mappings);
	}

	public void mapToColumnIndexes(Map<K, Integer> mappings) {
		mapping.putAll(mappings);
	}

	public Map<K, Object> getMappings() {
		return Collections.unmodifiableMap(mapping);
	}

	public boolean isMapped(K key) {
		return getMappedColumn(key) != null;
	}

	abstract K prefixKey(String prefix, K key);

	private Object getMappedColumn(K key) {
		if (key == null) {
			return null;
		}
		key = prefixKey(prefix, key);
		Object out = mapping.get(key);

		return out;
	}

	public boolean updateFieldMapping(FieldMapping fieldMapping, K key) {
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

	public String getPrefix() {
		return prefix;
	}

	void extractPrefixes(Set<String> out) {
		for (K key : mapping.keySet()) {
			String keyPrefix = getKeyPrefix(prefix, key);
			if (keyPrefix != null) {
				out.add(keyPrefix);
			}
		}
	}

	abstract String getKeyPrefix(String prefix, K key);


	public AbstractColumnMapping<K> clone() {
		try {
			AbstractColumnMapping<K> out = (AbstractColumnMapping<K>) super.clone();
			out.mapping = new LinkedHashMap<K, Object>(mapping);
			return out;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	abstract K findKey(String nameWithPrefix);

	public void remove(String nameWithPrefix) {
		K key = findKey(nameWithPrefix);
		if (key != null) {
			mapping.remove(key);
		}
	}
}
