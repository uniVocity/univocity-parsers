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
package com.univocity.parsers.annotations.helpers;

import java.lang.reflect.*;

/**
 * A very basic descriptor or getter/setter methods
 */
public final class MethodDescriptor {

	private final String prefixedName;
	private final String name;
	private final String prefix;
	private final Class<?> parameterType;
	private final Class<?> returnType;

	private final String string;

	private MethodDescriptor(String name, Class<?> parameterType, Class<?> returnType) {
		prefixedName = name;
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1) {
			this.name = name;
			this.prefix = "";
		} else {
			this.name = name.substring(lastDot + 1);
			this.prefix = name.substring(0, lastDot);
		}
		this.parameterType = parameterType;
		this.returnType = returnType;
		this.string = generateString();
	}

	private MethodDescriptor(String prefix, String name, Class<?> parameterType, Class<?> returnType) {
		this.prefixedName = prefix + '.' + name;
		this.name = name;
		this.prefix = prefix;
		this.parameterType = parameterType;
		this.returnType = returnType;
		this.string = generateString();
	}

	private String generateString() {
		StringBuilder out = new StringBuilder("method ");
		if (returnType != null) {
			out.append(returnType.getName());
			out.append(' ');
		}
		if (prefix.isEmpty()) {
			out.append(name);
		} else {
			out.append(prefix);
			out.append('.');
			out.append(name);
		}

		if (parameterType != null) {
			out.append('(');
			out.append(parameterType.getName());
			out.append(')');
		} else {
			out.append("()");
		}
		return out.toString();
	}

	/**
	 * Creates a descriptor for a setter method
	 * @param name name of the setter method
	 * @param parameterType the parameter type accepted by the given setter method
	 * @return a "setter" method descriptor
	 */
	public static MethodDescriptor setter(String name, Class<?> parameterType) {
		return new MethodDescriptor(name, parameterType, null);
	}

	/**
	 * Creates a descriptor for a getter method
	 * @param name name of the getter method
	 * @param returnType the return type of the given getter method
	 * @return a "getter" method descriptor
	 */
	public static MethodDescriptor getter(String name, Class<?> returnType) {
		return new MethodDescriptor(name, null, returnType);
	}


	/**
	 * Creates a descriptor for a setter method
	 * @param prefix a dot separated string denoting a path of nested object names
	 * @param method a actual class method to be associated with this prefix
	 * @return a "setter" method descriptor
	 */
	static MethodDescriptor setter(String prefix, Method method) {
		return new MethodDescriptor(prefix, method.getName(), method.getParameterTypes()[0], null);
	}

	/**
	 * Creates a descriptor for a getter method
	 * @param prefix a dot separated string denoting a path of nested object names
	 * @param method a actual class method to be associated with this prefix
	 * @return a "getter" method descriptor
	 */
	static MethodDescriptor getter(String prefix, Method method) {
		return new MethodDescriptor(prefix, method.getName(), null, method.getReturnType());
	}

	/**
	 * Returns the method name, without the prefix
	 * @return the method name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the prefix: a dot separated string denoting a path of nested object names (e.g. customer.contact).
	 *
	 * @return the object nesting path associated with a method.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the parameter type associated with a method, if available
	 * @return the type of parameter accepted by this method if it is a setter, or {@code null} if a getter is being represented.
	 */
	public Class<?> getParameterType() {
		return parameterType;
	}

	/**
	 * Returns the return type associated with a method, if available
	 * @return the return type of this method if it is a getter, or {@code null} if a setter is being represented.
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * Returns full path to a method, (e.g. {@code getName} or {@code person.getName}
	 * @return the path to the given method.
	 */
	public String getPrefixedName() {
		return prefixedName;
	}

	public String toString() {
		return string;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}

		return string.equals(o.toString());
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}
}
