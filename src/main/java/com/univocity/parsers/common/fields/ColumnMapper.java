package com.univocity.parsers.common.fields;

import com.univocity.parsers.annotations.helpers.*;

import java.util.*;

public interface ColumnMapper extends Cloneable{

	void attributeToColumnName(String attributeName, String columnName);

	void attributeToColumn(String attributeName, Enum<?> column);

	void attributeToIndex(String attributeName, int columnIndex);

	void attributesToColumnNames(Map<String, String> mappings);

	void attributesToColumns(Map<String, Enum<?>> mappings);

	void attributesToIndexes(Map<String, Integer> mappings);

	void setterToColumnName(String setterName, Class<?> parameterType, String columnName);

	void setterToColumn(String setterName, Class<?> parameterType, Enum<?> column);

	void setterToIndex(String setterName, Class<?> parameterType, int columnIndex);

	void getterToColumnName(String getterName, Class<?> returnType, String columnName);

	void getterToColumn(String getterName, Class<?> returnType, Enum<?> column);

	void getterToIndex(String getterName, Class<?> returnType, int columnIndex);

	void methodsToColumnNames(Map<MethodDescriptor, String> mappings);

	void methodsToColumns(Map<MethodDescriptor, Enum<?>> mappings);

	void methodsToIndexes(Map<MethodDescriptor, Integer> mappings);

	void methodNameToColumnName(String methodName, String columnName);

	void methodNameToColumn(String methodName, Enum<?> column);

	void methodNameToIndex(String methodName, int columnIndex);

	void methodNamesToColumnNames(Map<String, String> mappings);

	void methodNamesToColumns(Map<String, Enum<?>> mappings);

	void methodNamesToIndexes(Map<String, Integer> mappings);

	ColumnMapper clone();

	void remove(String methodOrAttributeName);
}
