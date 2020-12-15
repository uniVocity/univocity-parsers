package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/427
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_432 {

	public static class Person {
		@Parsed
		private int id;

		@Parsed(defaultNullRead = "[empty]")
		private String name;

		@Nested
		private Address address;
	}

	public static class Address {
		@Parsed
		private String street;
		@Parsed
		private int streetNumber;
	}

	@Test
	public void testNestedWithMissingFields() {
		String data = "id,street,streetNumber\n"+
				"1,3rd,31\n"+
				"2,33th,32";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		BeanListProcessor<Person> processor = new BeanListProcessor<Person>(Person.class);
		settings.setProcessor(processor);
		CsvParser parser = new CsvParser(settings);
		parser.parse(new StringReader(data));
		List<Person> beans = processor.getBeans();

		assertEquals(1, beans.get(0).id);
		assertEquals(2, beans.get(1).id);
		assertEquals("3rd", beans.get(0).address.street);
		assertEquals("33th", beans.get(1).address.street);
		assertEquals(31, beans.get(0).address.streetNumber);
		assertEquals(32, beans.get(1).address.streetNumber);
		assertEquals("[empty]", beans.get(0).name);
		assertEquals("[empty]", beans.get(1).name);
	}
}