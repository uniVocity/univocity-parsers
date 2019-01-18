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

import com.univocity.parsers.annotations.*;

import java.util.*;

/**
 * A utility that allows users to manually define mappings from
 * attributes/methods of a given class to columns to be parsed or written.
 *
 * This removes the requirement of having classes annotated with {@link Parsed} or
 * {@link Nested}.
 *
 * Mappings defined manually take precedence over annotations.
 *
 * @see ColumnMapping
 */
public interface ColumnMapper extends Cloneable {

	/**
	 * Maps an attribute to a column name.
	 *
	 * @param attributeName the name of the attribute.
	 *                      Use the dot character to access attributes of nested objects,
	 *                      e.g. {@code contact.mobile} will target the attribute "mobile"
	 *                      from a Contact attribute inside a Customer class.
	 * @param columnName the name of the column that:
	 *                      (a) when parsing, will be read from to populate the given attribute of an object;
	 *                      (b) when writing, will receive the value of the given attribute of an object;
	 */
	void attributeToColumnName(String attributeName, String columnName);

	/**
	 * Maps an attribute to a column.
	 *
	 * @param attributeName the name of the attribute.
	 *                      Use the dot character to access attributes of nested objects,
	 *                      e.g. {@code contact.mobile} will target the attribute "mobile"
	 *                      from a Contact attribute inside a Customer class.
	 * @param column an enumeration representing the column that:
	 *                      (a) when parsing, will be read from to populate the given attribute of an object;
	 *                      (b) when writing, will receive the value of the given attribute of an object;
	 */
	void attributeToColumn(String attributeName, Enum<?> column);

	/**
	 * Maps an attribute to a column position.
	 *
	 * @param attributeName the name of the attribute.
	 *                      Use the dot character to access attributes of nested objects,
	 *                      e.g. {@code contact.mobile} will target the attribute "mobile"
	 *                      from a Contact attribute inside a Customer class.
	 * @param columnIndex  the position of the column that:
	 *                      (a) when parsing, will be read from to populate the given attribute of an object;
	 *                      (b) when writing, will receive the value of the given attribute of an object;
	 */
	void attributeToIndex(String attributeName, int columnIndex);

	/**
	 * Maps multiple attributes to multiple column names.
	 *
	 * @param mappings a map of attribute names associated with a corresponding column name, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of the attribute. Use the dot character to access attributes of nested objects,
	 *     e.g. {@code contact.mobile} will target the attribute "mobile"
	 *     from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is the name of the column that:
	 *     (a) when parsing, will be read from to populate the given attribute of an object;
	 *     (b) when writing, will receive the value of the given attribute of an object;
	 * </li>
	 * </ul>
	 */
	void attributesToColumnNames(Map<String, String> mappings);

	/**
	 * Maps multiple attributes to multiple columns.
	 *
	 * @param mappings a map of attribute names associated with a corresponding column, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of the attribute. Use the dot character to access attributes of nested objects,
	 *     e.g. {@code contact.mobile} will target the attribute "mobile"
	 *     from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is an enumeration representing the column that:
	 *     (a) when parsing, will be read from to populate the given attribute of an object;
	 *     (b) when writing, will receive the value of the given attribute of an object;
	 * </li>
	 * </ul>
	 */
	void attributesToColumns(Map<String, Enum<?>> mappings);

	/**
	 * Maps multiple attributes to multiple column positions.
	 *
	 * @param mappings a map of attribute names associated with a corresponding column, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of the attribute. Use the dot character to access attributes of nested objects,
	 *     e.g. {@code contact.mobile} will target the attribute "mobile"
	 *     from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is an integer representing the position of the column that:
	 *     (a) when parsing, will be read from to populate the given attribute of an object;
	 *     (b) when writing, will receive the value of the given attribute of an object;
	 * </li>
	 * </ul>
	 */
	void attributesToIndexes(Map<String, Integer> mappings);

	/**
	 * Maps a setter method to a column name. Use when {@link #methodToColumnName(String, String)}
	 * is not enough to uniquely identify the method you need (e.g. when there are 
	 * overloaded methods with different parameter types)
	 *
	 * Used only for parsing. Will be ignored when writing.
	 *
	 * @param setterName the name of the setter method.
	 *                      Use the dot character to access methods of nested objects,
	 *                      e.g. {@code contact.mobile} will target the setter method "mobile(String)"
	 *                      from a Contact attribute inside a Customer class.
	 * @param parameterType the type of the parameter used in the given setter name.
	 * @param columnName the name of the column that when parsing, will be read from to invoke given setter method of an object
	 *
	 */
	void methodToColumnName(String setterName, Class<?> parameterType, String columnName);

	/**
	 * Maps a setter method to a column. Use when {@link #methodToColumnName(String, String)}
	 * is not enough to uniquely identify the method you need (e.g. when there are 
	 * overloaded methods with different parameter types)
	 *
	 * Used only for parsing. Will be ignored when writing.
	 *
	 * @param setterName the name of the setter method.
	 *                      Use the dot character to access methods of nested objects,
	 *                      e.g. {@code contact.mobile} will target the setter method "mobile(String)"
	 *                      from a Contact attribute inside a Customer class.
	 * @param parameterType the type of the parameter used in the given setter name.
	 * @param column an enumeration representing the column that when parsing, will be read from to invoke given setter method of an object
	 */
	void methodToColumn(String setterName, Class<?> parameterType, Enum<?> column);


	/**
	 * Maps a setter method to a column position. Use when {@link #methodToColumnName(String, String)}
	 * is not enough to uniquely identify the method you need (e.g. when there are
	 * overloaded methods with different parameter types)
	 *
	 * Used only for parsing. Will be ignored when writing.
	 *
	 * @param setterName the name of the setter method.
	 *                      Use the dot character to access methods of nested objects,
	 *                      e.g. {@code contact.mobile} will target the setter method "mobile(String)"
	 *                      from a Contact attribute inside a Customer class.
	 * @param parameterType the type of the parameter used in the given setter name.
	 * @param columnIndex the position of the column that when parsing, will be read from to invoke given setter method of an object
	 */
	void methodToIndex(String setterName, Class<?> parameterType, int columnIndex);

	/**
	 * Maps a method to a column name. 
	 *
	 * When parsing, only "setter" methods will be used i.e. the given method accepts one parameter. 
	 * If the method is overridden, use {@link #methodToColumnName(String, Class, String)}
	 * to specify the exact parameter type to match the appropriate setter method. 
	 *
	 * When writing, only "getter" methods will be used i.e. the given method 
	 * doesn't accept any parameters and returns a value.
	 *
	 * @param methodName the name of the method.
	 *                      Use the dot character to access methods of nested objects,
	 *                      e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 *                      from a Contact attribute inside a Customer class.
	 * @param columnName the name of the column that:
	 *        (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	      (b) when writing, will receive the value returned by the given getter method of an object;
	 *
	 */
	void methodToColumnName(String methodName, String columnName);


	/**
	 * Maps a method to a column. 
	 *
	 * When parsing, only "setter" methods will be used i.e. the given method accepts one parameter. 
	 * If the method is overridden, use {@link #methodToColumnName(String, Class, String)}
	 * to specify the exact parameter type to match the appropriate setter method. 
	 *
	 * When writing, only "getter" methods will be used i.e. the given method 
	 * doesn't accept any parameters and returns a value.
	 *
	 * @param methodName the name of the method.
	 *                      Use the dot character to access methods of nested objects,
	 * 	 *                      e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 * 	 *                      from a Contact attribute inside a Customer class.
	 * @param column an enumeration representing the column that:
	 *        (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	      (b) when writing, will receive the value returned by the given getter method of an object;
	 *
	 */
	void methodToColumn(String methodName, Enum<?> column);

	/**
	 * Maps a method to a column position. 
	 *
	 * When parsing, only "setter" methods will be used i.e. the given method accepts one parameter. 
	 * If the method is overridden, use {@link #methodToColumnName(String, Class, String)}
	 * to specify the exact parameter type to match the appropriate setter method. 
	 *
	 * When writing, only "getter" methods will be used i.e. the given method 
	 * doesn't accept any parameters and returns a value.
	 *
	 * @param methodName the name of the method.
	 *                      Use the dot character to access methods of nested objects,
	 * 	 *                      e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 * 	 *                      from a Contact attribute inside a Customer class.
	 * @param columnIndex the position of the column that:
	 *	      (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	      (b) when writing, will receive the value returned by the given getter method of an object;
	 *
	 */
	void methodToIndex(String methodName, int columnIndex);


	/**
	 * Maps multiple methods to multiple column names.
	 *
	 * @param mappings a map of methods names associated with a corresponding column name, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of a method. Use the dot character to access attributes of nested objects,
	 *        e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 * 	      from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is the name of the column that:
	 *     (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	   (b) when writing, will receive the value returned by the given getter method of an object;
	 * </li>
	 * </ul>
	 */
	void methodsToColumnNames(Map<String, String> mappings);

	/**
	 * Maps multiple methods to multiple columns.
	 *
	 * @param mappings a map of methods names associated with a corresponding column, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of a method. Use the dot character to access attributes of nested objects,
	 *        e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 * 	      from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is an enumeration representing the column that:
	 *     (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	   (b) when writing, will receive the value returned by the given getter method of an object;
	 * </li>
	 * </ul>
	 */
	void methodsToColumns(Map<String, Enum<?>> mappings);

	/**
	 * Maps multiple methods to multiple column positions.
	 *
	 * @param mappings a map of methods names associated with a corresponding column position, where:
	 *
	 * <ul>
	 * <li>Each <b>key</b> is the name of a method. Use the dot character to access attributes of nested objects,
	 *        e.g. {@code contact.mobile} will target the method "mobile(String)" when parsing, or "String mobile()" when writing,
	 * 	      from a Contact attribute inside a Customer class.
	 * </li>
	 * <li>Each <b>value</b> is an integer representing the position of the column that:
	 *     (a) when parsing, will be read from to invoke given setter method of an object;
	 * 	   (b) when writing, will receive the value returned by the given getter method of an object;
	 * </li>
	 * </ul>
	 */
	void methodsToIndexes(Map<String, Integer> mappings);

	/**
	 * Creates a deep copy of this object with all its mappings. Changes to the clone won't affect the original instance.
	 * @return a clone of the current mappings.
	 */
	ColumnMapper clone();

	/**
	 * Removes any mappings that target a given method or attribute name.
	 * @param methodOrAttributeName the name of the method or attribute to be removed.
	 */
	void remove(String methodOrAttributeName);
}
