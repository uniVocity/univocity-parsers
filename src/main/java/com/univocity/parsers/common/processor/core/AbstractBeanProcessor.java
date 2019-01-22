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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into java objects.
 * <p>The class type of the object must contain the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * <p> For each row processed, a java bean instance of a given class will be created with its fields populated.
 * <p> This instance will then be sent to the {@link AbstractBeanProcessor#beanProcessed(Object, Context)} method, where the user can access it.
 *
 * @param <T> the annotated class type.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see Processor
 */
public abstract class AbstractBeanProcessor<T, C extends Context> extends BeanConversionProcessor<T> implements Processor<C> {

	/**
	 * Creates a processor for java beans of a given type.
	 *
	 * @param beanType     the class with its attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 * @param methodFilter filter to apply over annotated methods when the processor is reading data from beans (to write values to an output)
	 *                     or writing values into beans (when parsing). It is used to choose either a "get" or a "set"
	 *                     method annotated with {@link Parsed}, when both methods target the same field.
	 */
	public AbstractBeanProcessor(Class<T> beanType, MethodFilter methodFilter) {
		super(beanType, methodFilter);
	}

	/**
	 * Converts a parsed row to a java object
	 */
	@Override
	public final void rowProcessed(String[] row, C context) {
		T instance = createBean(row, context);
		if (instance != null) {
			beanProcessed(instance, context);
		}
	}

	/**
	 * Invoked by the processor after all values of a valid record have been processed and converted into a java object.
	 *
	 * @param bean    java object created with the information extracted by the parser for an individual record.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	public abstract void beanProcessed(T bean, C context);

	@Override
	public void processStarted(C context) {
		super.initialize(NormalizedString.toArray(context.headers()));
	}

	@Override
	public void processEnded(C context) {
	}
}
