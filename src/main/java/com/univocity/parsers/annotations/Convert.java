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
package com.univocity.parsers.annotations;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.*;

/**
 * Assigns a custom implementation of {@link Conversion} to be executed ({@link Conversion#execute(Object)})
 * when writing to the field and reverted ({@link Conversion#revert(Object)}) when reading from the field.
 *
 * @see Conversion
 * @see Conversions
 * @see BeanProcessor
 * @see BeanWriterProcessor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Convert {

	/**
	 * A user provided implementation of {@link Conversion} which will be instantiated using the arguments provided by {@link Convert#args()}
	 * @return custom class used to convert values
	 */
	@SuppressWarnings("rawtypes") Class<? extends Conversion> conversionClass();

	/**
	 * The arguments to use when invoking the constructor of the class given by {@link Convert#conversionClass()}.
	 * @return list of arguments create a new instance of the custom conversion class.
	 */
	String[] args() default {};
}
