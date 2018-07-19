/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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

import static org.testng.Assert.*;

public class Github_89 {

	@Test
	public void maxColumnsSettingWorksOnEOF() {
		int[] length = new int[]{2, 2};
		FixedWidthFields lengths = new FixedWidthFields(length);
		FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);
		settings.setMaxColumns(2);

		FixedWidthParser parser = new FixedWidthParser(settings);

		String[] data = parser.parseLine("abcd");

		assertEquals(data[0], "ab");
		assertEquals(data[1], "cd");
	}
}
