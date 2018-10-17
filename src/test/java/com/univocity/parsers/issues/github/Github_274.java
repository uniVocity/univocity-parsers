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


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.common.processor.core.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/274
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_274 {

	public static class B1 {
		@Parsed
		String a;
		@Parsed
		String b;

		@Override
		public String toString() {
			return "B1{" +
					"a='" + a + '\'' +
					", b='" + b + '\'' +
					'}';
		}
	}

	public static class B2 {
		@Parsed
		String a;
		@Parsed
		String b;
		@Parsed
		String e;
		@Parsed
		String f;

		@Override
		public String toString() {
			return "B2{" +
					"a='" + a + '\'' +
					", b='" + b + '\'' +
					", e='" + e + '\'' +
					", f='" + f + '\'' +
					'}';
		}
	}

	@Test
	public void testHeadersInProcessorSwitch() throws Exception {
		CsvParserSettings s = new CsvParserSettings();

		final BeanListProcessor o1 = new BeanListProcessor<B1>(B1.class);
		final BeanListProcessor o2 = new BeanListProcessor<B2>(B2.class);

		InputValueSwitch sw = new InputValueSwitch(2) {
			@Override
			public void processorSwitched(Processor<ParsingContext> from, Processor<ParsingContext> to) {
				super.processorSwitched(from, to);
			}
		};
		sw.addSwitchForValue("1", o1, "a", "b");
		sw.addSwitchForValue("2", o2, "a", "b", "d", "e", "f");

		s.setProcessor(sw);
		CsvParser p = new CsvParser(s);

		p.parseAll(new StringReader("a,b,1\nc,d,2,e,f\n"));

		assertEquals(o1.getBeans().get(0).toString(), "B1{a='a', b='b'}");
		assertEquals(o2.getBeans().get(0).toString(), "B2{a='c', b='d', e='e', f='f'}");

	}

}
