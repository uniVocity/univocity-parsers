/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/280
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_280 {

	public static final class Model {
		@Parsed(field = "mycolumn")
		private String column1;

		@Parsed(field = "count")
		private int count;

		@Parsed
		private String text = "";

		private Model() {
		}


	}

	@Test
	public void testImmutableBeanConstructorParam() throws Exception {
		Model instance = new CsvRoutines().iterate(Model.class, new StringReader("mycolumn,text,count\nvalue,txt,1")).iterator().next();
		assertNotNull(instance);
		assertEquals(instance.column1, "value");
		assertEquals(instance.count, 1);
		assertEquals(instance.text, "txt");
	}

}
