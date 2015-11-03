/*
 * Copyright (c) 2015. uniVocity Software Pty Ltd
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

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Helper class used to obtain property descriptors from annotated java beans whose values are set via reflection.
 * This class was implemented to eliminate direct compile-time dependency with {@link java.beans.Introspector} and
 * other classes in the {@code java.beans.*} package. This is required to allow Android developers to use uniVocity-parsers.
 * Android developers should add have <a href="https://code.google.com/p/openbeans/downloads/detail?name=openbeans-1.0.jar">openbeans-1.0.jar</a>
 * in their classpath to be able to use uniVocity-parsers.
 *
 * When available, the classes from package {@code com.googlecode.openbeans.*} will be used, otherwise the
 * bean introspection classes classes from {@code java.beans.*} package will be loaded.
 *
 * If everything fails, then the parser will try to manipulate fields in annotated java beans directly, instead
 * of using their getters and setters.
 */
public final class BeanHelper {

	private static final PropertyWrapper[] EMPTY = new PropertyWrapper[0];

	private static final Class<?> introspectorClass = findIntrospectorImplementationClass();
	private static final Method beanInfoMethod = getBeanInfoMethod();
	private static final Method propertyDescriptorMethod = getMethod("getPropertyDescriptors", beanInfoMethod, false);

	static Method PROPERTY_WRITE_METHOD = getMethod("getWriteMethod", propertyDescriptorMethod, true);
	static Method PROPERTY_READ_METHOD = getMethod("getReadMethod", propertyDescriptorMethod, true);
	static Method PROPERTY_NAME_METHOD = getMethod("getName", propertyDescriptorMethod, true);

	private static final Map<Class<?>, WeakReference<PropertyWrapper[]>> descriptors = new ConcurrentHashMap<Class<?>, WeakReference<PropertyWrapper[]>>();

	private BeanHelper() {

	}

	/**
	 * Returns the property descriptors of all properties available from a class
	 * @param beanClass the class whose property descriptors should be returned
	 * @return an array of all property descriptors of the given class. Might be empty.
	 */
	public static PropertyWrapper[] getPropertyDescriptors(Class<?> beanClass) {
		if (propertyDescriptorMethod == null) {
			return EMPTY;
		}
		PropertyWrapper[] out = null;
		WeakReference<PropertyWrapper[]> reference = descriptors.get(beanClass);
		if (reference != null) {
			out = reference.get();
		}

		if (out == null) {
			try {
				Object beanInfo = beanInfoMethod.invoke(null, beanClass, Object.class);
				Object[] propertyDescriptors = (Object[]) propertyDescriptorMethod.invoke(beanInfo);
				out = new PropertyWrapper[propertyDescriptors.length];

				for (int i = 0; i < propertyDescriptors.length; i++) {
					out[i] = new PropertyWrapper(propertyDescriptors[i]);
				}

			} catch (Exception ex) {
				out = EMPTY;
			}
			descriptors.put(beanClass, new WeakReference<PropertyWrapper[]>(out));
		}

		return out;
	}

	private static Class<?> findIntrospectorImplementationClass() {
		try {
			return Class.forName("com.googlecode.openbeans.Introspector");
		} catch (Throwable e1) {
			try {
				return Class.forName("java.beans.Introspector");
			} catch (Throwable e2) {
				return null;
			}
		}
	}

	private static Method getBeanInfoMethod() {
		if (introspectorClass == null) {
			return null;
		}
		try {
			return introspectorClass.getMethod("getBeanInfo", Class.class, Class.class);
		} catch (Throwable e) {
			return null;
		}
	}


	private static Method getMethod(String methodName, Method method, boolean fromComponentType) {
		if (method == null) {
			return null;
		}
		try {
			Class<?> returnType = method.getReturnType();
			if (fromComponentType) {
				returnType = returnType.getComponentType();
			}
			return returnType.getMethod(methodName);
		} catch (Exception ex) {
			return null;
		}
	}
}
