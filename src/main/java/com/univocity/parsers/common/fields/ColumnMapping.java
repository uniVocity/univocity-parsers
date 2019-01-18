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

import static com.univocity.parsers.annotations.helpers.MethodDescriptor.*;

/**
 * Implementation the {@link ColumnMapper} interface which allows
 * users to manually define mappings from attributes/methods of a given class
 * to columns to be parsed or written.
 *
 * @see ColumnMapper
 */
public final class ColumnMapping implements ColumnMapper {

	private class NameMapping extends AbstractColumnMapping<String> {
		public NameMapping(String prefix, NameMapping parent) {
			super(prefix, parent);
		}

		@Override
		String prefixKey(String prefix, String key) {
			if (prefix.isEmpty()) {
				return key;
			} else {
				return prefix + '.' + key;
			}
		}

		@Override
		String getKeyPrefix(String prefix, String key) {
			return getCurrentAttributePrefix(prefix, key);
		}

		@Override
		String findKey(String nameWithPrefix) {
			return nameWithPrefix;
		}
	}

	private class MethodMapping extends AbstractColumnMapping<MethodDescriptor> {
		public MethodMapping(String prefix, MethodMapping parent) {
			super(prefix, parent);
		}

		@Override
		MethodDescriptor prefixKey(String prefix, MethodDescriptor key) {
			if (key.getPrefix().equals(prefix)) {
				return key;
			}
			return null;
		}

		@Override
		String getKeyPrefix(String prefix, MethodDescriptor key) {
			return getCurrentAttributePrefix(prefix, key.getPrefixedName());
		}

		@Override
		MethodDescriptor findKey(String nameWithPrefix) {
			for (MethodDescriptor k : this.mapping.keySet()) {
				if (k.getPrefixedName().equals(nameWithPrefix)) {
					return k;
				}
			}
			return null;
		}
	}

	private static String getCurrentAttributePrefix(String prefix, String name) {
		if (!name.startsWith(prefix)) {
			return null;
		}

		int off = prefix.isEmpty() ? 0 : 1;

		int dot = name.indexOf('.', prefix.length() + off);
		if (dot != -1) {
			String attributePrefix = name.substring(prefix.length() + off, dot);
			return attributePrefix;
		}
		return null;
	}

	private NameMapping attributeMapping;
	private NameMapping methodNameMapping;
	private MethodMapping methodMapping;

	/**
	 * Creates a new column mapping instance
	 */
	public ColumnMapping() {
		this("", null);
	}

	/**
	 * Creates a nested column mapping instance for handling nested attributes. For internal use.
	 *
	 * @param prefix the current nesting path, denoted by a dot separated string of attribute names
	 * @param parent the mappings of the parent object in the nested structure.
	 */
	public ColumnMapping(String prefix, ColumnMapping parent) {
		attributeMapping = new NameMapping(prefix, parent == null ? null : parent.attributeMapping);
		methodNameMapping = new NameMapping(prefix, parent == null ? null : parent.methodNameMapping);
		methodMapping = new MethodMapping(prefix, parent == null ? null : parent.methodMapping);
	}

	@Override
	public void attributeToColumnName(String attributeName, String columnName) {
		attributeMapping.mapToColumnName(attributeName, columnName);
	}

	@Override
	public void attributeToColumn(String attributeName, Enum<?> column) {
		attributeMapping.mapToColumn(attributeName, column);
	}

	@Override
	public void attributeToIndex(String attributeName, int columnIndex) {
		attributeMapping.mapToColumnIndex(attributeName, columnIndex);
	}

	@Override
	public void attributesToColumnNames(Map<String, String> mappings) {
		attributeMapping.mapToColumnNames(mappings);
	}

	@Override
	public void attributesToColumns(Map<String, Enum<?>> mappings) {
		attributeMapping.mapToColumns(mappings);
	}

	@Override
	public void attributesToIndexes(Map<String, Integer> mappings) {
		attributeMapping.mapToColumnIndexes(mappings);
	}

	private void methodToColumnName(MethodDescriptor method, String columnName) {
		methodMapping.mapToColumnName(method, columnName);
	}

	private void methodToColumn(MethodDescriptor method, Enum<?> column) {
		methodMapping.mapToColumn(method, column);
	}

	private void methodToIndex(MethodDescriptor method, int columnIndex) {
		methodMapping.mapToColumnIndex(method, columnIndex);
	}

	/**
	 * Tests whether a method or attribute has been mapped to a column.
	 * @param method a descriptor of getter/setter methods (can be {@code null})
	 * @param targetName name of a method or attribute
	 * @return {@code true} if the given method or attribute has been mapped to a column
	 */
	public boolean isMapped(MethodDescriptor method, String targetName) {
		return methodMapping.isMapped(method) || attributeMapping.isMapped(targetName) || methodNameMapping.isMapped(targetName);
	}

	/**
	 * Updates the mapping of a attribute/method so a mapped class member can target
	 * a user provided column.
	 *
	 * @param fieldMapping a class member that has should be mapped to a column
	 * @param targetName name of a method or attribute
	 * @param method a descriptor of getter/setter methods (can be {@code null})
	 *
	 * @return {@code true} if the mapping has been successfully updated.
	 */
	public boolean updateMapping(FieldMapping fieldMapping, String targetName, MethodDescriptor method) {
		if (methodMapping.isMapped(method)) {
			return methodMapping.updateFieldMapping(fieldMapping, method);
		} else if (attributeMapping.isMapped(targetName)) {
			return attributeMapping.updateFieldMapping(fieldMapping, targetName);
		} else if (methodNameMapping.isMapped(targetName)) {
			return methodNameMapping.updateFieldMapping(fieldMapping, targetName);
		}
		return false;
	}

	/**
	 * Returns object the nesting path associated with the current mapping.
	 * @return a dot separated string of nested attribute names
	 */
	public String getPrefix() {
		return methodMapping.prefix;
	}

	@Override
	public void methodToColumnName(String methodName, String columnName) {
		methodNameMapping.mapToColumnName(methodName, columnName);
	}

	@Override
	public void methodToColumn(String methodName, Enum<?> column) {
		methodNameMapping.mapToColumn(methodName, column);
	}

	@Override
	public void methodToIndex(String methodName, int columnIndex) {
		methodNameMapping.mapToColumnIndex(methodName, columnIndex);
	}

	@Override
	public void methodsToColumnNames(Map<String, String> mappings) {
		methodNameMapping.mapToColumnNames(mappings);
	}

	@Override
	public void methodsToColumns(Map<String, Enum<?>> mappings) {
		methodNameMapping.mapToColumns(mappings);
	}

	@Override
	public void methodsToIndexes(Map<String, Integer> mappings) {
		methodNameMapping.mapToColumnIndexes(mappings);
	}

	public void remove(String methodOrAttributeName) {
		attributeMapping.remove(methodOrAttributeName);
		methodNameMapping.remove(methodOrAttributeName);
		methodMapping.remove(methodOrAttributeName);
	}

	@Override
	public void methodToColumnName(String setterName, Class<?> parameterType, String columnName) {
		methodToColumnName(setter(setterName, parameterType), columnName);
	}

	@Override
	public void methodToColumn(String setterName, Class<?> parameterType, Enum<?> column) {
		methodToColumn(setter(setterName, parameterType), column);
	}

	@Override
	public void methodToIndex(String setterName, Class<?> parameterType, int columnIndex) {
		methodToIndex(setter(setterName, parameterType), columnIndex);
	}

	/**
	 * Returns the first-level names of all nested members whose attributes or methods have been mapped
	 * @return the names of nested objects to visit from the current object
	 */
	public Set<String> getNestedAttributeNames() {
		Set<String> out = new HashSet<String>();
		attributeMapping.extractPrefixes(out);
		methodNameMapping.extractPrefixes(out);
		methodMapping.extractPrefixes(out);
		return out;
	}

	@Override
	public ColumnMapper clone() {
		try {
			ColumnMapping out = (ColumnMapping) super.clone();
			out.attributeMapping = (NameMapping) this.attributeMapping.clone();
			out.methodNameMapping = (NameMapping) this.methodNameMapping.clone();
			out.methodMapping = (MethodMapping) this.methodMapping.clone();
			return out;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
}
