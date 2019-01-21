/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class FieldMapping {
	private final Class parentClass;
	private final AnnotatedElement target;
	private int index;
	private NormalizedString fieldName;
	private final Class<?> beanClass;
	private final Method readMethod;
	private final Method writeMethod;
	private boolean accessible;
	private final boolean primitive;
	private final Object defaultPrimitiveValue;
	private Boolean applyDefault = null;
	private Class fieldType;
	private boolean primitiveNumber;

	/**
	 * Creates the mapping and identifies how it is mapped (by name or by index)
	 *
	 * @param beanClass   the class that contains a the given field.
	 * @param target      a {@link java.lang.reflect.Field} or {@link java.lang.reflect.Method} annotated with {@link Parsed}
	 * @param property    the property descriptor of this field, if any. If this bean does not have getters/setters, it will be accessed directly.
	 * @param transformer an optional {@link HeaderTransformer} to modify header names/positions in attributes of {@link Nested} classes.
	 * @param headers     list of headers parsed from the input or manually set with {@link CommonSettings#setHeaders(String...)}
	 */
	public FieldMapping(Class<?> beanClass, AnnotatedElement target, PropertyWrapper property, HeaderTransformer transformer, NormalizedString[] headers) {
		this.beanClass = beanClass;
		this.target = target;
		if (target instanceof Field) {
			this.readMethod = property != null ? property.getReadMethod() : null;
			this.writeMethod = property != null ? property.getWriteMethod() : null;
		} else {
			Method method = (Method) target;
			this.readMethod = method.getReturnType() != Void.class ? method : null;
			this.writeMethod = method.getParameterTypes().length != 0 ? method : null;
		}

		Class typeToSet;

		if (target != null) {
			typeToSet = getType(target);
			parentClass = getDeclaringClass(target);
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
		primitiveNumber = (defaultPrimitiveValue instanceof Number);
		fieldType = typeToSet;
		determineFieldMapping(transformer, headers);
	}

	private void determineFieldMapping(HeaderTransformer transformer, NormalizedString[] headers) {
		Parsed parsed = findAnnotation(target, Parsed.class);
		String name = "";

		if (parsed != null) { //field can be annotated with @Nested only. In this case we get the original field name
			index = AnnotationRegistry.getValue(target, parsed, "index", parsed.index());

			if (index >= 0) {
				fieldName = null;
				if (transformer != null) {
					index = transformer.transformIndex(target, index);
				}
				return;
			}

			String[] fields = AnnotationRegistry.getValue(target, parsed, "field", parsed.field());

			if (fields.length > 1 && headers != null) {
				for (int i = 0; i < headers.length; i++) {
					NormalizedString header = headers[i];
					if (header == null) {
						continue;
					}

					for (int j = 0; j < fields.length; j++) {
						String field = fields[j];
						if (header.equals(field)) {
							name = field;
							break;
						}
					}
				}
			}
			if (name.isEmpty()) {
				name = fields.length == 0 ? "" : fields[0];
			}
		}

		if (name.isEmpty()) {
			name = getName(target);
		}
		fieldName = NormalizedString.valueOf(name);


		//Not a @Nested field
		if (parsed != null && transformer != null) {
			if (index >= 0) {
				index = transformer.transformIndex(target, index);
			} else if (fieldName != null) {
				fieldName = NormalizedString.valueOf(transformer.transformName(target, fieldName.toString()));
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
		if (!target.equals(that.target)) {
			return false;
		}
		if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
			return false;
		}
		return beanClass.equals(that.beanClass);

	}

	@Override
	public int hashCode() {
		int result = target.hashCode();
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
	 * Defines the column index against which this field is mapped, overriding any current position derived from
	 * annotations.
	 *
	 * @param index the column index associated with this field
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Defines the column name against which this field is mapped, overriding any current name derived from
	 * annotations or from the attribute name itself.
	 *
	 * @param fieldName the column name associated with this field
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = NormalizedString.valueOf(fieldName);
	}

	/**
	 * Defines the column name against which this field is mapped, overriding any current name derived from
	 * annotations or from the attribute name itself.
	 *
	 * @param fieldName the column name associated with this field
	 */
	public void setFieldName(NormalizedString fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Returns the column name against which this field is mapped.
	 *
	 * @return the column name associated with this field, or {@code null} if there's no such association.
	 */
	public NormalizedString getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the {@link Field} mapped to a column
	 *
	 * @return the {@link Field} mapped to a column
	 */
	public AnnotatedElement getTarget() {
		return target;
	}

	private void setAccessible() {
		if (!accessible) {
			if (target instanceof Field) {
				final Field field = ((Field) target);
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
			} else if (target instanceof Method) {
				final Method method = (Method) target;
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
			}
			accessible = true;
		}
	}

	/**
	 * Returns the parent class that contains the mapped field.
	 *
	 * @return the field's parent class
	 */
	public Class<?> getFieldParent() {
		return parentClass;
	}

	/**
	 * Returns the type of the mapped field
	 *
	 * @return the field type.
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * Queries whether this field mapping can be applied over a given object instance.
	 *
	 * @param instance the object whose type will be verified in order to identify if it contains the mapped field
	 *
	 * @return {@code true} if the given instance contains the field/accessor method and can use this field mapping to modify its internal state; otherwise {@code false}
	 */
	public boolean canWrite(Object instance) {
		if (!primitive) {
			if (instance == null) {
				return true;
			}
			return fieldType.isAssignableFrom(instance.getClass());
		} else if (instance instanceof Number) {
			return primitiveNumber;
		} else if (instance instanceof Boolean) {
			return fieldType == boolean.class;
		} else if (instance instanceof Character) {
			return fieldType == char.class;
		}
		return false;
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
		setAccessible();
		try {
			if (readMethod != null) {
				return readMethod.invoke(instance);
			} else {
				return ((Field) target).get(instance);
			}
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			if (!ignoreErrors) {
				String msg = "Unable to get value from field: " + toString();
				if (e instanceof DataProcessingException) {
					DataProcessingException ex = (DataProcessingException) e;
					ex.setDetails(msg);
					throw ex;
				}

				throw new DataProcessingException(msg, e);
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
		setAccessible();
		try {
			if (primitive) {
				if (value == null) {
					if (applyDefault == null) {
						Object currentValue = read(instance, true);
						applyDefault = defaultPrimitiveValue.equals(currentValue);
					}
					if (applyDefault == Boolean.TRUE) {
						value = defaultPrimitiveValue;
					} else {
						return;
					}
				} else if (defaultPrimitiveValue.getClass() != value.getClass() && value instanceof Number) {
					Number number = ((Number) value);
					if (fieldType == int.class) {
						value = number.intValue();
					} else if (fieldType == long.class) {
						value = number.longValue();
					} else if (fieldType == double.class) {
						value = number.doubleValue();
					} else if (fieldType == float.class) {
						value = number.floatValue();
					} else if (fieldType == byte.class) {
						value = number.byteValue();
					} else if (fieldType == short.class) {
						value = number.shortValue();
					}
				}
			}
			if (writeMethod != null) {
				writeMethod.invoke(instance, value);
			} else {
				((Field) target).set(instance, value);
			}
		} catch (Throwable e) {
			String valueTypeName = value == null ? null : value.getClass().getName();
			String msg;
			String details = null;
			if (valueTypeName != null) {
				msg = "Unable to set value '{value}' of type '" + valueTypeName + "' to " + toString();
			} else {
				msg = "Unable to set value 'null' to " + toString();
			}

			if (e instanceof InvocationTargetException) {
				e = e.getCause();
				details = msg;
			}

			if (e instanceof DataProcessingException) {
				DataProcessingException ex = (DataProcessingException) e;
				ex.markAsNonFatal();
				ex.setValue(value);
				ex.setDetails(details);
				throw (DataProcessingException) e;
			}

			DataProcessingException ex = new DataProcessingException(msg, e);
			ex.markAsNonFatal();
			ex.setValue(value);
			throw ex;
		}

	}

	@Override
	public String toString() {
		return AnnotationHelper.describeElement(target);
	}
}
