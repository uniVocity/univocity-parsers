package com.univocity.parsers.common.fields;

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

import static com.univocity.parsers.annotations.helpers.MethodDescriptor.*;

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
			for (MethodDescriptor k : this.getMappings().keySet()) {
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

	public ColumnMapping() {
		this("", null);
	}

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

	@Override
	public Map<String, Object> getAttributeMappings() {
		return attributeMapping.getMappings();
	}

	public void methodToColumnName(MethodDescriptor method, String columnName) {
		methodMapping.mapToColumnName(method, columnName);
	}

	public void methodToColumn(MethodDescriptor method, Enum<?> column) {
		methodMapping.mapToColumn(method, column);
	}

	public void methodToIndex(MethodDescriptor method, int columnIndex) {
		methodMapping.mapToColumnIndex(method, columnIndex);
	}

	public void methodsToColumnNames(Map<MethodDescriptor, String> mappings) {
		methodMapping.mapToColumnNames(mappings);
	}

	public void methodsToColumns(Map<MethodDescriptor, Enum<?>> mappings) {
		methodMapping.mapToColumns(mappings);
	}

	public void methodsToIndexes(Map<MethodDescriptor, Integer> mappings) {
		methodMapping.mapToColumnIndexes(mappings);
	}

	@Override
	public Map<MethodDescriptor, Object> getMethodMappings() {
		return methodMapping.getMappings();
	}

	@Override
	public Map<String, Object> getMethodNameMappings() {
		return methodNameMapping.getMappings();
	}

	public boolean isMapped(MethodDescriptor method, String targetName) {
		return methodMapping.isMapped(method) || attributeMapping.isMapped(targetName) || methodNameMapping.isMapped(targetName);
	}

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

	public String getPrefix() {
		return methodMapping.getPrefix();
	}

	@Override
	public void methodNameToColumnName(String methodName, String columnName) {
		methodNameMapping.mapToColumnName(methodName, columnName);
	}

	@Override
	public void methodNameToColumn(String methodName, Enum<?> column) {
		methodNameMapping.mapToColumn(methodName, column);
	}

	@Override
	public void methodNameToIndex(String methodName, int columnIndex) {
		methodNameMapping.mapToColumnIndex(methodName, columnIndex);
	}

	@Override
	public void methodNamesToColumnNames(Map<String, String> mappings) {
		methodNameMapping.mapToColumnNames(mappings);
	}

	@Override
	public void methodNamesToColumns(Map<String, Enum<?>> mappings) {
		methodNameMapping.mapToColumns(mappings);
	}

	@Override
	public void methodNamesToIndexes(Map<String, Integer> mappings) {
		methodNameMapping.mapToColumnIndexes(mappings);
	}

	public void remove(String methodOrAttributeName) {
		attributeMapping.remove(methodOrAttributeName);
		methodNameMapping.remove(methodOrAttributeName);
		methodMapping.remove(methodOrAttributeName);
	}

	@Override
	public void setterToColumnName(String setterName, Class<?> parameterType, String columnName) {
		methodToColumnName(setter(setterName, parameterType), columnName);
	}

	@Override
	public void setterToColumn(String setterName, Class<?> parameterType, Enum<?> column) {
		methodToColumn(setter(setterName, parameterType), column);
	}

	@Override
	public void setterToIndex(String setterName, Class<?> parameterType, int columnIndex) {
		methodToIndex(setter(setterName, parameterType), columnIndex);
	}

	@Override
	public void getterToColumnName(String getterName, Class<?> returnType, String columnName) {
		methodToColumnName(getter(getterName, returnType), columnName);
	}

	@Override
	public void getterToColumn(String getterName, Class<?> returnType, Enum<?> column) {
		methodToColumn(getter(getterName, returnType), column);
	}

	@Override
	public void getterToIndex(String getterName, Class<?> returnType, int columnIndex) {
		methodToIndex(getter(getterName, returnType), columnIndex);
	}

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
