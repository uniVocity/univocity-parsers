/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;


/**
 * An internal registry of annotated elements and their properties that have been set via a {@link com.univocity.parsers.annotations.Copy} annotation.
 */
public class AnnotationRegistry {

	private static final Map<AnnotatedElement, FieldAnnotations> modifiedAnnotations = new HashMap<AnnotatedElement, FieldAnnotations>();

	/**
	 * Associates a value to a given annotation attribute
	 * @param annotatedElement a method or field that has an annotation whose properties are changed by a {@link com.univocity.parsers.annotations.Copy} annotation
	 * @param annotation the altered annotation of the given annotatedElement
	 * @param attribute the attribute of the altered annotation
	 * @param newValue the value of the given attribute of the altered annotation.
	 */
	static synchronized final void setValue(AnnotatedElement annotatedElement, Annotation annotation, String attribute, Object newValue) {
		FieldAnnotations attributes = modifiedAnnotations.get(annotatedElement);
		if (attributes == null) {
			attributes = new FieldAnnotations();
			modifiedAnnotations.put(annotatedElement, attributes);
		}
		attributes.setValue(annotation, attribute, newValue);
	}

	/**
	 * Returns the a value to a given annotation attribute that might have been modified by a {@link com.univocity.parsers.annotations.Copy} annotation
	 *
	 * @param annotatedElement a method or field that has an annotation whose properties might have been changed by a {@link com.univocity.parsers.annotations.Copy} annotation
	 * @param annotation the possibly altered annotation of the given annotatedElement
	 * @param attribute the attribute of the possibly altered annotation
	 * @param valueIfNull the value to return from the unmodified annotation, if it has not been changed by a {@link com.univocity.parsers.annotations.Copy}
	 *
	 * @param <T> the expected value type to be returned by this method.
	 *
	 * @return the value associated with the given annotation property.
	 */
	public static synchronized final <T> T getValue(AnnotatedElement annotatedElement, Annotation annotation, String attribute, T valueIfNull) {
		if (annotatedElement == null) {
			return valueIfNull;
		}
		Object value = getValue(annotatedElement, annotation, attribute);
		if (value == null) {
			return valueIfNull;
		}
		return (T) value;
	}

	/**
	 * Returns the a value to a given annotation attribute that might have been modified by a {@link com.univocity.parsers.annotations.Copy} annotation
	 *
	 * @param annotatedElement a method or field that has an annotation whose properties might have been changed by a {@link com.univocity.parsers.annotations.Copy} annotation
	 * @param annotation the possibly altered annotation of the given annotatedElement
	 * @param attribute the attribute of the possibly altered annotation
	 *
	 * @return the value associated with the given annotation property, or {@code null} if it has not been modified by a {@link com.univocity.parsers.annotations.Copy}
	 */
	static synchronized final Object getValue(AnnotatedElement annotatedElement, Annotation annotation, String attribute) {
		FieldAnnotations attributes = modifiedAnnotations.get(annotatedElement);
		if (attributes == null) {
			return null;
		}
		return attributes.getValue(annotation, attribute);
	}

	public static final void reset() {
		modifiedAnnotations.clear();
	}

	private static class FieldAnnotations {
		private Map<Annotation, AnnotationAttributes> annotations = new HashMap<Annotation, AnnotationAttributes>();

		private void setValue(Annotation annotation, String attribute, Object newValue) {
			AnnotationAttributes attributes = annotations.get(annotation);
			if (attributes == null) {
				attributes = new AnnotationAttributes();
				annotations.put(annotation, attributes);
			}
			attributes.setAttribute(attribute, newValue);
		}

		private Object getValue(Annotation annotation, String attribute) {
			AnnotationAttributes attributes = annotations.get(annotation);
			if (attributes == null) {
				return null;
			}
			return attributes.getAttribute(attribute);
		}
	}

	private static class AnnotationAttributes {
		private Map<String, Object> attributes = new HashMap<String, Object>();

		private void setAttribute(String attribute, Object newValue) {
			if (!attributes.containsKey(attribute)) {
				attributes.put(attribute, newValue);
			} else {
				Object existingValue = attributes.get(attribute);
				if (existingValue == null || newValue == null) {
					return;
				}

				//handles single values copied to a parent annotation that accepts an array
				Class originalClass = existingValue.getClass();
				Class newClass = newValue.getClass();

				if (originalClass != newClass && newClass.isArray() && newClass.getComponentType() == existingValue.getClass()) {
					Object array = Array.newInstance(originalClass, 1);
					Array.set(array, 0, existingValue);
					attributes.put(attribute, array);
				}
			}
		}

		private Object getAttribute(String attribute) {
			return attributes.get(attribute);
		}
	}
}
