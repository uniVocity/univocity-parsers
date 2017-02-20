/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/132
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_136 {

	@Test
	public void testIterateJavaBeansWithParsingContext() throws Exception {
		List<TestBean> beans = new ArrayList<TestBean>();

		Reader input = CsvParserTest.newReader("/examples/bean_test.csv");

		CsvRoutines routine = new CsvRoutines();
		ResultIterator<TestBean, ParsingContext> it = routine.iterate(TestBean.class, input).iterator();

		StringBuilder content = new StringBuilder();
		while(it.hasNext()) {
			content.append(it.getContext().currentParsedContent());
			beans.add(it.next());
		}
		assertEquals(beans.size(), 2);
		assertEquals(beans.get(0).getQuantity(), Integer.valueOf(1));
		assertEquals(beans.get(1).getComments(), "\" something \"");

		assertEquals(content.toString(), "" +
				"10-oct-2001,\t555.999,\t1,\t\t\tyEs\t\t,?\n" +
				"2001-10-10,\t\t,\t\t\t?,\t\t\tN\t\t,\"  \"\" something \"\"  \"");
	}


}
