/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.beans.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * The base class for {@link Processor} and {@link RowWriterProcessor} implementations that support java beans annotated with the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * @param <T> the annotated class type.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Processor
 * @see RowWriterProcessor
 */
public class BeanConversionProcessor<T> extends DefaultConversionProcessor {

	final Class<T> beanClass;
	protected final Set<FieldMapping> parsedFields = new LinkedHashSet<FieldMapping>();
	private int lastFieldIndexMapped = -1;
	private FieldMapping[] readOrder;
	private FieldMapping[] missing;
	private Object[] valuesForMissing;
	protected boolean initialized = false;
	boolean strictHeaderValidationEnabled = false;
	private String[] syntheticHeaders = null;
	private Object[] row;
	private Map<FieldMapping, BeanConversionProcessor<?>> nestedAttributes = null;
	protected final HeaderTransformer transformer;
	protected final MethodFilter methodFilter;


	/**
	 * Initializes the BeanConversionProcessor with the annotated bean class. If any method of the given class has annotations,
	 * only the setter methods will be used (getters will be ignored), making this processor useful mostly for parsing into
	 * instances of the given class.
	 *
	 * @param beanType the class annotated with one or more of the annotations provided in {@link com.univocity.parsers.annotations}.
	 *
	 * @deprecated Use the {@link #BeanConversionProcessor(Class, MethodFilter)} constructor instead.
	 */
	@Deprecated
	public BeanConversionProcessor(Class<T> beanType) {
		this(beanType, null, MethodFilter.ONLY_SETTERS);
	}

	/**
	 * Initializes the BeanConversionProcessor with the annotated bean class
	 *
	 * @param beanType     the class annotated with one or more of the annotations provided in {@link com.univocity.parsers.annotations}.
	 * @param methodFilter filter to apply over annotated methods when the processor is reading data from beans (to write values to an output)
	 *                     or writing values into beans (when parsing). It is used to choose either a "get" or a "set"
	 *                     method annotated with {@link Parsed}, when both methods target the same field.
	 */
	public BeanConversionProcessor(Class<T> beanType, MethodFilter methodFilter) {
		this(beanType, null, methodFilter);
	}

	BeanConversionProcessor(Class<T> beanType, HeaderTransformer transformer, MethodFilter methodFilter) {
		this.beanClass = beanType;
		this.transformer = transformer;
		this.methodFilter = methodFilter;
	}

	/**
	 * Returns a flag indicating whether all headers declared in the annotated class must be present in the input.
	 * If enabled, an exception will be thrown in case the input data does not contain all headers required.
	 *
	 * @return flag indicating whether strict validation of headers is enabled.
	 */
	public boolean isStrictHeaderValidationEnabled() {
		return strictHeaderValidationEnabled;
	}

	/**
	 * Identifies and extracts fields annotated with the {@link Parsed} annotation
	 */
	public final void initialize() {
		initialize(null);
	}

	/**
	 * Identifies and extracts fields annotated with the {@link Parsed} annotation
	 *
	 * @param headers headers parsed from the input.
	 */
	protected final void initialize(String[] headers) {
		if (!initialized) {
			initialized = true;

			Map<Field, PropertyWrapper> allFields = AnnotationHelper.getAllFields(beanClass);
			for (Map.Entry<Field, PropertyWrapper> e : allFields.entrySet()) {
				Field field = e.getKey();
				PropertyWrapper property = e.getValue();
				processField(field, property, headers);
			}

			for (Method method : AnnotationHelper.getAnnotatedMethods(beanClass, methodFilter)) {
				processField(method, null, headers);
			}

			readOrder = null;
			lastFieldIndexMapped = -1;

			validateMappings();
		}
	}

	/**
	 * Defines whether all headers declared in the annotated class must be present in the input.
	 * If enabled, an exception will be thrown in case the input data does not contain all headers required.
	 *
	 * @param strictHeaderValidationEnabled flag indicating whether strict validation of headers is enabled.
	 */
	public void setStrictHeaderValidationEnabled(boolean strictHeaderValidationEnabled) {
		this.strictHeaderValidationEnabled = strictHeaderValidationEnabled;
	}

	void processField(AnnotatedElement element, PropertyWrapper propertyDescriptor, String[] headers) {
		Parsed annotation = AnnotationHelper.findAnnotation(element, Parsed.class);
		if (annotation != null) {
			FieldMapping mapping = new FieldMapping(beanClass, element, propertyDescriptor, transformer, headers);
			if (processField(mapping)) {
				parsedFields.add(mapping);
				setupConversions(element, mapping);
			}
		}

		Nested nested = AnnotationHelper.findAnnotation(element, Nested.class);
		if (nested != null) {
			Class nestedType = AnnotationRegistry.getValue(element, nested, "type", nested.type());
			if (nestedType == Object.class) {
				nestedType = AnnotationHelper.getType(element);
			}

			HeaderTransformer transformer;

			Class<? extends HeaderTransformer> transformerType = AnnotationRegistry.getValue(element, nested, "headerTransformer", nested.headerTransformer());
			if (transformerType != HeaderTransformer.class) {
				String[] args = AnnotationRegistry.getValue(element, nested, "args", nested.args());
				transformer = AnnotationHelper.newInstance(HeaderTransformer.class, transformerType, args);
			} else {
				transformer = null;
			}

			FieldMapping mapping = new FieldMapping(nestedType, element, propertyDescriptor, null, headers);
			BeanConversionProcessor<?> processor = createNestedProcessor(nested, nestedType, mapping, transformer);
			processor.initialize(headers);
			getNestedAttributes().put(mapping, processor);
		}
	}

	Map<FieldMapping, BeanConversionProcessor<?>> getNestedAttributes() {
		if (nestedAttributes == null) {
			nestedAttributes = new LinkedHashMap<FieldMapping, BeanConversionProcessor<?>>();
		}
		return nestedAttributes;
	}

	BeanConversionProcessor<?> createNestedProcessor(Annotation annotation, Class nestedType, FieldMapping fieldMapping, HeaderTransformer transformer) {
		return new BeanConversionProcessor<Object>(nestedType, transformer, methodFilter);
	}

	/**
	 * Determines whether or not an annotated field should be processed.
	 * Can be overridden by subclasses for fine grained control.
	 *
	 * @param field the field to be processed
	 *
	 * @return {@code true} if the given field should be processed, otherwise {@code false}.
	 */
	protected boolean processField(FieldMapping field) {
		return true;
	}

	void validateMappings() {
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
				msg.append("\n\tIndex: '").append(mapping.getIndex()).append("' of  ").append(describeField(mapping.getTarget()));
			}
			for (FieldMapping mapping : duplicateNames) {
				msg.append("\n\tName: '").append(mapping.getFieldName()).append("' of ").append(describeField(mapping.getTarget()));
			}
			throw new DataProcessingException(msg.toString());
		}
	}

	static String describeField(AnnotatedElement target) {
		if (target instanceof Method) {
			return "method: " + target;
		}
		return "field '" + AnnotationHelper.getName(target) + "' (" + AnnotationHelper.getType(target).getName() + ')';
	}

	/**
	 * Goes through each field and method annotated with {@link Parsed} and extracts the sequence of {@link Conversion} elements associated with each one.
	 *
	 * @param target  the field and method annotated with {@link Parsed} that must be associated with one or more {@link Conversion} objects
	 * @param mapping a helper class to store information how the field or method is mapped to a parsed record.
	 */
	@SuppressWarnings("rawtypes")
	private void setupConversions(AnnotatedElement target, FieldMapping mapping) {
		List<Annotation> annotations = AnnotationHelper.findAllAnnotationsInPackage(target, Parsed.class.getPackage());

		Conversion lastConversion = null;
		for (Annotation annotation : annotations) {
			try {
				Conversion conversion = AnnotationHelper.getConversion(target, annotation);
				if (conversion != null) {
					addConversion(conversion, mapping);
					lastConversion = conversion;

				}
			} catch (Throwable ex) {
				String path = annotation.annotationType().getSimpleName() + "' of field " + mapping;
				throw new DataProcessingException("Error processing annotation '" + path + ". " + ex.getMessage(), ex);
			}
		}

		Parsed parsed = AnnotationHelper.findAnnotation(target, Parsed.class);
		boolean applyDefaultConversion = AnnotationRegistry.getValue(target, parsed, "applyDefaultConversion", parsed.applyDefaultConversion());

		if (applyDefaultConversion) {
			Conversion defaultConversion = AnnotationHelper.getDefaultConversion(target);
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
			if (method.getName().equals(methodName) && !method.isSynthetic() && !method.isBridge() && ((method.getModifiers() & Modifier.PUBLIC) == 1) && method.getParameterTypes().length == 1 && method.getReturnType() != void.class) {
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
	 *
	 * @param conversion The conversion object that must be executed against the given field
	 * @param mapping    the helper object that contains information about how a field is mapped.
	 */
	@SuppressWarnings("rawtypes")
	protected void addConversion(Conversion conversion, FieldMapping mapping) {
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
	 *
	 * @param instance the java bean instance that is going to have its properties set
	 * @param row      the values to associate with each field of the javabean.
	 * @param context  information about the current parsing process.
	 */
	void mapValuesToFields(T instance, Object[] row, Context context) {
		if (row.length > lastFieldIndexMapped) {
			this.lastFieldIndexMapped = row.length;
			mapFieldIndexes(context, row, context.headers(), context.extractedFieldIndexes(), context.columnsReordered());
		}

		int last = row.length < readOrder.length ? row.length : readOrder.length;
		int i = 0;
		for (; i < last; i++) {
			FieldMapping field = readOrder[i];
			if (field != null) {
				Object value = row[i];
				field.write(instance, value);
			}
		}

		if (conversions != null && row.length < readOrder.length) {
			i = last;
			for (; i < readOrder.length; i++) {
				FieldMapping field = readOrder[i];
				if (field != null) {
					Object value = conversions.applyConversions(i, null, null);
					field.write(instance, value);
				}
			}
		}

		if (missing != null) {
			for (i = 0; i < missing.length; i++) {
				Object value = valuesForMissing[i];
				if (value != null) {
					FieldMapping field = missing[i];
					field.write(instance, value);
				}
			}
		}

	}

	/**
	 * Identifies which fields are associated with which columns in a row.
	 *
	 * @param row              A row with values for the given java bean.
	 * @param headers          The names of all fields of the record (including any header that is not mapped to the java bean). May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexes          The indexes of the headers or row that are actually being used. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @param columnsReordered Indicates the indexes provided were reordered and do not match the original sequence of headers.
	 */

	private void mapFieldIndexes(Context context, Object[] row, String[] headers, int[] indexes, boolean columnsReordered) {
		if (headers == null) {
			headers = ArgumentUtils.EMPTY_STRING_ARRAY;
		}

		boolean boundToIndex = false;

		int last = headers.length > row.length ? headers.length : row.length;
		for (FieldMapping mapping : parsedFields) {
			int index = mapping.getIndex();
			if (last <= index) {
				last = index;
				boundToIndex = true;
			}
		}
		if (boundToIndex) {
			last++;
		}

		FieldMapping[] fieldOrder = new FieldMapping[last];
		TreeSet<String> fieldsNotFound = new TreeSet<String>();

		for (FieldMapping mapping : parsedFields) {
			if (mapping.isMappedToField()) {
				int[] positions = ArgumentUtils.indexesOf(headers, mapping.getFieldName());
				if (positions.length == 0) {
					fieldsNotFound.add(mapping.getFieldName());
					continue;
				}
				for (int i = 0; i < positions.length; i++) {
					fieldOrder[positions[i]] = mapping;
				}
			} else if (mapping.getIndex() < fieldOrder.length) {
				fieldOrder[mapping.getIndex()] = mapping;
			}
		}

		if (context != null && !fieldsNotFound.isEmpty()) { //Trigger this validation only when reading, not writing.
			if (headers.length == 0) {
				throw new DataProcessingException("Could not find fields " + fieldsNotFound.toString() + " in input. Please enable header extraction in the parser settings in order to match field names.");
			}
			if (strictHeaderValidationEnabled) {
				DataProcessingException exception = new DataProcessingException("Could not find fields " + fieldsNotFound.toString() + "' in input. Names found: {headers}");
				exception.setValue("headers", Arrays.toString(headers));
				throw exception;
			}
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
						if (index != -1) {
							FieldMapping field = fieldOrder[index];
							newFieldOrder[i] = field;
						}
					}
				}

				fieldOrder = newFieldOrder;
			}
		}

		readOrder = fieldOrder;
		initializeValuesForMissing();

	}

	private void initializeValuesForMissing() {
		if (readOrder.length < parsedFields.size()) {
			Set<FieldMapping> unmapped = new LinkedHashSet<FieldMapping>(parsedFields);
			unmapped.removeAll(Arrays.asList(readOrder));
			missing = unmapped.toArray(new FieldMapping[0]);
			String[] headers = new String[missing.length];
			BeanConversionProcessor tmp = new BeanConversionProcessor(getBeanClass(), methodFilter) {
				protected void addConversion(Conversion conversion, FieldMapping mapping) {
					if (conversion == null) {
						return;
					}
					convertFields(conversion).add(mapping.getFieldName());
				}
			};

			for (int i = 0; i < missing.length; i++) {
				FieldMapping mapping = missing[i];
				if (processField(mapping)) {
					tmp.setupConversions(mapping.getTarget(), mapping);
				}
				headers[i] = mapping.getFieldName();
			}
			tmp.initializeConversions(headers, null);
			valuesForMissing = tmp.applyConversions(new String[missing.length], null);
		} else {
			missing = null;
			valuesForMissing = null;
		}
	}

	/**
	 * Converts a record with values extracted from the parser into a java bean instance.
	 *
	 * @param row     The values extracted from the parser
	 * @param context The current state of the parsing process
	 *
	 * @return an instance of the java bean type defined in this class constructor.
	 */
	public T createBean(String[] row, Context context) {
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

		if (nestedAttributes != null) {
			processNestedAttributes(row, instance, context);
		}

		return instance;
	}

	void processNestedAttributes(String[] row, Object instance, Context context) {
		for (Map.Entry<FieldMapping, BeanConversionProcessor<?>> e : nestedAttributes.entrySet()) {
			Object nested = e.getValue().createBean(row, context);
			if (nested != null) {
				e.getKey().write(instance, nested);
			}
		}
	}

	/**
	 * Iterates over all fields in the java bean instance and extracts its values.
	 *
	 * @param instance         the java bean instance to be read
	 * @param row              object array that will receive the values extracted from java bean
	 * @param headers          The names of all fields of the record (including any header that is not mapped to the java bean). May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexes          The indexes of the headers or row that are actually being used. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
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
				try {
					row[i] = field.read(instance);
				} catch (Throwable e) {
					if (!beanClass.isAssignableFrom(instance.getClass())) {
						handleConversionError(e, new Object[]{instance}, -1);
						throw toDataProcessingException(e, row, i);
					} else if (!handleConversionError(e, row, i)) {
						throw toDataProcessingException(e, row, i);
					}//else proceed
				}
			}
		}
	}

	/**
	 * Converts a java bean instance into a sequence of values for writing.
	 *
	 * @param bean           an instance of the type defined in this class constructor.
	 * @param headers        All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 *
	 * @return a row of objects containing the values extracted from the java bean
	 */
	public final Object[] reverseConversions(T bean, String[] headers, int[] indexesToWrite) {
		if (bean == null) {
			return null;
		}

		if (row == null) {
			if (headers != null) {
				row = new Object[headers.length];
			} else if (indexesToWrite != null) {
				int minimumRowLength = 0;
				for (int index : indexesToWrite) {
					if (index + 1 > minimumRowLength) {
						minimumRowLength = index + 1;
					}
				}
				if (minimumRowLength < indexesToWrite.length) {
					minimumRowLength = indexesToWrite.length;
				}
				row = new Object[minimumRowLength];
			} else {
				Set<Integer> assignedIndexes = new HashSet<Integer>();
				int lastIndex = -1;
				for (FieldMapping f : parsedFields) {
					if (lastIndex < f.getIndex() + 1) {
						lastIndex = f.getIndex() + 1;
					}
					assignedIndexes.add(f.getIndex());
				}
				if (lastIndex < parsedFields.size()) {
					lastIndex = parsedFields.size();
				}

				row = new Object[lastIndex];
				if (syntheticHeaders == null) {
					syntheticHeaders = new String[lastIndex];
					Iterator<FieldMapping> it = parsedFields.iterator();
					for (int i = 0; i < lastIndex; i++) {
						if (assignedIndexes.contains(i)) {
							continue;
						}
						String fieldName = null;
						while (it.hasNext() && (fieldName = it.next().getFieldName()) == null)
							;
						syntheticHeaders[i] = fieldName;
					}
				}
			}
		}

		if (nestedAttributes != null) {
			for (Map.Entry<FieldMapping, BeanConversionProcessor<?>> e : nestedAttributes.entrySet()) {
				Object nested = e.getKey().read(bean);
				if (nested != null) {
					BeanConversionProcessor<Object> nestedProcessor = (BeanConversionProcessor<Object>) e.getValue();
					nestedProcessor.row = row;
					nestedProcessor.reverseConversions(nested, headers, indexesToWrite);
				}
			}
		}

		if (syntheticHeaders != null) {
			headers = syntheticHeaders;
		}

		try {
			mapFieldsToValues(bean, row, headers, indexesToWrite, false);
		} catch (Throwable ex) {
			if (ex instanceof DataProcessingException) {
				DataProcessingException error = (DataProcessingException) ex;
				if (error.isHandled()) {
					return null;
				} else {
					throw error;
				}
			} else if (!handleConversionError(ex, row, -1)) {
				throw toDataProcessingException(ex, row, -1);
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
	 *
	 * @return the class of the annotated java bean instances that will be manipulated by this processor.
	 */
	public Class<T> getBeanClass() {
		return beanClass;
	}
}
