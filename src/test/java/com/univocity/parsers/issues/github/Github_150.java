/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/150
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_150 {

	public static class User {
		@Parsed(field = {"email", "contact", "e-mail"})
		private String email;

		@Parsed(field = {"phone", "ph", "mobile"})
		private String phone;
	}

	@DataProvider
	public Object[][] headerProvider() {
		return new Object[][]{
				{new String[]{"email", "ph"}},
				{new String[]{"contact", "mobile"}},
				{new String[]{"e-mail", "phone"}},
				{new String[]{"", "mobile"}},
		};
	}


	@Test(dataProvider = "headerProvider")
	public void readMultipleInputsIntoSameBean(String[] headers) {
		String headerString = headers[0] + "," + headers[1];
		String line = "blah@etc.com,040012333\nfoo@bar.net,99311-322";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderExtractionEnabled(true);

		List<User> users = new CsvRoutines(settings).parseAll(User.class, new StringReader(headerString + "\n" + line));

		assertEquals(users.get(0).phone, "040012333");
		assertEquals(users.get(1).phone, "99311-322");

		if (!headers[0].isEmpty()) {
			assertEquals(users.get(0).email, "blah@etc.com");
			assertEquals(users.get(1).email, "foo@bar.net");
		}
	}

	@Test(dataProvider = "headerProvider")
	public void writeSameBeanTypeIntoMultipleOutputs(String[] headers) {

		List<User> users = new ArrayList<User>();
		User u1 = new User();
		u1.email = "blah@etc.com";
		u1.phone = "040012333";
		users.add(u1);

		User u2 = new User();
		u2.email = "foo@bar.net";
		u2.phone = "99311-322";
		users.add(u2);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaders(headers);
		settings.setHeaderWritingEnabled(true);

		StringWriter out = new StringWriter();
		new CsvRoutines(settings).writeAll(users, User.class, out);

		String headerString = headers[0] + "," + headers[1];
		String lines;
		if (headers[0].isEmpty()) {
			lines = ",040012333\n,99311-322\n";
		} else {
			lines = "blah@etc.com,040012333\nfoo@bar.net,99311-322\n";
		}

		assertEquals(out.toString(), headerString + "\n" + lines);
	}
}
