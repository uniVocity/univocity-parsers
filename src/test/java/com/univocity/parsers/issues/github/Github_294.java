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


import com.univocity.parsers.common.record.*;
import com.univocity.parsers.fixed.*;
import org.testng.*;
import org.testng.annotations.*;

import java.io.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/294
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_294 {

	@Test
	public void withoutLookAhead(){
		StringReader input = new StringReader("XAAAAABBBBBCCCCCDDDDD\n");
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField("chunk_1", 6, 11);
		fields.addField("chunk_2", 16, 21);
		FixedWidthParserSettings settings = new FixedWidthParserSettings(fields);
		settings.getFormat().setLineSeparator("\n");
		settings.setRecordEndsOnNewline(true);
		settings.setSkipTrailingCharsUntilNewline(true);
		FixedWidthParser parser = new FixedWidthParser(settings);
		Iterable<Record> iterator = parser.iterateRecords(input);
		for (Record r : iterator){

			Assert.assertEquals("BBBBB", r.getString(0));
			Assert.assertEquals("DDDDD", r.getString(1));
		}
	}

	@Test
	public void withLookAhead(){
		StringReader input = new StringReader("XAAAAABBBBBCCCCCDDDDD\n");
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField("chunk_1", 6, 11);
		fields.addField("chunk_2", 16, 21);
		FixedWidthParserSettings settings = new FixedWidthParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRecordEndsOnNewline(true);
		settings.setSkipTrailingCharsUntilNewline(true);
		settings.addFormatForLookahead("X", fields);
		FixedWidthParser parser = new FixedWidthParser(settings);
		Iterable<Record> iterator = parser.iterateRecords(input);
		for (Record r : iterator){
			System.out.println(r.getString(0));
			System.out.println(r.getString(1));
			Assert.assertEquals("BBBBB", r.getString(0));
			Assert.assertEquals("DDDDD", r.getString(1));
		}
	}

}
