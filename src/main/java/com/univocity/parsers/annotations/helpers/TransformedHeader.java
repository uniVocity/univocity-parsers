/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

import static com.univocity.parsers.annotations.helpers.AnnotationHelper.*;

/**
 * A pair associating a Field of an annotated class to an optional {@link HeaderTransformer} obtained from
 * {@link Nested#headerTransformer()} when nested classes are used to process beans.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class TransformedHeader {

	private final AnnotatedElement target;
	private final Field field;
	private final Method method;
	private final HeaderTransformer transformer;
	private int index = -2;

	public TransformedHeader(AnnotatedElement target, HeaderTransformer transformer) {
		if (target instanceof Field) {
			field = (Field) target;
			method = null;
		} else {
			method = (Method) target;
			field = null;
		}
		this.target = target;
		this.transformer = transformer;
	}

	/**
	 * Returns the name to be used as a header based on a given field and its {@link Parsed} annotation.
	 *
	 * @return the header name to be used for the given field.
	 */
	public String getHeaderName() {
		if (target == null) {
			return null;
		}
		String name = null;

		Parsed annotation = findAnnotation(target, Parsed.class);
		if (annotation != null) {
			if (annotation.field().length == 0) {
				name = getTargetName();
			} else {
				name = annotation.field()[0];
			}
		}

		if (transformer != null) {
			if (field != null) {
				return transformer.transformName(field, name);
			} else {
				return transformer.transformName(method, name);
			}
		}

		return name;
	}

	/**
	 * Returns the index that determines which column the current field represents, as specified by {@link Parsed#index()}
	 *
	 * @return the current header index.
	 */
	public int getHeaderIndex() {
		if (index == -2) {
			Parsed annotation = findAnnotation(target, Parsed.class);
			if (annotation != null) {
				index = annotation.index();
				if (index != -1) {
					if (transformer != null) {
						if (field != null) {
							index = transformer.transformIndex(field, index);
						} else {
							index = transformer.transformIndex(method, index);
						}
					}
				}
			} else {
				index = -1;
			}
		}
		return index;
	}

	/**
	 * Returns the original attribute name of the field in its containing class.
	 *
	 * @return the original attribute name of the field
	 */
	public String getTargetName() {
		if (target == null) {
			return null;
		}
		if (field != null) {
			return field.getName();
		} else {
			return method.getName();
		}
	}

	/**
	 * Returns the {@link AnnotatedElement} used to read/write values from/to.
	 *
	 * @return the field or method being manipulated by the parser/writer when processing java beans
	 */
	public AnnotatedElement getTarget() {
		return target;
	}

	/**
	 * Returns {@code true} if this {@link AnnotatedElement} is a {@link Method} with parameters and can only be used
	 * for writing values into the java bean.
	 *
	 * @return a flag indicating whether this is a method that allows writing values only.
	 */
	public boolean isWriteOnly() {
		if (method != null) {
			return method.getParameterTypes().length != 0;
		}
		return false;
	}

	/**
	 * Returns {@code true} if this {@link AnnotatedElement} is a {@link Method} with no parameters and a return type which can only be used
	 * for reading values from the java bean.
	 *
	 * @return a flag indicating whether this is a method that allows reading values only.
	 */
	public boolean isReadOly() {
		if (method != null) {
			return method.getParameterTypes().length == 0 && method.getReturnType() != void.class;
		}
		return false;
	}

	public String describe() {
		return AnnotationHelper.describeElement(target);
	}
}
