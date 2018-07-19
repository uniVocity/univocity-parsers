/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
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

package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/253
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_253 {

	public static class AA {
		@NullString(nulls = { "", " " })
		@Parsed(
				field = "r1",
				defaultNullRead = "NULL")
		private String r1;

		@NullString(nulls = { "", " " })
		@Parsed(
				field = "r2",
				defaultNullRead = "NULL")
		private String r2;
	}

	@Test
	public void testBean(){
		CsvRoutines r = new CsvRoutines();
		r.getParserSettings().setNullValue("");

		for(AA o : r.iterate(AA.class, new StringReader("r1,r2\nref1,\nref1"))){
			assertEquals(o.r2, "NULL");
		}
	}

}