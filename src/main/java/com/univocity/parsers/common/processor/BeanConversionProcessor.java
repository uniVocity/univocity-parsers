/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.processor;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;

import java.beans.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * The base class for {@link RowProcessor} and {@link RowWriterProcessor} implementations that support java beans annotated with the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * @see RowProcessor
 * @see RowWriterProcessor
 *
 * @param <T> the annotated class type.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
abstract class BeanConversionProcessor<T> extends ConversionProcessor {

	private final Class<T> beanClass;
	private final Set<FieldMapping> parsedFields = new HashSet<FieldMapping>();
	private int lastFieldIndexMapped = -1;
	private FieldMapping[] readOrder;
	private boolean initialized = false;
	private final Map<Class<?>, BeanConversionProcessor<?>> nestedInstances;
	private NestedProcessor[] nestedProcessors;
	private NestedProcessor nestedProcessor;
	private NestedProcessor previousProcessor;
	private T lastParsedInstance;
	private final Deque<Object> nestingPath = new ArrayDeque<Object>();

	/**
	 * Initializes the BeanConversionProcessor with the annotated bean class
	 * @param beanType the class annotated with one or more of the annotations provided in {@link com.univocity.parsers.annotations}.
	 */
	public BeanConversionProcessor(Class<T> beanType) {
		this(beanType, new HashMap<Class<?>, BeanConversionProcessor<?>>());
	}

	BeanConversionProcessor(Class<T> beanType, Map<Class<?>, BeanConversionProcessor<?>> nestedInstances) {
		this.beanClass = beanType;
		this.nestedInstances = nestedInstances;
	}

	/**
	 * Identifies and extracts fields annotated with the {@link Parsed} annotation
	 */
	protected final void initialize() {
		if (!initialized) {

			initialized = true;
			lastParsedInstance = null;
			Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
				for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
					String name = property.getName();
					properties.put(name, property);
				}
			} catch (IntrospectionException e) {
				//ignore and proceed to get fields directly
			}

			Set<String> used = new HashSet<String>();
			Class<?> clazz = beanClass;
			List<NestedProcessor> nestedProcessors = new ArrayList<NestedProcessor>();
			do {
				Field[] declared = clazz.getDeclaredFields();
				for (Field field : declared) {
					if (used.contains(field.getName())) {
						continue;
					}
					Parsed annotation = field.getAnnotation(Parsed.class);
					if (annotation != null) {
						FieldMapping mapping = new FieldMapping(beanClass, field, properties.get(field.getName()));
						parsedFields.add(mapping);
						setupConversions(field, mapping);
						used.add(field.getName());
					}

					Nested nested = field.getAnnotation(Nested.class);
					if (nested != null) {
						NestedProcessor nestedProcessor = processNestedBeans(nested, field);
						nestedProcessor.fieldMapping = new FieldMapping(beanClass, field, properties.get(field.getName()));
						nestedProcessors.add(nestedProcessor);
						used.add(field.getName());
					}
				}
				clazz = clazz.getSuperclass();
			} while (clazz != null && clazz != Object.class);

			readOrder = null;
			lastFieldIndexMapped = -1;

			setupNestedProcessors(nestedProcessors);

			validateMappings();
		}
	}

	private void validateMappings() {
		Map<String, FieldMapping> mappedNames = new HashMap<String, FieldMapping>();
		Map<Integer, FieldMapping> mappedIndexes = new HashMap<Integer, FieldMapping>();

		Set<FieldMapping> duplicateNames = new HashSet<FieldMapping>();
		Set<FieldMapping> duplicateIndexes = new HashSet<FieldMapping>();

		for (FieldMapping mapping : parsedFields) {
			String name = mapping.getFieldName();
			int index = mapping.getIndex();

			if (index != -1) {
				if (mappedIndexes.containsKey(index)) {
					duplicateIndexes.add(mapping);
					duplicateIndexes.add(mappedIndexes.get(index));
				} else {
					mappedIndexes.put(index, mapping);
				}
			} else {
				if (mappedNames.containsKey(name)) {
					duplicateNames.add(mapping);
					duplicateNames.add(mappedNames.get(name));
				} else {
					mappedNames.put(name, mapping);
				}
			}
		}

		if (duplicateIndexes.size() > 0 || duplicateNames.size() > 0) {
			StringBuilder msg = new StringBuilder("Conflicting field mappings defined in annotated class: " + this.getBeanClass().getName());
			for (FieldMapping mapping : duplicateIndexes) {
				msg.append("\n\tIndex: '" + mapping.getIndex() + "' of  " + describeField(mapping.getField()));
			}
			for (FieldMapping mapping : duplicateNames) {
				msg.append("\n\tName: '" + mapping.getFieldName() + "' of " + describeField(mapping.getField()));
			}
			throw new DataProcessingException(msg.toString());
		}
	}

	private static String describeField(Field field) {
		return "field '" + field.getName() + "' (" + field.getType().getName() + ")";
	}

	private NestedProcessor processNestedBeans(Nested nested, Field field) {
		Class<?> instanceToCreate;
		Class<?> componentType = nested.componentType();
		boolean isCollection = false;
		boolean isMap = false;
		NestedProcessor out = new NestedProcessor(nestingPath);

		if (java.util.Collection.class.isAssignableFrom(field.getType())) {
			if (java.util.Set.class.isAssignableFrom(field.getType())) {
				instanceToCreate = java.util.HashSet.class;
				isCollection = true;
			} else {
				instanceToCreate = java.util.ArrayList.class;
				isCollection = true;
			}
		} else if (field.getType().isArray()) {
			out.isArray = true;
			instanceToCreate = java.util.ArrayList.class;

			if (componentType == Object.class) {
				componentType = field.getType().getComponentType();
			}

			isCollection = true;
		} else if (java.util.Map.class.isAssignableFrom(field.getType())) {
			instanceToCreate = java.util.HashMap.class;
			isCollection = true;
			isMap = true;
		} else {
			instanceToCreate = field.getType();
		}

		if (nested.instanceOf() != Object.class) {
			instanceToCreate = nested.instanceOf();
		}

		int keyIndex = -1;
		String keyField = nested.keyField().trim();

		if (isMap) {
			out.mapType = instanceToCreate;
			if (!keyField.isEmpty()) {
				keyIndex = getFieldInHeaders("keyField", keyField, field, componentType);
			}

			if (keyIndex == -1) {
				keyIndex = nested.keyIndex();
				if (keyIndex < 0) {
					throw new IllegalStateException("Index of column used to provide keys for @Nested map in field " + field.getName() + " of class " + field.getDeclaringClass().getName() + " cannot be negative.");
				}
			}
		} else if (isCollection) {
			out.collectionType = instanceToCreate;
		}

		ensureClassIsNotAnInterface("instanceOf", field, instanceToCreate);

		if (componentType != Object.class) {
			if (isCollection) {
				ensureClassIsNotAnInterface("componentType", field, componentType);
			} else {
				throw new IllegalStateException("Invalid usage of @Nested annotation on field " + field.getName() + " of class " + field.getDeclaringClass().getName()
					+ ": The 'componentType' parameter can only be used for collections, maps and arrays.");
			}
		}

		String identityValue = nested.identityValue().trim();
		if (identityValue.isEmpty()) {
			if (componentType != Object.class) {
				identityValue = componentType.getSimpleName();
			} else if (instanceToCreate != Object.class) {
				identityValue = instanceToCreate.getSimpleName();
			} else {
				identityValue = field.getType().getSimpleName();
			}
		}
		out.identityValue = identityValue;

		Class<?> beanClass = isCollection ? componentType : instanceToCreate;

		int identityIndex = -1;
		String identityField = nested.identityField().trim();

		if (!identityField.isEmpty()) {
			identityIndex = getFieldInHeaders("identityField", identityField, field, beanClass);
		}

		if (identityIndex == -1) {
			identityIndex = nested.identityIndex();
			if (identityIndex < 0) {
				throw new IllegalStateException("Index of identity value of @Nested field " + field.getName() + " of class " + field.getDeclaringClass().getName() + " cannot be negative.");
			}
		}
		out.identityIndex = identityIndex;

		if (beanClass == this.beanClass) {
			out.processor = this;
		} else {
			if (nestedInstances.containsKey(beanClass)) {
				out.processor = nestedInstances.get(beanClass);
			} else {
				out.processor = newNestedInstance(beanClass, nestedInstances);
				nestedInstances.put(beanClass, out.processor);
				out.processor.initialize();
			}
		}
		return out;
	}

	private static int getFieldInHeaders(String description, String fieldName, Field field, Class<?> beanClass) {
		Headers headers = beanClass.getAnnotation(Headers.class);
		if (headers != null) {
			int index = ArgumentUtils.indexOf(headers.sequence(), fieldName);
			if (index == -1) {
				throw new IllegalStateException("Invalid usage of @Nested annotation on field " + field.getName() + " of class " + field.getDeclaringClass().getName()
					+ ": The '" + description + "' parameter refers to header '" + fieldName + "' which does not exist in headers of '" + beanClass.getName() + "': " + Arrays.toString(headers.sequence()));
			}
			return index;
		} else {
			throw new IllegalStateException("Invalid usage of @Nested annotation on field " + field.getName() + " of class " + field.getDeclaringClass().getName()
				+ ": The '" + description + "' parameter refers to header '" + fieldName + "' which does not exist in class '" + beanClass.getName() + "'. Please add a @Headers annotation with this header to class '"
				+ beanClass.getName() + '\'');
		}
	}

	private static void ensureClassIsNotAnInterface(String description, Field field, Class<?> clazz) {
		if (clazz.isInterface()) {
			throw new IllegalStateException("Invalid usage of @Nested annotation on field " + field.getName() + " of class " + field.getDeclaringClass().getName() + ": Cannot create instances of interface " + clazz.getName()
				+ ". Please provide a class name in the '" + description + "' parameter.");
		}
	}

	/**
	 * Goes through each field annotated with {@link Parsed} and extracts the sequence of {@link Conversion} elements associated with each one.
	 * @param field the field annotated with {@link Parsed} that must be associated with one or more {@link Conversion} objects
	 * @param mapping a helper class to store information how the field is mapped to a parsed record.
	 */
	@SuppressWarnings("rawtypes")
	private void setupConversions(Field field, FieldMapping mapping) {
		Annotation[] annotations = field.getAnnotations();

		Conversion lastConversion = null;
		for (Annotation annotation : annotations) {
			try {
				Conversion conversion = AnnotationHelper.getConversion(field, annotation);
				if (conversion != null) {
					addConversion(conversion, mapping);
					lastConversion = conversion;

				}
			} catch (Throwable ex) {
				String path = annotation.annotationType().getSimpleName() + "' of field '" + field.getName() + "' in " + this.beanClass.getName();
				throw new DataProcessingException("Error processing annotation '" + path + ". " + ex.getMessage(), ex);
			}
		}

		if (field.getAnnotation(Parsed.class).applyDefaultConversion()) {
			Conversion defaultConversion = AnnotationHelper.getDefaultConversion(field);
			if (applyDefaultConversion(lastConversion, defaultConversion)) {
				addConversion(defaultConversion, mapping);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean applyDefaultConversion(Conversion lastConversionApplied, Conversion defaultConversion) {
		if (defaultConversion == null) {
			return false;
		}
		if (lastConversionApplied == null) {
			return true;
		}

		if (lastConversionApplied.getClass() == defaultConversion.getClass()) {
			// no need to add the default conversion as it was manually specified by the user with his settings
			return false;
		}

		Method execute = getConversionMethod(lastConversionApplied, "execute");
		Method revert = getConversionMethod(lastConversionApplied, "revert");

		Method defaultExecute = getConversionMethod(defaultConversion, "execute");
		Method defaultRevert = getConversionMethod(defaultConversion, "revert");

		return !(execute.getReturnType() == defaultExecute.getReturnType() && revert.getReturnType() == defaultRevert.getReturnType());

	}

	@SuppressWarnings("rawtypes")
	private Method getConversionMethod(Conversion conversion, String methodName) {
		Method targetMethod = null;
		for (Method method : conversion.getClass().getMethods()) {
			if (method.getName().equals(methodName) && !method.isSynthetic() && !method.isBridge() && ((method.getModifiers() & Modifier.PUBLIC) == 1) && method.getParameterTypes().length == 1 && method.getReturnType() != Void.class) {
				if (targetMethod != null) {
					throw new DataProcessingException("Unable to convert values for class '" + beanClass + "'. Multiple '" + methodName + "' methods defined in conversion " + conversion.getClass() + '.');
				}
				targetMethod = method;
			}
		}
		if (targetMethod != null) {
			return targetMethod;
		}
		//should never happen
		throw new DataProcessingException("Unable to convert values for class '" + beanClass + "'. Cannot find method '" + methodName + "' in conversion " + conversion.getClass() + '.');
	}

	/**
	 * Associates a conversion to a field of the java bean class.
	 * @param conversion The conversion object that must be executed against the given field
	 * @param mapping the helper object that contains information about how a field is mapped.
	 */
	@SuppressWarnings("rawtypes")
	private void addConversion(Conversion conversion, FieldMapping mapping) {
		if (conversion == null) {
			return;
		}

		if (mapping.isMappedToIndex()) {
			this.convertIndexes(conversion).add(mapping.getIndex());
		} else {
			this.convertFields(conversion).add(mapping.getFieldName());
		}
	}

	/**
	 * Goes through a list of objects and associates each value to a particular field of a java bean instance
	 * @param instance the java bean instance that is going to have its properties set
	 * @param row the values to associate with each field of the javabean.
	 * @param context information about the current parsing process.
	 */
	private void mapValuesToFields(T instance, Object[] row, ParsingContext context) {
		if (row.length > lastFieldIndexMapped) {
			this.lastFieldIndexMapped = row.length;
			mapFieldIndexes(context, row, context.headers(), context.extractedFieldIndexes(), context.columnsReordered());
		}

		int last = row.length < readOrder.length ? row.length : readOrder.length;
		for (int i = 0; i < last; i++) {
			FieldMapping field = readOrder[i];
			if (field != null) {
				Object value = row[i];
				field.write(instance, value);
			}
		}
	}

	/**
	 * Identifies which fields are associated with which columns in a row.
	 *
	 * @param row A row with values for the given java bean.
	 * @param headers The names of all fields of the record (including any header that is not mapped to the java bean). May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexes The indexes of the headers or row that are actually being used. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @param columnsReordered Indicates the indexes provided were reordered and do not match the original sequence of headers.
	 */

	private void mapFieldIndexes(ParsingContext context, Object[] row, String[] headers, int[] indexes, boolean columnsReordered) {
		if (headers == null) {
			headers = ArgumentUtils.EMPTY_STRING_ARRAY;
		}
		int biggestIndex = headers.length > row.length ? headers.length : row.length;
		for (FieldMapping mapping : parsedFields) {
			int index = mapping.getIndex();
			if (biggestIndex < index) {
				biggestIndex = index;
			}
		}

		FieldMapping[] fieldOrder = new FieldMapping[biggestIndex];

		TreeSet<String> fieldsNotFound = new TreeSet<String>();

		for (FieldMapping mapping : parsedFields) {
			if (mapping.isMappedToField()) {
				int index = ArgumentUtils.indexOf(headers, mapping.getFieldName());
				if (index == -1) {
					fieldsNotFound.add(mapping.getFieldName());
				}
				fieldOrder[index] = mapping;
			} else {
				if (mapping.getIndex() < fieldOrder.length) {
					fieldOrder[mapping.getIndex()] = mapping;
				}
			}
		}

		if (!fieldsNotFound.isEmpty()) {
			if (headers.length == 0) {
				throw new DataProcessingException("Could not find fields " + fieldsNotFound.toString() + " in input. Please enable header extraction in the parser settings in order to match field names.");
			}
			throw new DataProcessingException("Could not find fields " + fieldsNotFound.toString() + "' in input. Names found: " + Arrays.toString(headers));
		}

		if (indexes != null) {
			// sets fields not read from CSV to null.
			for (int i = 0; i < fieldOrder.length; i++) {
				boolean isIndexUsed = false;
				for (int j = 0; j < indexes.length; j++) {
					if (indexes[j] == i) {
						isIndexUsed = true;
						break;
					}
				}
				if (!isIndexUsed) {
					fieldOrder[i] = null;
				}
			}

			// reorders the fields so they are positioned in the same order as in the incoming row[]
			if (columnsReordered) {
				FieldMapping[] newFieldOrder = new FieldMapping[indexes.length];

				for (int i = 0; i < indexes.length; i++) {
					for (int j = 0; j < fieldOrder.length; j++) {
						int index = indexes[i];
						FieldMapping field = fieldOrder[index];
						newFieldOrder[i] = field;
					}
				}

				fieldOrder = newFieldOrder;
			}
		}

		this.readOrder = fieldOrder;

	}

	/**
	 * Converts a record with values extracted from the parser into a java bean instance.
	 * @param row The values extracted from the parser
	 * @param context The current state of the parsing process
	 * @return an instance of the java bean type defined in this class constructor.
	 */
	public final T createBean(String[] row, ParsingContext context) {
		if (nestedProcessors != null) {
			for (int i = 0; i < nestedProcessors.length; i++) {
				nestedProcessor = nestedProcessors[i];
				if (nestedProcessor.identityIndex < row.length) {
					if (nestedProcessor.identityValue.equals(row[nestedProcessor.identityIndex])) {
						NestedProcessor[] originalProcessors = nestedProcessors;
						try {
							if (previousProcessor == nestedProcessor) {
								nestedProcessors = null;
								previousProcessor = null;
							} else {
								previousProcessor = nestedProcessor;
							}
							Object bean = nestedProcessor.processor.createBean(row, context);
							if (bean != null) { // bean can be null as the nested processor can submit the row to another nested processor :)
								nestedBeanProcessed(bean, context);
							}
						} finally {
							nestedProcessors = originalProcessors;
						}

						return null;
					}
				}
			}
		}

		Object[] convertedRow = super.applyConversions(row, context);
		if (convertedRow == null) {
			return null;
		}

		T instance;
		try {
			instance = beanClass.newInstance();
		} catch (Throwable e) {
			throw new DataProcessingException("Unable to instantiate class '" + beanClass.getName() + '\'', row, e);
		}
		mapValuesToFields(instance, convertedRow, context);

		if (nestedProcessors == null) {
			return instance;
		}

		T out = lastParsedInstance;

		lastParsedInstance = instance;

		if (out != null) {
			nestingPath.clear();
		}

		return out;
	}

	/**
	 * Iterates over all fields in the java bean instance and extracts its values.
	 * @param instance the java bean instance to be read
	 * @param row object array that will receive the values extracted from java bean
	 * @param headers The names of all fields of the record (including any header that is not mapped to the java bean). May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexes The indexes of the headers or row that are actually being used. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @param columnsReordered Indicates the indexes provided were reordered and do not match the original sequence of headers.
	 */
	private void mapFieldsToValues(T instance, Object[] row, String[] headers, int[] indexes, boolean columnsReordered) {
		if (row.length > this.lastFieldIndexMapped) {
			mapFieldIndexes(null, row, headers, indexes, columnsReordered);
		}

		int last = row.length < readOrder.length ? row.length : readOrder.length;
		for (int i = 0; i < last; i++) {
			FieldMapping field = readOrder[i];
			if (field != null) {
				row[i] = field.read(instance);
			}
		}
	}

	/**
	 * Converts a java bean instance into a sequence of values for writing.
	 *
	 * @param bean an instance of the type defined in this class constructor.
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return a row of objects containing the values extracted from the java bean
	 */
	public final Object[] reverseConversions(T bean, String[] headers, int[] indexesToWrite) {
		if (bean == null) {
			return null;
		}
		Object[] row;
		if (indexesToWrite != null) {
			row = new Object[indexesToWrite.length];
		} else if (headers != null) {
			row = new Object[headers.length];
		} else {
			throw new TextWritingException("Cannot process bean of type " + bean.getClass().getName() + ". No headers defined nor selection of indexes to write.", -1, new Object[]{bean});
		}

		try {
			mapFieldsToValues(bean, row, headers, indexesToWrite, false);
		} catch (DataProcessingException ex) {
			ex.markAsNonFatal();
			if (!beanClass.isAssignableFrom(bean.getClass())) {
				handleConversionError(ex, new Object[]{bean}, -1);
			} else {
				handleConversionError(ex, row, -1);
			}
			return null;
		}

		if (super.reverseConversions(true, row, headers, indexesToWrite)) {
			return row;
		}

		return null;
	}

	/**
	 * Returns the class of the annotated java bean instances that will be manipulated by this processor.
	 * @return the class of the annotated java bean instances that will be manipulated by this processor.
	 */
	public Class<T> getBeanClass() {
		return beanClass;
	}

	abstract BeanConversionProcessor<Object> newNestedInstance(Class<?> beanClass, Map<Class<?>, BeanConversionProcessor<?>> nestedInstances);

	protected void nestedBeanProcessed(Object bean, ParsingContext context) {
		if (bean == null) {
			return;
		}
		if (lastParsedInstance == null) {
			DataProcessingException ex = new DataProcessingException("Unable to process nested bean in type " + bean.getClass().getName() + '(' + bean.toString() + "). No parent bean of type " + getBeanClass().getName()
				+ " has been parsed from the input.");
			ex.markAsNonFatal();
			throw ex;
		}

		nestedProcessor.nest(lastParsedInstance, bean);
	}

	private void setupNestedProcessors(List<NestedProcessor> nestedProcessorList) {
		if (nestedProcessorList.isEmpty()) {
			nestedProcessors = null;
			return;
		}
		nestedProcessors = new NestedProcessor[nestedProcessorList.size()];
		int i = 0;
		for (NestedProcessor nested : nestedProcessorList) {
			nestedProcessors[i++] = nested;
		}
	}

	protected T getLastParsedInstance() {
		if (lastParsedInstance == null) {
			return null;
		} else {
			if (nestedProcessor != null) {
				nestedProcessor.nest(null, null);
			}
		}
		return lastParsedInstance;
	}

	static class NestedProcessor { //TODO: refactor this and split in more than one class?
		BeanConversionProcessor<?> processor;
		String identityValue;
		int identityIndex;
		boolean isArray = false;
		Class<?> collectionType;
		Class<?> mapType;
		int keyIndex;
		FieldMapping fieldMapping;
		boolean initialized = false;
		Collection<Object> tempCollection;
		Map<Object, Object> tempMap;

		Object previousParent = null;
		final Deque<Object> nestingPath;

		public NestedProcessor(Deque<Object> nestingPath) {
			this.nestingPath = nestingPath;
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		public void nest(Object parent, Object child) {
			if (previousParent == null || previousParent != parent) {
				initialized = false;
				if (isArray && previousParent != null) {
					Object arrayInstance = Array.newInstance(fieldMapping.getFieldType().getComponentType(), tempCollection.size());
					int i = 0;
					for (Object e : tempCollection) {
						Array.set(arrayInstance, i++, e);
					}
					fieldMapping.write(previousParent, arrayInstance);
				}
			}

			previousParent = parent;

			if (parent == null) {
				// if nesting arrays, we will end up here at the end of the process.
				// The code above will create the array and set it to the mapped field of the previousParent instance.
				// We just need to set the array and bail out as there's nothing else to process.
				return;
			}

			if (!initialized && fieldMapping.read(parent) == null) {
				try {
					if (collectionType != null) {
						tempCollection = (Collection) collectionType.newInstance();
						if (!isArray) {
							fieldMapping.write(parent, tempCollection);
						}
					} else if (mapType != null) {
						tempMap = (Map) mapType.newInstance();
						fieldMapping.write(parent, tempMap);
					}
				} catch (Exception ex) {
					throw new DataProcessingException("Unable to initialize field '" + fieldMapping.getFieldName() + "' of object of type " + parent.getClass().getName() + " (" + parent + ')');
				}
				initialized = true;
			}
			if (initialized) {
				if (collectionType != null || isArray) {
					tempCollection.add(child);
				} else if (mapType != null) {
					tempMap.put(child, child); //TODO: determine what the key will be
				} else {
					applyNestedChild(false, parent, child);
				}
			}
		}

		private void applyNestedChild(boolean popped, Object parent, Object child) {
			Object candidateParent = nestingPath.peek();
			if (!nestingPath.isEmpty() && fieldMapping.canWrite(candidateParent)) {
				fieldMapping.write(candidateParent, child);
				nestingPath.push(child);
				return;
			} else if (!nestingPath.isEmpty()) {
				if (popped) {
					nestingPath.clear();
				} else {
					nestingPath.pop();
					applyNestedChild(true, parent, child);
					return;
				}
			}

			fieldMapping.write(parent, child);
			nestingPath.push(child);
		}
	}

}
