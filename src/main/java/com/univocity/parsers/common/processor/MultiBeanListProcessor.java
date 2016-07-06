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

package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

/**
 *
 * A {@link RowProcessor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into java objects, storing
 * them into lists. This processor stores beans in separate lists, one for each type of bean processed.
 * All lists of all types will have the same number of entries as the number of records in the input.
 * When an object of a particular type can't be generated from a row, {@code null} will be added to the list. This ensures all lists are the same size,
 * and each element of each list contains the exact information parsed from each row.
 *
 * <p>The class types passed to the constructor of this class must contain the annotations provided in {@link com.univocity.parsers.annotations}.
 *
 * @see AbstractParser
 * @see RowProcessor
 * @see BeanProcessor
 * @see MultiBeanProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class MultiBeanListProcessor extends AbstractMultiBeanListProcessor<ParsingContext> implements RowProcessor{

	/**
	 * Creates a processor for java beans of multiple types
	 * @param beanTypes the classes with their attributes mapped to fields of records parsed by an {@link AbstractParser} or written by an {@link AbstractWriter}.
	 */
	public MultiBeanListProcessor(Class... beanTypes) {
		super(beanTypes);
	}
}
