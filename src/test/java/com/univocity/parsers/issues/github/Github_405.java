/*******************************************************************************
 * Copyright 2020 Univocity Software Pty Ltd
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


import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/405
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_405 {

	@Test
	public void testPaddingOnFixedWidth() {
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField("padding", 4, FieldAlignment.RIGHT, '0')
				.addField("text", 4, FieldAlignment.LEFT, ' ');
		fields.keepPaddingOn("padding", "text");

		FixedWidthParserSettings settings = new FixedWidthParserSettings(fields);
		settings.setKeepPadding(true);

		FixedWidthParser parser = new FixedWidthParser(settings);

		assertEquals(Arrays.toString(parser.parseLine("0000abcd")),"[0000, abcd]");
		assertEquals(Arrays.toString(parser.parseLine("0000    ")),"[0000,     ]");
	}

}
