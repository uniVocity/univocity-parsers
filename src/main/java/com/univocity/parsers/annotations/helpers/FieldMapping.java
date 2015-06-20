/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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

import java.beans.*;
import java.lang.reflect.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;

/**
 * A helper class with information about the location of an field annotated with {@link Parsed} in a record.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FieldMapping {
	private final Field field;
	private int index;
	private String fieldName;
	private final Class<?> beanClass;
	private final Method readMethod;
	private final Method writeMethod;
	private boolean accessible = false;

	/**
	 * Creates the mapping and identifies how it is mapped (by name or by index)
	 * @param beanClass the class that contains a the given field.
	 * @param field a {@link java.lang.reflect.Field} annotated with {@link Parsed}
	 * @param property the property descriptor of this field, if any. If this bean does not have getters/setters, it will be accessed directly.
	 */
	public FieldMapping(Class<?> beanClass, Field field, PropertyDescriptor property) {
		this.beanClass = beanClass;
		this.field = field;
		this.readMethod = property != null ? property.getReadMethod() : null;
		this.writeMethod = property != null ? property.getWriteMethod() : null;

		determineFieldMapping();
	}

	private void determineFieldMapping() {
		Parsed parsed = field.getAnnotation(Parsed.class);
		String name = "";

		if (parsed != null) { //field can be annotated with @Nested only. In this case we get the original field name
			index = parsed.index();

			if (index >= 0) {
				fieldName = null;
				return;
			}

			name = parsed.field();
		}

		if (name.isEmpty()) {
			name = field.getName();
		}
		fieldName = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldMapping that = (FieldMapping) o;

		if (index != that.index) return false;
		if (!field.equals(that.field)) return false;
		if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
		return beanClass.equals(that.beanClass);

	}

	@Override
	public int hashCode() {
		int result = field.hashCode();
		result = 31 * result + index;
		result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
		result = 31 * result + beanClass.hashCode();
		return result;
	}

	public boolean isMappedToIndex() {
		return index >= 0;
	}

	public boolean isMappedToField() {
		return index < 0;
	}

	public int getIndex() {
		return index;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Field getField(){
		return field;
	}

	private void setAccessible(Method accessor) {
		if (accessor == null && !accessible) {
			this.field.setAccessible(true);
			accessible = true;
		}
	}

	public Class<?> getFieldParent() {
		return field.getDeclaringClass();
	}

	public Class<?> getFieldType() {
		return field.getType();
	}

	public boolean canWrite(Object instance) {
		Class<?> declaringClass;

		if (writeMethod != null) {
			declaringClass = writeMethod.getDeclaringClass();
		} else {
			declaringClass = field.getDeclaringClass();
		}

		return declaringClass.isAssignableFrom(instance.getClass());
	}

	public Object read(Object instance) {
		setAccessible(readMethod);
		try {
			if (readMethod != null) {
				return readMethod.invoke(instance);
			} else {
				return field.get(instance);
			}
		} catch (Throwable e) {
			throw new DataProcessingException("Unable to get value from field " + field.getName() + "' in " + this.beanClass.getName(), e);
		}
	}

	public void write(Object instance, Object value) {
		setAccessible(writeMethod);
		try {
			if (writeMethod != null) {
				writeMethod.invoke(instance, value);
			} else {
				field.set(instance, value);
			}
		} catch (Throwable e) {
			DataProcessingException ex = new DataProcessingException("Unable to set value '" + value + "' for field " + field.getName() + "' in " + this.beanClass.getName(), e);
			ex.setValue(value);
			throw ex;
		}

	}
}
