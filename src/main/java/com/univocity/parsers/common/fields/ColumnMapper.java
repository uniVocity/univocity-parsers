package com.univocity.parsers.common.fields;

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

public interface ColumnMapper {

	void attributeToColumnName(String attributeName, String columnName);

	void attributeToColumn(String attributeName, Enum<?> column);

	void attributeToIndex(String attributeName, int columnIndex);

	void attributesToColumnNames(Map<String, String> mappings);

	void attributesToColumns(Map<String, Enum<?>> mappings);

	void attributesToIndexes(Map<String, Integer> mappings);

	Map<String, Object> getAttributeMappings();

	boolean isAttributeMapped(String attributeName);

	void methodToColumnName(MethodDescriptor method, String columnName);

	void methodToColumn(MethodDescriptor method, Enum<?> column);

	void methodToIndex(MethodDescriptor method, int columnIndex);

	void methodsToColumnNames(Map<MethodDescriptor, String> mappings);

	void methodsToColumns(Map<MethodDescriptor, Enum<?>> mappings);

	void methodsToIndexes(Map<MethodDescriptor, Integer> mappings);

	Map<MethodDescriptor, Object> getMethodMappings();

	boolean isMethodMapped(MethodDescriptor method);

	boolean updateAttributeMapping(FieldMapping fieldMapping, String attributeName);

	boolean updateMethodMapping(FieldMapping fieldMapping, MethodDescriptor method);
}
