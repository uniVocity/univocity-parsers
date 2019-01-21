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
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/258
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_258 {

	@Test
	public void testRecordsInLookaheadFixedWidth(){
		final FixedWidthParserSettings settings = new FixedWidthParserSettings();

		final FixedWidthFields cp = new FixedWidthFields()
				.addField("id", 2)
				.addField("number", 7);
		settings.addFormatForLookahead("CP", cp);

		final FixedWidthFields da = new FixedWidthFields()
				.addField("id",  2)
				.addField("constant", 1);
		settings.addFormatForLookahead("DA", da);

		final FixedWidthFields as = new FixedWidthFields()
				.addField("id", 2)
				.addField("subsidiary", 2)
				.addField("articleNumber", 7);
		settings.addFormatForLookahead("AS", as);

		FixedWidthParser parser = new FixedWidthParser(settings);

		List<Record> records = parser.parseAllRecords(new StringReader("" +
				"CP1234567\n" +
				"DA3\n" +
				"ASabcdefhij"));

		assertEquals(records.get(0).getMetaData().headers(), new String[]{"id", "number"});
		assertEquals(records.get(1).getMetaData().headers(), new String[]{"id", "constant"});
		assertEquals(records.get(2).getMetaData().headers(), new String[]{"id", "subsidiary", "articleNumber"});

		assertEquals(new TreeMap<String, String>(records.get(0).toFieldMap()).toString(), "{id=CP, number=1234567}");
		assertEquals(new TreeMap<String, String>(records.get(1).toFieldMap()).toString(), "{constant=3, id=DA}");
		assertEquals(new TreeMap<String, String>(records.get(2).toFieldMap()).toString(), "{articleNumber=cdefhij, id=AS, subsidiary=ab}");
	}

}
