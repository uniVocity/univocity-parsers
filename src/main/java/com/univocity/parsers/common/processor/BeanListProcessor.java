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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

import java.util.*;

/**
 * A convenience {@link BeanProcessor} implementation for storing all java objects generated form the parsed input into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * parserSettings.setRowProcessor(new BeanListProcessor(MyObject.class));
 * parser.parse(reader); // will invoke the {@link BeanListProcessor#beanProcessed(Object, Context)} method for each generated object.
 *
 * List&lt;T&gt; beans = rowProcessor.getBeans();
 * }</pre></blockquote><hr>
 *
 * @param <T> the annotated class type.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BeanProcessor
 * @see RowProcessor
 * @see AbstractParser
 * @see AbstractBeanListProcessor
 */
public class BeanListProcessor<T> extends AbstractBeanListProcessor<T, ParsingContext> implements RowProcessor {

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType the class with its attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public BeanListProcessor(Class<T> beanType) {
		super(beanType);
	}

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType          the class with its attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 * @param expectedBeanCount expected number of rows to be parsed from the input which will be converted into java beans.
	 *                          Used to pre-allocate the size of the output {@link List}
	 *                          returned by {@link #getBeans()}
	 */
	public BeanListProcessor(Class<T> beanType, int expectedBeanCount) {
		super(beanType, expectedBeanCount);
	}

}
