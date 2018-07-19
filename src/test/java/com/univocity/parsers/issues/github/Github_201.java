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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_201 {

	public static class AB {
		@Parsed
		public String a;

		@Parsed
		public boolean b;

		public AB() {

		}

		public AB(String a, boolean b) {
			this.a = a;
			this.b = b;
		}
	}

	@Test
	public void testRoutineKeepResourcesOpen() throws Exception {

		File tmp = File.createTempFile("github_201", ".csv");
		//System.out.println(tmp.getAbsolutePath());
		FileWriter w = new FileWriter(tmp);

		List<AB> list = new ArrayList<AB>();
		list.add(new AB("1", true));
		list.add(new AB("2", false));


		CsvRoutines routines = new CsvRoutines();
		routines.getWriterSettings().setHeaderWritingEnabled(true);
		routines.setKeepResourcesOpen(true);

		routines.writeAll(list, AB.class, w);
		routines.writeAll(list, AB.class, w);
		w.close();

		list = routines.parseAll(AB.class, tmp);
		assertEquals(list.size(), 4);

	}

}
