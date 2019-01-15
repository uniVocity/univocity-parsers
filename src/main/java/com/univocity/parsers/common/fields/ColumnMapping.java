package com.univocity.parsers.common.fields;

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

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
	}

	private final NameMapping attributeMapping;
	private final NameMapping methodNameMapping;
	private final MethodMapping methodMapping;


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

	@Override
	public boolean isAttributeMapped(String attributeName) {
		return attributeMapping.isMapped(attributeName);
	}

	@Override
	public void methodToColumnName(MethodDescriptor method, String columnName) {
		methodMapping.mapToColumnName(method, columnName);
	}

	@Override
	public void methodToColumn(MethodDescriptor method, Enum<?> column) {
		methodMapping.mapToColumn(method, column);
	}

	@Override
	public void methodToIndex(MethodDescriptor method, int columnIndex) {
		methodMapping.mapToColumnIndex(method, columnIndex);
	}

	@Override
	public void methodsToColumnNames(Map<MethodDescriptor, String> mappings) {
		methodMapping.mapToColumnNames(mappings);
	}

	@Override
	public void methodsToColumns(Map<MethodDescriptor, Enum<?>> mappings) {
		methodMapping.mapToColumns(mappings);
	}

	@Override
	public void methodsToIndexes(Map<MethodDescriptor, Integer> mappings) {
		methodMapping.mapToColumnIndexes(mappings);
	}

	@Override
	public Map<MethodDescriptor, Object> getMethodMappings() {
		return methodMapping.getMappings();
	}

	@Override
	public boolean isMethodMapped(MethodDescriptor method) {
		return methodMapping.isMapped(method);
	}

	@Override
	public boolean isMethodNameMapped(String methodName) {
		return methodNameMapping.isMapped(methodName);
	}

	@Override
	public Map<String, Object> getMethodNameMappings() {
		return methodNameMapping.getMappings();
	}

	@Override
	public boolean updateAttributeMapping(FieldMapping fieldMapping, String attributeName) {
		return attributeMapping.updateFieldMapping(fieldMapping, attributeName);
	}

	@Override
	public boolean updateMethodMapping(FieldMapping fieldMapping, MethodDescriptor method) {
		return methodMapping.updateFieldMapping(fieldMapping, method);
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
	public void methodsNameToColumnNames(Map<String, String> mappings) {
		methodNameMapping.mapToColumnNames(mappings);
	}

	@Override
	public void methodsNameToColumns(Map<String, Enum<?>> mappings) {
		methodNameMapping.mapToColumns(mappings);
	}

	@Override
	public void methodsNameToIndexes(Map<String, Integer> mappings) {
		methodNameMapping.mapToColumnIndexes(mappings);
	}
}
