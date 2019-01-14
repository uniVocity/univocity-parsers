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
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.text.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/280
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_287 {

	public static final class Model {
		private String a;
		private int b;
		private String c;

		@Format(formats = "dd MMM yyyy")
		private Date d;

		private Model() {
		}

		public void setC(String c){
			this.c = c;
		}

		public String getC(){
			return c;
		}
	}

	@Test
	public void mapColumnNameToAttributeInCode() throws Exception {
		BeanListProcessor<Model> processor = new BeanListProcessor<Model>(Model.class);

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("a", "col1");
		mapping.put("b","col2");
		mapping.put("setC","col3");
		mapping.put("d","col4");

		processor.mapping = mapping;

		parseWithMapping(processor);
	}

	private void parseWithMapping(BeanListProcessor<Model> processor){
		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(processor);
		settings.getFormat().setLineSeparator("\n");

		new CsvParser(settings).parse(new StringReader("col1,col2,col3,col4\nval1,2,val3,12 Dec 2010"));

		Model instance = processor.getBeans().get(0);
		assertEquals(instance.a, "val1");
		assertEquals(instance.b, 2);
		assertEquals(instance.c, "val3");
		assertNotNull(instance.d);
		assertEquals(new SimpleDateFormat("yyyy MM dd", Locale.ENGLISH).format(instance.d), "2010 12 12");

	}

	@Test
	public void mapColumnIndexToAttributeInCode() throws Exception {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(0, "a");
		mapping.put(1, "b");
		mapping.put(2, "c");
	}
}
