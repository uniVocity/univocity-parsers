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

import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;

/**
 *
 * A {@link RowWriterProcessor} implementation for executing conversion sequences in object arrays before for writing them using any implementation of {@link AbstractWriter}.
 *
 * @see AbstractWriter
 * @see RowWriterProcessor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class ObjectRowWriterProcessor extends DefaultConversionProcessor implements RowWriterProcessor<Object[]> {

	/**
	 * Executes the sequences of conversions defined using {@link DefaultConversionProcessor#convertFields(Conversion...)}, {@link DefaultConversionProcessor#convertIndexes(Conversion...)} and {@link DefaultConversionProcessor#convertAll(Conversion...)}, for every field in the given row.
	 *
	 * <p>Each field will be transformed using the {@link Conversion#execute(Object)} method.
	 * <p>In general the conversions will process a String and convert it to some object value (such as booleans, dates, etc).
	 *
	 * @param input the object array that represents a record with its individual fields.
	 * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return an row of Object instances containing the values obtained after the execution of all conversions.
	 * <p> Fields that do not have any conversion defined will just be copied to the object array into their original positions.
	 */
	@Override
	public Object[] write(Object[] input, String[] headers, int[] indexesToWrite) {
		if (input == null) {
			return null;
		}

		Object[] output = new Object[input.length];
		System.arraycopy(input, 0, output, 0, input.length);

		if (reverseConversions(false, output, NormalizedString.toIdentifierGroupArray(headers), indexesToWrite)) {
			return output;
		}

		return null;
	}
}
