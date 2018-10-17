package com.univocity.parsers.issues.support;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_13 {

	public static class TestRecord {
		@Parsed
		@FixedWidth(from = 0, to = 2)
		String type;

		@Parsed
		@FixedWidth(from = 2, to = 6)
		String code;

		@Parsed
		@FixedWidth(from = 6, to = 46)
		String identifier;

		@Parsed
		@FixedWidth(from = 46, to = 51, keepPadding = true)
		String currency;

		@Parsed
		@FixedWidth(from = 51, to = 59)
		String first_date;

		@Parsed
		@FixedWidth(from = 59, to = 67)
		String second_date;

		@Parsed
		@FixedWidth(from = 67, to = 84)
		String amount;

		@Parsed
		@FixedWidth(from = 84, to = 85)
		String amount_sign;

	}

	static final String content = "" +
			"04123012300104241233350010                    EUR  201806182018061800000000012345.18\n" +
			"  123012300104241233350010                    EUR  201806182018062200000000001235.18-\n";

	@Test
	public void shouldParseFile() {
		BeanListProcessor<TestRecord> rowProcessor = new BeanListProcessor<TestRecord>(TestRecord.class);
		FixedWidthParserSettings settings = new FixedWidthParserSettings();
		settings.setProcessor(rowProcessor);
		settings.getFormat().setLineSeparator("\n");
		settings.setRecordEndsOnNewline(true);

		FixedWidthParser parser = new FixedWidthParser(settings);

		parser.parse(new StringReader(content));

		final List<TestRecord> testRecords = rowProcessor.getBeans();

		final TestRecord firstRec = testRecords.get(0);
		assertEquals(firstRec.type, "04");
		assertEquals(firstRec.identifier, "12300104241233350010");
		assertEquals(firstRec.code, "1230");
		assertEquals(firstRec.amount_sign, null);
		assertEquals(firstRec.currency, "EUR  ");

	}

}
