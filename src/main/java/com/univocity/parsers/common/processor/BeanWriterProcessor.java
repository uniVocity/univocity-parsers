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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.processor.core.*;

/**
 *
 * A {@link RowWriterProcessor} implementation for converting annotated java objects into object arrays suitable for writing in any implementation of {@link AbstractWriter}.
 * <p>The class type of the object must contain the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * <p> For any given java bean instance, this processor will read and convert annotated fields into an object array.
 *
 *
 * @param <T> the annotated class type.
 *
 * @see AbstractWriter
 * @see RowWriterProcessor
 * @see BeanConversionProcessor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class BeanWriterProcessor<T> extends BeanConversionProcessor<T> implements RowWriterProcessor<T> {


	/**
	 * Initializes the BeanWriterProcessor with the annotated bean class
	 * @param beanType the class annotated with one or more of the annotations provided in {@link com.univocity.parsers.annotations}.
	 */
	public BeanWriterProcessor(Class<T> beanType) {
		super(beanType, MethodFilter.ONLY_GETTERS);
	}

	/**
	 * Converts the java bean instance into a sequence of values for writing.
	 *
	 * @param input an instance of the type defined in this class constructor.
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return a row of objects containing the values extracted from the java bean
	 */
	@Override
	public Object[] write(T input, String[] headers, int[] indexesToWrite) {
		if (!initialized) {
			super.initialize(headers);
		}
		return reverseConversions(input, headers, indexesToWrite);
	}

	@Override
	protected FieldConversionMapping cloneConversions(){
		return null;
	}
}
