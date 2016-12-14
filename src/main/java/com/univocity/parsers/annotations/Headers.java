/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.annotations;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

import java.lang.annotation.*;

/**
 * The {@code Headers} annotation allows java beans to configure how to handle field names in a given input/output
 *
 * <p> With this annotation, you can configure the sequence of headers to use when reading/writing:</p>
 *
 * <ul>
 *  <li>when reading, the given {@link #sequence()} of header names will be used to refer to each column, irrespective of whether or not the input contains a header row.
 *  	If empty, and no headers have been defined in {@link CommonSettings#getHeaders()}, the parser will automatically use the first row in the input as the header row,
 *  	unless the fields in the bean have been annotated using {@link Parsed#index()} only.
 *  </li>
 *  <li>when writing, the given {@link #sequence()} of names will be used to refer to each column and will be used for writing the header row if {@link #write()} is enabled.
 *  	If empty, and no headers have been defined in {@link CommonSettings#getHeaders()}, the names given by attributes annotated with {@link Parsed#field()} will be used.
 *  </li>
 * </ul>
 *
 *
 * <p>
 * 	This annotation has no effect if {@link CommonSettings#isAutoConfigurationEnabled()} evaluates to {@code false}.
 * </p>
 *
 * @see BeanWriterProcessor
 * @see BeanProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Headers {

	/**
	 * Returns the sequence of header names in the input/output
	 *
	 * @return the sequence of header names in the input/output
	 */
	String[] sequence() default {};

	/**
	 * Indicates whether a row with headers should be written to the output.
	 * @return a flag indicating whether to write the headers to the output when writing instances of a java bean.
	 */
	boolean write() default true;

	/**
	 * Indicates whether the first row of on the input should be extracted as a header row.
	 * @return a flag indicating whether to extract the headers from the first valid row when reading. If
	 */
	boolean extract() default false;

}
