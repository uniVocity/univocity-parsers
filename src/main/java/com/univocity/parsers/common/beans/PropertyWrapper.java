/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
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
 */

package com.univocity.parsers.common.beans;

import java.lang.reflect.*;

/**
 * Wrapper for a implementation of PropertyDescriptor from either {@code java.beans.PropertyDescriptor}
 * or {@code com.googlecode.openbeans.PropertyDescriptor}.
 *
 * Used to eliminate compile-time dependencies with package {@code java.beans.*} which is not
 * available to Android developers.
 */
public final class PropertyWrapper {

	private static final Method NO_METHOD = getNullMethod();
	private static final String NO_NAME = "!!NO_NAME!!";


	private final Object propertyDescriptor;
	private Method writeMethod;
	private Method readMethod;
	private String name;

	PropertyWrapper(Object propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	/**
	 * Returns the method that should be used to write a value to a property of a Java bean.
	 * Might be {@code null}.
	 *
	 * @return The method that should be used to write the property value, if available.
	 **/
	public final Method getWriteMethod() {
		if (writeMethod == null) {
			writeMethod = (Method) invoke(propertyDescriptor, BeanHelper.PROPERTY_WRITE_METHOD);
		}
		return writeMethod == NO_METHOD ? null : writeMethod;
	}

	/**
	 * Returns the method that should be used to read the value of a property of a Java bean.
	 * Might be {@code null}.
	 *
	 * @return The method that should be used to read the property value, if available.
	 */
	public final Method getReadMethod() {
		if (readMethod == null) {
			readMethod = (Method) invoke(propertyDescriptor, BeanHelper.PROPERTY_READ_METHOD);
		}
		return readMethod == NO_METHOD ? null : readMethod;
	}

	/**
	 * Returns the name of a property of a Java bean.
	 * Might be {@code null}.
	 *
	 * @return The property name.
	 */
	public final String getName() {
		if (name == null) {
			name = (String) invoke(propertyDescriptor, BeanHelper.PROPERTY_NAME_METHOD);
		}
		return name == NO_NAME ? null : name;
	}

	private static Object invoke(Object propertyDescriptor, Method method) {
		try {
			return method.invoke(propertyDescriptor);
		} catch (Exception ex) {
			return null;
		}
	}

	private static Method getNullMethod() {
		try {
			return Object.class.getMethod("hashCode");
		} catch (NoSuchMethodException e) {
			// should never happen.
			return null;
		}
	}
}
