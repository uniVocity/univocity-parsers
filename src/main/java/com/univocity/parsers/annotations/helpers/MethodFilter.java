/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
 * A filter for annotated methods. Used internally to exclude setters or getters from the list of fields to be processed,
 * accordingly to the use case: when parsing into beans, only setter methods are to be considered. When writing values
 * in beans to an output, only the getter methods should be used.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public enum MethodFilter {
	/**
	 * Rejects any method that returns {@code void} or has a parameter list.
	 */
	ONLY_GETTERS(new Filter() {
		@Override
		public boolean reject(Method method) {
			return method.getReturnType() == void.class || method.getParameterTypes().length != 0;
		}
	}),
	/**
	 * Rejects any method that doesn't accept a single parameter.
	 */
	ONLY_SETTERS(new Filter() {
		@Override
		public boolean reject(Method method) {
			return method.getParameterTypes().length != 1;
		}
	});


	private Filter filter;

	MethodFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Tests whether a method is not a getter or setter and should be rejected.
	 *
	 * @param method the method to be tested
	 *
	 * @return {@code true} if the given method should be rejected, {@code false} otherwise
	 */
	public boolean reject(Method method) {
		return filter.reject(method);
	}

	private interface Filter {
		boolean reject(Method method);
	}

	/**
	 * Creates a descriptor for a getter or setter method
	 *
	 * @param prefix a dot separated string denoting a path of nested object names
	 * @param method a actual class method to be associated with this prefix
	 * @return a descriptor for the given method
	 */
	public MethodDescriptor toDescriptor(String prefix, Method method) {
		if (reject(method)) {
			return null;
		}
		if (this == MethodFilter.ONLY_SETTERS) {
			return MethodDescriptor.setter(prefix, method);
		} else {
			return MethodDescriptor.getter(prefix, method);
		}
	}
}
