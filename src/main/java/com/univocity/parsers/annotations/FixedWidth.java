/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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
import com.univocity.parsers.fixed.*;

import java.lang.annotation.*;

/**
 * The {@code @FixedWidth} annotation, along with the {@link Parsed} annotation, allows users to configure the length,
 * alignment and padding of fields parsed/written using the {@link FixedWidthParser} and {@link FixedWidthWriter}
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 * @see FixedWidthFields
 * @see FixedWidthParser
 * @see FixedWidthWriter
 * @see FixedWidthParserSettings
 * @see FixedWidthWriterSettings
 * @see BeanProcessor
 * @see BeanWriterProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface FixedWidth {

	/**
	 * Sets the length of the fixed-width field
	 *
	 * @return length of the fixed-width field
	 */
	int value();

	/**
	 * Sets the alignment of the fixed-width field
	 *
	 * @return alignment of the fixed-width field
	 */
	FieldAlignment alignment() default FieldAlignment.LEFT;

	/**
	 * Sets the padding character of the fixed-width field
	 *
	 * @return padding of the fixed-width field
	 */
	char padding() default ' ';

}
