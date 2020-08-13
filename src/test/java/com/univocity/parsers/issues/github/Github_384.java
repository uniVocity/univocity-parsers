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


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/384
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_384 {

	String inputA = "" +
			"id,date,description,amount\n" +
			"20108680,2019-03-07,Some description,9.18\n" +
			"20108680,2019-03-10,This's description,113.91\n" +
			"20108680,2019-03-14,\"Some description, with double quotes\",113.91";

	String inputB = "" +
			"id,date,description,amount\n" +
			"20108680,2019-03-07,Some description,9.18\n" +
			"20108680,2019-03-14,\"Some description, with double quotes\",113.91\n" +
			"20108680,2019-03-10,This's description,113.91";

	public static class MyPojo {
		private static final String DATE_FORMAT = "yyyy-MM-dd";

		@Parsed
		private Long id;
		@Parsed
		private String date;
		@Parsed
		private String description;
		@Parsed
		private Double amount;
	}

	@Test
	public void testQuoteAutoDetection() {
		CsvParserSettings settings = new CsvParserSettings();
//		settings.detectFormatAutomatically();

		new CsvRoutines(settings).parseAll(MyPojo.class, new StringReader(inputA));
		new CsvRoutines(settings).parseAll(MyPojo.class, new StringReader(inputB));

	}

}
