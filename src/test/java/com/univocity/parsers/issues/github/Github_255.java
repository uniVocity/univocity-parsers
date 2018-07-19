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
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/255
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_255 {

	@Headers
	public static class Role {
		@Parsed(field = "Code", index = 0)
		private final String code;
		@Parsed(field = "Description", index = 2)
		private final String description;
		@Parsed(field = "Name", index = 1)
		private final String name;

		private Role(final String code, final String name, final String description) {
			this.code = code;
			this.description = description;
			this.name = name;
		}

		public static List<Role> examples() {
			return Arrays.asList(new Role("FIN", "Finance", null)
					, new Role("MKT", "Marketing", null)
					, new Role("SLS", "Sales", null));
		}
	}


	@Headers
	static class Employee {
		@Parsed(field = "Code", index = 0)
		private final String code;
		@Format(formats = "yyyy-MM-dd")
		@Parsed(applyDefaultConversion = false, field = "Exit date", index = 6)
		private final Date exitDate;
		@Parsed(field = "Full/ part-time", index = 3)
		private final String fullPart;
		@Format(formats = "yyyy-MM-dd")
		@Parsed(applyDefaultConversion = false, field = "Joining date", index = 5)
		private final Date joiningDate;
		@Parsed(field = "Name", index = 1)
		private final String name;
		@Parsed(field = "Title", index = 2)
		private final String title;
		@Parsed(field = "Type", index = 4)
		private final String type;

		private Employee(final String code
				, final String name
				, final String title
				, final String type
				, final String fullPart
				, final Date joiningDate
				, final Date exitDate) {
			this.code = code;
			this.exitDate = exitDate;
			this.fullPart = fullPart;
			this.joiningDate = joiningDate;
			this.name = name;
			this.title = title;
			this.type = type;
		}

		public static List<Employee> examples() {
			return Arrays.asList(
					new Employee("E001", "John Doe", "CEO", "Employee", "Full-time", new Date(111, 0, 1), null)
					, new Employee("E002", "Jane Doe", "CFO", "Employee", "Full-time", new Date(111, 0, 1), null)
					, new Employee("E003", "James Doe", "CMO", "Employee", "Full-time", new Date(111, 0, 1), null)
					, new Employee("E004", "Jennifer Doe", "CTO", "Employee", "Full-time", new Date(111, 0, 1), null)
					, new Employee("E005", "Jason Doe", "Analyst", "Employee", "Full-time", new Date(111, 0, 1), new Date(111, 11, 31))
					, new Employee("E006", "Joseph Doe", "Analyst", "Employee", "Full-time", new Date(111, 0, 1), null)
					, new Employee("C001", "Jimmy Doe", "Analyst", "Contractor", "Full-time", new Date(111, 0, 1), new Date(112, 5, 30))
					, new Employee("B001", "Jillian Doe", "Chairperson", "Board member", "Part-time", new Date(111, 0, 1), null)
			);
		}
	}

	private static <T> String write(final Class<T> type, final Iterable<T> records) {
		final CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(new BeanWriterProcessor<T>(type));

		StringWriter out = new StringWriter();
		final CsvWriter writer = new CsvWriter(out, settings);

		writer.processRecords(records);

		writer.close();

		return out.toString();
	}

	@Test
	public void testHeaderWriting() {
		String roles = write(Role.class, Role.examples());
		assertEquals(roles, "" +
				"Code,Name,Description\n" +
				"FIN,Finance,\n" +
				"MKT,Marketing,\n" +
				"SLS,Sales,\n");

		String employees = write(Employee.class, Employee.examples());
		assertEquals(employees, "" +
				"Code,Name,Title,Full/ part-time,Type,Joining date,Exit date\n" +
				"E001,John Doe,CEO,Full-time,Employee,2011-01-01,\n" +
				"E002,Jane Doe,CFO,Full-time,Employee,2011-01-01,\n" +
				"E003,James Doe,CMO,Full-time,Employee,2011-01-01,\n" +
				"E004,Jennifer Doe,CTO,Full-time,Employee,2011-01-01,\n" +
				"E005,Jason Doe,Analyst,Full-time,Employee,2011-01-01,2011-12-31\n" +
				"E006,Joseph Doe,Analyst,Full-time,Employee,2011-01-01,\n" +
				"C001,Jimmy Doe,Analyst,Full-time,Contractor,2011-01-01,2012-06-30\n" +
				"B001,Jillian Doe,Chairperson,Part-time,Board member,2011-01-01,\n");
	}
}
