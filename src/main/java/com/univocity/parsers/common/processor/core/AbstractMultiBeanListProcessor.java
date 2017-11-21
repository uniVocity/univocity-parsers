/*
 * Copyright (c) 2015. uniVocity Software Pty Ltd
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

import java.util.*;

/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into java objects, storing
 * them into lists. This processor stores beans in separate lists, one for each type of bean processed.
 * All lists of all types will have the same number of entries as the number of records in the input.
 * When an object of a particular type can't be generated from a row, {@code null} will be added to the list. This ensures all lists are the same size,
 * and each element of each list contains the exact information parsed from each row.
 *
 * <p>The class types passed to the constructor of this class must contain the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see Processor
 * @see AbstractBeanProcessor
 * @see AbstractMultiBeanProcessor
 */
public class AbstractMultiBeanListProcessor<C extends Context> extends AbstractMultiBeanRowProcessor<C> {

	private final Class[] beanTypes;
	private final List[] beans;
	private String[] headers;
	private int expectedBeanCount;

	/**
	 * Creates a processor for java beans of multiple types
	 *
	 * @param expectedBeanCount expected number of rows to be parsed from the input which will be converted into java beans.
	 *                          Used to pre-allocate the size of the output {@link List} returned by {@link #getBeans()}
	 * @param beanTypes         the classes with their attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public AbstractMultiBeanListProcessor(int expectedBeanCount, Class... beanTypes) {
		super(beanTypes);
		this.beanTypes = beanTypes;
		this.beans = new List[beanTypes.length];
		this.expectedBeanCount = expectedBeanCount <= 0 ? 10000 : expectedBeanCount;
	}

	/**
	 * Creates a processor for java beans of multiple types
	 *
	 * @param beanTypes the classes with their attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public AbstractMultiBeanListProcessor(Class... beanTypes) {
		this(0, beanTypes);
	}

	@Override
	public final void processStarted(C context) {
		super.processStarted(context);
		for (int i = 0; i < beanTypes.length; i++) {
			beans[i] = new ArrayList(expectedBeanCount);
		}
	}

	@Override
	protected final void rowProcessed(Map<Class<?>, Object> row, C context) {
		for (int i = 0; i < beanTypes.length; i++) {
			Object bean = row.get(beanTypes[i]);
			beans[i].add(bean);
		}
	}

	@Override
	public final void processEnded(C context) {
		headers = context.headers();
		super.processEnded(context);
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 *
	 * @return the headers of all records parsed.
	 */
	public final String[] getHeaders() {
		return headers;
	}

	/**
	 * Returns the beans of a given type processed from the input.
	 *
	 * @param beanType the type of bean processed
	 * @param <T>      the type of bean processed
	 *
	 * @return a list with all beans of the given that were processed from the input. Might contain nulls.
	 */
	public <T> List<T> getBeans(Class<T> beanType) {
		int index = ArgumentUtils.indexOf(beanTypes, beanType);
		if (index == -1) {
			throw new IllegalArgumentException("Unknown bean type '" + beanType.getSimpleName() + "'. Available types are: " + Arrays.toString(beanTypes));
		}
		return beans[index];
	}

	/**
	 * Returns a map of all beans processed from the input.
	 *
	 * @return all beans processed from the input.
	 */
	public Map<Class<?>, List<?>> getBeans() {
		LinkedHashMap<Class<?>, List<?>> out = new LinkedHashMap<Class<?>, List<?>>();
		for (int i = 0; i < beanTypes.length; i++) {
			out.put(beanTypes[i], beans[i]);
		}
		return out;
	}

}
