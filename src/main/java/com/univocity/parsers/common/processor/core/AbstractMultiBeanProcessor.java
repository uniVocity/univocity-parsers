/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

import java.util.*;

/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into java objects.
 *
 * <p>The class types passed to the constructor of this class must contain the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * <p> For each row processed, one or more java bean instances of any given class will be created with their fields populated.
 * <p> Each individual instance will then be sent to the {@link AbstractMultiBeanProcessor#beanProcessed(Class, Object, Context)} method, where the user can access the
 * beans parsed for each row.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see Processor
 * @see com.univocity.parsers.common.processor.BeanProcessor
 */
public abstract class AbstractMultiBeanProcessor<C extends Context> implements Processor<C>, ConversionProcessor {

	private final AbstractBeanProcessor<?, C>[] beanProcessors;
	private final Map<Class, AbstractBeanProcessor> processorMap = new HashMap<Class, AbstractBeanProcessor>();

	/**
	 * Creates a processor for java beans of multiple types
	 *
	 * @param beanTypes the classes with their attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public AbstractMultiBeanProcessor(Class... beanTypes) {
		ArgumentUtils.noNulls("Bean types", beanTypes);
		this.beanProcessors = new AbstractBeanProcessor[beanTypes.length];

		for (int i = 0; i < beanTypes.length; i++) {
			final Class type = beanTypes[i];
			beanProcessors[i] = new AbstractBeanProcessor<Object, C>(type, MethodFilter.ONLY_SETTERS) {
				@Override
				public void beanProcessed(Object bean, C context) {
					AbstractMultiBeanProcessor.this.beanProcessed(type, bean, context);
				}
			};

			processorMap.put(type, beanProcessors[i]);
		}
	}

	public final Class[] getBeanClasses() {
		Class[] classes = new Class[beanProcessors.length];
		for (int i = 0; i < beanProcessors.length; i++) {
			classes[i] = beanProcessors[i].beanClass;
		}
		return classes;
	}

	/**
	 * Returns the {@link com.univocity.parsers.common.processor.BeanProcessor} responsible for processing a given class
	 *
	 * @param type the type of java bean being processed
	 * @param <T>  the type of java bean being processed
	 *
	 * @return the {@link com.univocity.parsers.common.processor.BeanProcessor} that handles java beans of the given class.
	 */
	public <T> AbstractBeanProcessor<T, C> getProcessorOfType(Class<T> type) {
		AbstractBeanProcessor<T, C> processor = processorMap.get(type);
		if (processor == null) {
			throw new IllegalArgumentException("No processor of type '" + type.getName() + "' is available. Supported types are: " + processorMap.keySet());
		}
		return processor;
	}

	/**
	 * Invoked by the processor after all values of a valid record have been processed and converted into a java object.
	 *
	 * @param beanType     the type of the object created by the parser using the information collected for an individual record.
	 * @param beanInstance java object created with the information extracted by the parser for an individual record.
	 * @param context      A contextual object with information and controls over the current state of the parsing process
	 */
	public abstract void beanProcessed(Class<?> beanType, Object beanInstance, C context);

	@Override
	public void processStarted(C context) {
		for (int i = 0; i < beanProcessors.length; i++) {
			beanProcessors[i].processStarted(context);
		}
	}

	@Override
	public final void rowProcessed(String[] row, C context) {
		for (int i = 0; i < beanProcessors.length; i++) {
			beanProcessors[i].rowProcessed(row, context);
		}
	}

	@Override
	public void processEnded(C context) {
		for (int i = 0; i < beanProcessors.length; i++) {
			beanProcessors[i].processEnded(context);
		}
	}


	@Override
	public FieldSet<Integer> convertIndexes(Conversion... conversions) {
		List<FieldSet<Integer>> sets = new ArrayList<FieldSet<Integer>>(beanProcessors.length);
		for (int i = 0; i < beanProcessors.length; i++) {
			sets.add(beanProcessors[i].convertIndexes(conversions));
		}
		return new FieldSet<Integer>(sets);
	}

	@Override
	public void convertAll(Conversion... conversions) {
		for (int i = 0; i < beanProcessors.length; i++) {
			beanProcessors[i].convertAll(conversions);
		}
	}

	@Override
	public FieldSet<String> convertFields(Conversion... conversions) {
		List<FieldSet<String>> sets = new ArrayList<FieldSet<String>>(beanProcessors.length);
		for (int i = 0; i < beanProcessors.length; i++) {
			sets.add(beanProcessors[i].convertFields(conversions));
		}
		return new FieldSet<String>(sets);
	}

	@Override
	public void convertType(Class<?> type, Conversion... conversions) {
		for (int i = 0; i < beanProcessors.length; i++) {
			beanProcessors[i].convertType(type, conversions);
		}
	}
}
