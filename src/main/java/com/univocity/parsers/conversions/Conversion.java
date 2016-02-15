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
package com.univocity.parsers.conversions;

import com.univocity.parsers.common.processor.*;

/**
 * The interface that defines the conversion from one type of value to another, and vice versa.
 *
 * uniVocity-parsers provides a set of default conversions for usage with
 * {@link ObjectRowProcessor} and {@link ObjectRowWriterProcessor}.
 *
 * Annotations in package {@link com.univocity.parsers.annotations} are associated with different Conversion
 * implementations in {@link com.univocity.parsers.conversions}.
 *
 * @param <I> The input type to be converted to the output type <b>O</b>
 * @param <O> The type of outputs produced by a conversion applied to the an input <b>I</b>.
 *
 *            When used in conjunction with the {@link com.univocity.parsers.annotations.Convert} annotation,
 *            <b>O</b> should be compatible with the type of the annotated field.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.processor.ObjectRowProcessor
 * @see com.univocity.parsers.common.processor.ObjectRowWriterProcessor
 */
public interface Conversion<I, O> {

	/**
	 * Converts a value of type <b>I</b> to a value of type <b>O</b>.
	 *
	 * When used in conjunction with the {@link com.univocity.parsers.annotations.Convert} annotation, this method will perform
	 * the conversion from a parsed input {@code String} (if no other conversion has been applied before)
	 * to a value of the desired type, and the result will be assigned to the annotated field. Note that conversions can be
	 * chained so you need to make sure the input type of any previous conversion is compatible with <b>I</b>
	 *
	 * @param input the input of type <b>I</b> to be converted to an object of type <b>O</b>
	 *
	 * @return the conversion result.
	 */
	O execute(I input);

	/**
	 * Converts a value of type <b>O</b> to a value of type <b>I</b>.
	 *
	 * When used in conjunction with the {@link com.univocity.parsers.annotations.Convert} annotation, this method will
	 * convert the value of the annotated field so it can be written to the output (usually a {@code String}). Note that conversions can be
	 * chained so you need to make sure the type of any previous conversion is compatible with <b>O</b>
	 *
	 * @param input the input of type <b>O</b> to be converted to an object of type <b>I</b>
	 *
	 * @return the conversion result.
	 */
	I revert(O input);
}
