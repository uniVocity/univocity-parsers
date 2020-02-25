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

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/352
 */
public class Github_352 {

	@Test
	public void testNullableFields(){
		CsvWriterSettings s = new CsvWriterSettings();
		s.getFormat().setQuote('\'');
		s.setNullValue("null");
		s.setEmptyValue("''");
		s.setQuoteAllFields(true);
		s.setQuoteNulls(false);
		CsvWriter w = new CsvWriter(s);

		assertEquals(w.writeRowToString(1, "one", "", "empty"), "'1','one','','empty'");
		assertEquals(w.writeRowToString(2, "two", null, "null"), "'2','two',null,'null'");
		assertEquals(w.writeRowToString(3, null, "THREE", "null"), "'3',null,'THREE','null'");
		assertEquals(w.writeRowToString(null, "four", "FOUR", "null"), "null,'four','FOUR','null'");
	}

}