/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.csv;

import java.util.*;

import org.testng.*;

public class TestUtils {
	public static <T> void assertEquals(T[] result, T[] expected) {
		Assert.assertEquals(result, expected);
	}

	public static <T> void assertEquals(Collection<T> result, T[] expected) {
		assertEquals(result.toArray(), expected);
	}

	public static <T> void assertEquals(T[] result, Collection<T> expected) {
		assertEquals(result, expected.toArray());
	}
}
