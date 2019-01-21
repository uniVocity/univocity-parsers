/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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
package com.univocity.parsers.common;

import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class NormalizedStringTest {

	private NormalizedString A = NormalizedString.valueOf("A");
	private NormalizedString a = NormalizedString.valueOf("a");
	private NormalizedString _a_ = NormalizedString.valueOf(" a ");
	private NormalizedString _A_ = NormalizedString.valueOf(" A ");

	NormalizedString[] normalized = new NormalizedString[]{A, a, _a_, _A_};

	private NormalizedString dA = NormalizedString.literalValueOf("A");
	private NormalizedString da = NormalizedString.literalValueOf("a");
	private NormalizedString d_a_ = NormalizedString.literalValueOf(" a ");
	private NormalizedString d_A_ = NormalizedString.literalValueOf(" A ");

	NormalizedString[] literal = new NormalizedString[]{dA, da, d_a_, d_A_};

	@Test
	public void testEqualsHashCodeContract() throws Exception {
		for (int i = 0; i < normalized.length; i++) {
			for (int j = 0; j < normalized.length; j++) {
				assertEquals(normalized[i], normalized[j]);
				assertEquals(normalized[j], normalized[i]);
				assertEquals(normalized[i].hashCode(), normalized[j].hashCode());
			}
		}

		for (int i = 0; i < literal.length; i++) {
			for (int j = 0; j < literal.length; j++) {
				if (i == j) {
					assertEquals(literal[i], literal[j]);
					assertEquals(literal[j], literal[i]);
					assertEquals(literal[i].hashCode(), literal[j].hashCode());
				} else {
					assertFalse(literal[i].equals(literal[j]));
					assertFalse(literal[j].equals(literal[i]));
				}
			}
		}

		for (int i = 0; i < normalized.length; i++) {
			if (normalized[i].equals(literal[i])) {
				assertEquals(literal[i], normalized[i]);
				assertEquals(normalized[i].hashCode(), literal[i].hashCode());
			} else {
				assertNotEquals(literal[i], normalized[i]);
			}
		}
	}

	@Test
	public void testCompareToContract() throws Exception {
		for (int i = 0; i < normalized.length; i++) {
			for (int j = 0; j < normalized.length; j++) {
				assertEquals(normalized[i], normalized[j]);
				assertTrue(normalized[i].compareTo(normalized[j]) == 0);
			}
		}

		for (int i = 0; i < literal.length; i++) {
			for (int j = 0; j < literal.length; j++) {
				if (i == j) {
					assertEquals(literal[i], literal[j]);
					assertTrue(literal[i].compareTo(literal[j]) == 0);
				} else {
					assertFalse(literal[i].equals(literal[j]));
					assertFalse(literal[i].compareTo(literal[j]) == 0);
				}
			}
		}

		for (int i = 0; i < normalized.length; i++) {
			if (normalized[i].equals(literal[i])) {
				assertTrue(normalized[i].compareTo(literal[i]) == 0);
			} else {
				assertFalse(normalized[i].compareTo(literal[i]) == 0);
			}
		}
	}

	@DataProvider
	private Object[][] setProvider() {
		return new Object[][]{
				{new HashSet<NormalizedString>(), "HashSet"},
				{new TreeSet<NormalizedString>(), "TreeSet"}
		};
	}

	@Test(dataProvider = "setProvider")
	public void testSetBehaves(Set<NormalizedString> set, String name) {
		Collections.addAll(set, normalized); // 1

		assertEquals(set.size(), 1); //hashcode & equals are the same for all elements, so we'll end up with just one.

		for (NormalizedString element : normalized) {
			assertTrue(set.contains(element));
		}

		int count = 0;
		for (NormalizedString element : literal) {
			if (set.contains(element)) {
				count++;
			}
		}

		assertEquals(count, 1); // only one literal element will match because the original strings are compared and if they match then the NormalizedString always matches.

		Collections.addAll(set, literal);

		assertEquals(set.size(), literal.length); //all literal elements should be in the set

		for (NormalizedString element : normalized) {
			assertTrue(set.contains(element)); //normalized Strings should all be found here
		}

		for (NormalizedString element : literal) {
			assertTrue(set.contains(element)); //literal Strings should all be found here
		}
	}

	@Test
	public void testComparisonForOrdering() {
		TreeSet<NormalizedString> set = new TreeSet<NormalizedString>();
		Collections.addAll(set, literal);

		TreeSet<String> stringSet = new TreeSet<String>();
		for (NormalizedString element : literal) {
			stringSet.add(element.toString());
		}

		assertEquals(stringSet.toString(), set.toString());

		set.add(NormalizedString.valueOf("0"));
		set.add(NormalizedString.literalValueOf(" 0"));
		set.add(NormalizedString.literalValueOf("Z"));
		set.add(NormalizedString.valueOf("z"));

		stringSet.add("0");
		stringSet.add(" 0");
		stringSet.add("Z");
		stringSet.add("z");

		assertEquals(set.toString(), stringSet.toString());
	}

	@Test
	public void testHashMap() {
		Map<NormalizedString, String> map = new HashMap<NormalizedString, String>();
		putString(map, "A");
		putString(map, "'a'"); //literal

		assertEquals(map.get(NormalizedString.valueOf("A")), "A");
		assertEquals(map.get(NormalizedString.literalValueOf("A")), "A");
		assertEquals(map.get(NormalizedString.valueOf("'a'")), "'a'");
		assertEquals(map.get(NormalizedString.literalValueOf("a")), "'a'");

		// Unspecified behavior here as A and 'a' clash (A should be a literal in this test, but it isn't).
		// A normalized, non literal 'a' can match either the literal 'a' or the normalized A, depending on the
		// HashMap implementation. These entries have the same hashcode and the equals method will
		// compare this:

		// Search entry:
		// NormalizedString.valueOf("a") - not a literal, normalized value = a

		// Keys in map
		// NormalizedString.valueOf("A") - not literal, normalized value = a (will match)
		// NormalizedString.valueOf("'a'") - literal, original value = a (will also match)
		
		// The entry picked up first by the map implementation will be returned.

		// On JDK 6 this is the expected output:
		// assertEquals(map.get(NormalizedString.valueOf("a")), "'a'");

		// On JDK 8 the expected output is:
		// assertEquals(map.get(NormalizedString.valueOf("a")), "A");
	}

	@Test
	public void testTreeMap() {
		Map<NormalizedString, String> map = new TreeMap<NormalizedString, String>();
		putString(map, "A");
		putString(map, "'a'"); //literal

		assertEquals(map.get(NormalizedString.valueOf("A")), "A");
		assertEquals(map.get(NormalizedString.literalValueOf("A")), "A");
		assertEquals(map.get(NormalizedString.valueOf("'a'")), "'a'");
		assertEquals(map.get(NormalizedString.literalValueOf("a")), "'a'");
		assertEquals(map.get(NormalizedString.valueOf("a")), "A");  //compareTo implementation will run a compareTo against normalized values (normalized against normalized)
	}

	private void putString(Map<NormalizedString, String> map, String str) {
		map.put(NormalizedString.valueOf(str), str);
	}

	@Test
	public void identifyLiterals(){
		NormalizedString[] s = NormalizedString.toArray("a", "A", " a ", " A ", "a ", "A ", "B");
		Set<NormalizedString> set = new HashSet<NormalizedString>();
		Collections.addAll(set, s);
		assertEquals(set.size(), 2);

		NormalizedString.identifyLiterals(s);
		assertTrue(s[0].isLiteral());
		assertTrue(s[1].isLiteral());
		assertTrue(s[2].isLiteral());
		assertTrue(s[3].isLiteral());
		assertTrue(s[4].isLiteral());
		assertTrue(s[5].isLiteral());
		assertFalse(s[6].isLiteral());

		set = new HashSet<NormalizedString>();
		Collections.addAll(set, s);
		assertEquals(set.size(), 7);
	}
}