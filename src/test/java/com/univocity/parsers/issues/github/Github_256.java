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
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/256
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_256 {

	@Test
	public void testParseFooFile(){
		ArrayList<Foo> fooList = new ArrayList<Foo>();

		// Foo fields - the default format to use as nothing in the rows identify their type
		FixedWidthFields fooFields = new FixedWidthFields();
		fooFields.addField("UserId", 10, FieldAlignment.RIGHT, '0');

		// Create the parser settings using foo fields as the default format.
		FixedWidthParserSettings settings = new FixedWidthParserSettings(fooFields);

		// Header fields - rows identified by the "H" lookahead
		FixedWidthFields headerFields = new FixedWidthFields();
		headerFields.addField("RecordType", 1);
		headerFields.addField("FileName", 10);
		headerFields.addField("RecordCount", 10, FieldAlignment.RIGHT, '0');
		// associate the "H" lookahead with the header fields
		settings.addFormatForLookahead("H", headerFields);


		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setPadding(' ');


		final FooFileProcessor fileProcessor = new FooFileProcessor();
		final FooProcessor fooProcesser = new FooProcessor(fooList);
		InputValueSwitch processor = new InputValueSwitch("RecordType") {
			public void rowProcessorSwitched(RowProcessor from, RowProcessor to) {
				if(from == fileProcessor) {
					fooProcesser.prepareForRecords(((FooFileProcessor) from).file);
				}
			}
		};
		processor.addSwitchForValue("H", fileProcessor);
		processor.setDefaultSwitch(fooProcesser);
		settings.setProcessor(processor);

		FixedWidthParser  parser = new FixedWidthParser(settings);
		parser.parse(new StringReader("" +
				"HFooFile   0000000001\n" +
				"0000000002\n"));


		assertFalse(fooList.isEmpty());
		assertEquals(fooList.get(0).userId, Long.valueOf(2L));
		assertNotNull(fooList.get(0).fooFile);

		assertEquals(fooList.get(0).fooFile.name, "FooFile");
		assertEquals(fooList.get(0).fooFile.recordCount, Integer.valueOf(1));
	}

	public static class Foo {
		@Trim
		@Parsed(field = "UserId")
		@FixedWidth(padding = '0', from = 0, to = 10)
		private Long userId;

		private FooFile fooFile;
	}

	public static class FooFile {
		@Trim
		@Parsed(field = "FileName")
		@FixedWidth(from = 1, to = 11)
		private String name;

		@Trim
		@Parsed(field = "RecordCount")
		@FixedWidth(from = 11, to = 21)
		private Integer recordCount;
	}

	public static class FooProcessor extends BeanProcessor<Foo> {
		private FooFile currentFile;
		private final ArrayList<Foo> foos;

		public FooProcessor(ArrayList<Foo> list) {
			super(Foo.class);
			this.foos = list;
		}

		public void prepareForRecords(FooFile file) {
			this.currentFile = file;
			this.foos.ensureCapacity(this.foos.size() + file.recordCount);
		}

		@Override
		public void beanProcessed(Foo f, ParsingContext parsingContext) {
			f.fooFile = currentFile;
			this.foos.add(f);
		}
	}

	public static class FooFileProcessor extends BeanProcessor<FooFile> {
		private FooFile file;

		public FooFileProcessor() {
			super(FooFile.class);
		}

		@Override
		public void beanProcessed(FooFile f, ParsingContext parsingContext) {
			this.file = f;
		}
	}
}
