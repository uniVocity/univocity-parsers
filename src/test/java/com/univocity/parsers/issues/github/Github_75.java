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

import com.univocity.parsers.conversions.*;
import org.testng.annotations.*;


import static org.testng.Assert.*;

public class Github_75 {

	@DataProvider
	public Object[][] inputProvider() {
		return new Object[][]{
				{8, "Dies ist ein Text", "Dies ist"},
				{8, " Dies ist ein Text mit 34 Zeichen. ", "Dies ist"},
				{17, "Dies ist ein Text", "Dies ist ein Text"},
				{17, " Dies ist ein Text mit 34 Zeichen. ", "Dies ist ein Text"},
				{18, "Dies ist ein Text", "Dies ist ein Text"},
				{18, " Dies ist ein Text mit 34 Zeichen. ", "Dies ist ein Text"},
				{18, "Dies ist ein Text ", "Dies ist ein Text"},
				{4, "  a b d", "a b"},
				{5, "  a b d ", "a b d"},
				{5, "", ""},
				{5, " ", ""},
				{2, " 1", "1"},
				{2, "  1", "1"},
		};
	}

	@Test(dataProvider = "inputProvider")
	public void testExecuteString(int length, String input, String expected) {
		String result = new TrimConversion(length).execute(input);
		assertTrue(result.length() <= length);
		assertEquals(result, expected);
	}
}

