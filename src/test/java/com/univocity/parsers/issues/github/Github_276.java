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


import com.univocity.parsers.examples.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.util.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/276
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_276 extends Example {

	@Test
	public void testKeepPaddingFlag() throws Exception {

		FixedWidthFields fields = new FixedWidthFields(4, 5, 40, 40, 8);
		fields.stripPaddingFrom(0, 1, 4);

		FixedWidthParserSettings settings = new FixedWidthParserSettings(fields);
		settings.setKeepPadding(true);
		settings.getFormat().setPadding('_');
		settings.getFormat().setLineSeparator("\n");

		FixedWidthParser parser = new FixedWidthParser(settings);

		List<String[]> allRows = parser.parseAll(getReader("/examples/example.txt"));
		
		printAndValidate(null, allRows);
	}

}
