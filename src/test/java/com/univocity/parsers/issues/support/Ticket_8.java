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

package com.univocity.parsers.issues.support;

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Ticket_8 extends Example {

	@Test
	public void testIterateOverBeansWithErrorHandling(){
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		final boolean[] errorFlag = new boolean[]{false};
		settings.setProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				errorFlag[0] = true;
				assertEquals(error.getValue(), "yEs");
			}
		});

		CsvRoutines routines = new CsvRoutines(settings);
		Iterator<AnotherTestBean> it = routines.iterate(AnotherTestBean.class, getReader("/examples/bean_test.csv")).iterator();

		assertTrue(it.hasNext());
		assertNull(it.next()); //failed to process bean from broken input row.
		assertTrue(errorFlag[0]); //tests the error handler ran

		assertTrue(it.hasNext());
		assertNotNull(it.next());

		assertFalse(it.hasNext());
	}

}
