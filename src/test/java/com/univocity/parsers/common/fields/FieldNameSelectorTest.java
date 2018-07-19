/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
package com.univocity.parsers.common.fields;

import org.testng.annotations.*;

import static org.testng.Assert.*;

public class FieldNameSelectorTest {

	@Test
	public void getFieldsToExtract() {
		FieldNameSelector selector = new FieldNameSelector();
		selector.add("D", "A");

		int[] indexes = selector.getFieldIndexes(new String[]{"A", "B", "C", "D", "E", "F"});

		int[] expected = new int[]{3, 0};

		assertEquals(indexes, expected);
	}
}
