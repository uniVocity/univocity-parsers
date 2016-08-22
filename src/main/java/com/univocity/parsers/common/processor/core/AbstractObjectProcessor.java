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

import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;

/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into arrays of objects.
 * <p>This uses the value conversions provided by {@link Conversion} instances.
 *
 * <p> For each row processed, a sequence of conversions will be executed and stored in an object array, at its original position.
 * <p> The row with the result of these conversions will then be sent to the {@link AbstractObjectProcessor#rowProcessed(Object[], Context)} method, where the user can access it.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see Processor
 */
public abstract class AbstractObjectProcessor<T extends Context> extends DefaultConversionProcessor implements Processor<T> {

	/**
	 * Executes the sequences of conversions defined using {@link DefaultConversionProcessor#convertFields(Conversion...)},
	 * {@link DefaultConversionProcessor#convertIndexes(Conversion...)} and {@link DefaultConversionProcessor#convertAll(Conversion...)},
	 * for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#execute(Object)} method.
	 * <p>In general the conversions will process a String and convert it to some object value (such as booleans, dates, etc).
	 *
	 * @param row     the parsed record with its individual records as extracted from the original input.
	 * @param context the current state of the parsing process.
	 *                <p> Fields that do not have any conversion defined will just be copied to the object array into their original positions.
	 */
	@Override
	public void rowProcessed(String[] row, T context) {
		Object[] objectRow = applyConversions(row, context);
		if (objectRow != null) {
			rowProcessed(objectRow, context);
		}
	}

	/**
	 * Invoked by the processor after all values of a valid record have been processed and converted into an Object array.
	 *
	 * @param row     object array created with the information extracted by the parser and then converted.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	public abstract void rowProcessed(Object[] row, T context);

	@Override
	public void processStarted(T context) {
	}

	@Override
	public void processEnded(T context) {
	}
}
