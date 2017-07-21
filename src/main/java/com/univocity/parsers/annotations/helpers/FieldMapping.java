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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.beans.*;

import java.lang.reflect.*;

import static com.univocity.parsers.annotations.helpers.AnnotationHelper.*;

/**
 * A helper class with information about the location of an field annotated with {@link Parsed} in a record.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class FieldMapping {
	private final Class parentClass;
	private final Field field;
	private int index;
	private String fieldName;
	private final Class<?> beanClass;
	private final Method readMethod;
	private final Method writeMethod;
	private boolean accessible;
	private final boolean primitive;
	private final Object defaultPrimitiveValue;
	private Boolean applyDefault = null;

	/**
	 * Creates the mapping and identifies how it is mapped (by name or by index)
	 *
	 * @param beanClass   the class that contains a the given field.
	 * @param field       a {@link java.lang.reflect.Field} annotated with {@link Parsed}
	 * @param property    the property descriptor of this field, if any. If this bean does not have getters/setters, it will be accessed directly.
	 * @param transformer an optional {@link HeaderTransformer} to modify header names/positions in attributes of {@Nested} classes.
	 */
	public FieldMapping(Class<?> beanClass, Field field, PropertyWrapper property, HeaderTransformer transformer) {
		this.beanClass = beanClass;
		this.field = field;
		this.readMethod = property != null ? property.getReadMethod() : null;
		this.writeMethod = property != null ? property.getWriteMethod() : null;

		Class typeToSet;

		if (field != null) {
			typeToSet = field.getType();
			parentClass = field.getDeclaringClass();
		} else if (writeMethod != null && writeMethod.getParameterTypes().length == 1) {
			typeToSet = writeMethod.getParameterTypes()[0];
			parentClass = writeMethod.getDeclaringClass();
		} else {
			typeToSet = Object.class;
			if (readMethod != null) {
				parentClass = readMethod.getDeclaringClass();
			} else {
				parentClass = beanClass;
			}
		}

		primitive = typeToSet.isPrimitive();
		defaultPrimitiveValue = getDefaultPrimitiveValue(typeToSet);
		determineFieldMapping(transformer);
	}

	private void determineFieldMapping(HeaderTransformer transformer) {
		Parsed parsed = findAnnotation(field, Parsed.class);
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

		//Not a @Nested field
		if (parsed != null && transformer != null) {
			if (index >= 0) {
				index = transformer.transformIndex(field, index);
			} else if (fieldName != null) {
				fieldName = transformer.transformName(field, fieldName);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FieldMapping that = (FieldMapping) o;

		if (index != that.index) {
			return false;
		}
		if (!field.equals(that.field)) {
			return false;
		}
		if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
			return false;
		}
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

	/**
	 * Returns {@code true} if the field is mapped to a column index, otherwise {@code false}
	 *
	 * @return {@code true} if the field is mapped to a column index, otherwise {@code false}
	 */
	public boolean isMappedToIndex() {
		return index >= 0;
	}

	/**
	 * Returns {@code true} if the field is mapped to a column name, otherwise {@code false}
	 *
	 * @return {@code true} if the field is mapped to a column name, otherwise {@code false}
	 */
	public boolean isMappedToField() {
		return index < 0;
	}

	/**
	 * Returns the column index against which this field is mapped.
	 *
	 * @return the column index associated with this field, or -1 if there's no such association.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Defines the column name against which this field is mapped, overriding any current name derived from
	 * annotations or from the attribute name itself.
	 *
	 * @param fieldName the column name associated with this field
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Returns the column name against which this field is mapped.
	 *
	 * @return the column name associated with this field, or {@code null} if there's no such association.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the {@link Field} mapped to a column
	 *
	 * @return the {@link Field} mapped to a column
	 */
	public Field getField() {
		return field;
	}

	private void setAccessible(Method accessor) {
		if (accessor == null && !accessible) {
			this.field.setAccessible(true);
			accessible = true;
		}
	}

	/**
	 * Returns the parent class that contains the mapped field.
	 *
	 * @return the field's parent class
	 */
	public Class<?> getFieldParent() {
		return field.getDeclaringClass();
	}

	/**
	 * Returns the type of the mapped field
	 *
	 * @return the field type.
	 */
	public Class<?> getFieldType() {
		return field.getType();
	}

	/**
	 * Queries whether this field mapping can be applied over a given object instance.
	 *
	 * @param instance the object whose type will be verified in order to identify if it contains the mapped field
	 *
	 * @return {@code true} if the given instance contains the field/accessor method and can use this field mapping to modify its internal state; otherwise {@code false}
	 */
	public boolean canWrite(Object instance) {
		Class<?> targetType;

		if (writeMethod != null && writeMethod.getParameterTypes().length > 0) {
			targetType = writeMethod.getParameterTypes()[0];
		} else {
			targetType = field.getType();
		}

		return targetType.isAssignableFrom(instance.getClass());
	}

	/**
	 * Reads the value accessible by this field mapping from a given object
	 *
	 * @param instance the object whose field, mapped by this field mapping, will be read
	 *
	 * @return the value contained in the given instance's field
	 */
	public Object read(Object instance) {
		return read(instance, false);
	}

	private Object read(Object instance, boolean ignoreErrors) {
		setAccessible(readMethod);
		try {
			if (readMethod != null) {
				return readMethod.invoke(instance);
			} else {
				return field.get(instance);
			}
		} catch (Throwable e) {
			if (!ignoreErrors) {
				throw new DataProcessingException("Unable to get value from field " + toString(), e);
			}
		}
		return null;
	}

	/**
	 * Writes a value to the field of a given object instance, whose field is accessible through this field mapping.
	 *
	 * @param instance the object whose field will be set
	 * @param value    the value to set on the given object's field.
	 */
	public void write(Object instance, Object value) {
		setAccessible(writeMethod);
		try {
			if (value == null && primitive) {
				if (applyDefault == null) {
					Object currentValue = read(instance, true);
					applyDefault = defaultPrimitiveValue.equals(currentValue);
				}
				if (applyDefault == Boolean.TRUE) {
					value = defaultPrimitiveValue;
				} else {
					return;
				}
			}
			if (writeMethod != null) {
				writeMethod.invoke(instance, value);
			} else {
				field.set(instance, value);
			}
		} catch (Throwable e) {
			if (e instanceof DataProcessingException) {
				throw (DataProcessingException) e;
			}
			String valueTypeName = value == null ? null : value.getClass().getName();

			String msg;
			if (valueTypeName != null) {
				msg = "Unable to set value '{value}' of type '" + valueTypeName + "' to field " + toString();
			} else {
				msg = "Unable to set value 'null' to field " + toString();
			}
			DataProcessingException ex = new DataProcessingException(msg, e);
			ex.markAsNonFatal();
			ex.setValue(value);
			throw ex;
		}

	}

	@Override
	public String toString() {
		return '\'' + field.getName() + "' in " + this.parentClass.getName();
	}
}
