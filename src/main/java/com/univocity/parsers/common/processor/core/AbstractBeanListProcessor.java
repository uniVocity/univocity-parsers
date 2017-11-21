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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

import java.util.*;

/**
 * A convenience {@link Processor} implementation for storing all java objects generated form the parsed input into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * parserSettings.setRowProcessor(new BeanListProcessor(MyObject.class));
 * parser.parse(reader); // will invoke the {@link AbstractBeanListProcessor#beanProcessed(Object, C)} method for each generated object.
 *
 * List&lt;T&gt; beans = rowProcessor.getBeans();
 * }</pre></blockquote><hr>
 *
 * @param <T> the annotated class type.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Processor
 * @see AbstractParser
 * @see AbstractBeanProcessor
 * @see BeanConversionProcessor
 */
public abstract class AbstractBeanListProcessor<T, C extends Context> extends AbstractBeanProcessor<T, C> {

	private List<T> beans;
	private String[] headers;
	private final int expectedBeanCount;

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType the class with its attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public AbstractBeanListProcessor(Class<T> beanType) {
		this(beanType, 0);
	}

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType          the class with its attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 * @param expectedBeanCount expected number of rows to be parsed from the input which will be converted into java beans.
	 *                          Used to pre-allocate the size of the output {@link List} returned by {@link #getBeans()}
	 */
	public AbstractBeanListProcessor(Class<T> beanType, int expectedBeanCount) {
		super(beanType, MethodFilter.ONLY_SETTERS);
		this.expectedBeanCount = expectedBeanCount <= 0 ? 10000 : expectedBeanCount;
	}

	/**
	 * Stores the generated java bean produced with a parsed record into a list.
	 *
	 * @param bean    java bean generated with the information extracted by the parser for an individual record
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 *
	 * @see com.univocity.parsers.common.processor.BeanProcessor
	 */
	@Override
	public void beanProcessed(T bean, C context) {
		beans.add(bean);
	}

	/**
	 * Returns the list of generated java beans at the end of the parsing process.
	 *
	 * @return the list of generated java beans at the end of the parsing process.
	 */
	public List<T> getBeans() {
		return beans == null ? Collections.<T>emptyList() : beans;
	}

	@Override
	public void processStarted(C context) {
		super.processStarted(context);
		beans = new ArrayList<T>(expectedBeanCount);
	}

	@Override
	public void processEnded(C context) {
		headers = context.headers();
		super.processEnded(context);
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 *
	 * @return the headers of all records parsed.
	 */
	public String[] getHeaders() {
		return headers;
	}
}
