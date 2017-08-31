package com.univocity.parsers.csv;

import com.univocity.parsers.common.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class UnescapedQuoteHandlingTest {

	static final String INPUT_1 = "a,\"b c\" d,\"e,f\",\",g\","; // a,"b c" d,"e,f",",g",
	static final String INPUT_2 = "a,\"b c\" d\n\"e,f\",\",g\","; // a,"b c" d,"e,f",",g",

	@DataProvider
	private Object[][] inputProvider() {
		return new Object[][]{
				//INPUT 1
				{UnescapedQuoteHandling.SKIP_VALUE, INPUT_1, new String[][]{
						{"a", null, "e,f", ",g", null}
				}},

				{UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, INPUT_1, new String[][]{
						{"a", "b c\" d,\"e,f", ",g", null}
				}},
				{UnescapedQuoteHandling.STOP_AT_DELIMITER, INPUT_1, new String[][]{
						{"a", "\"b c\" d", "e,f", ",g", null}
				}},
				{UnescapedQuoteHandling.RAISE_ERROR, INPUT_1, null},

				//INPUT 2
				{UnescapedQuoteHandling.SKIP_VALUE, INPUT_2, new String[][]{
						{"a", null},
						{"e,f", ",g", null}
				}},
				{UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, INPUT_2, new String[][]{
						{"a", "b c\" d\n\"e,f", ",g", null}
				}},
				{UnescapedQuoteHandling.STOP_AT_DELIMITER, INPUT_2, new String[][]{
						{"a", "\"b c\" d"},
						{"e,f", ",g", null}
				}},
				{UnescapedQuoteHandling.RAISE_ERROR, INPUT_2, null},
		};
	}

	@Test(dataProvider = "inputProvider")
	public void testQuoteHandling(UnescapedQuoteHandling setting, String input, String[][] expectedOutput) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setUnescapedQuoteHandling(setting);
		settings.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(settings);

		try {
			String[][] values = parser.parseAll(new StringReader(input)).toArray(new String[0][]);
			TestUtils.assertLinesAreEqual(values, expectedOutput);
			assertNotEquals(setting, UnescapedQuoteHandling.RAISE_ERROR);

		} catch (TextParsingException e) {
			assertEquals(setting, UnescapedQuoteHandling.RAISE_ERROR);
		}
	}

	@DataProvider
	public Object[][] config() {
		return new Object[][]{
				{false, UnescapedQuoteHandling.STOP_AT_DELIMITER, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{false, UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, "INCOME\".\"Taxable", "EXPENSES\".\"TotalExpenses", "EXPENSES\".\"Exceptional"},
				{false, UnescapedQuoteHandling.SKIP_VALUE, null, null, null},
				{true, UnescapedQuoteHandling.STOP_AT_DELIMITER, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{true, UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{true, UnescapedQuoteHandling.SKIP_VALUE, null, null, null},
		};
	}

	@Test(dataProvider = "config")
	public void testWithKeepQuotes(boolean keepQuotes, UnescapedQuoteHandling handling, String first, String second, String third) {
		String input = "" +
				"PAL : PAL : NF : \"INCOME\".\"Taxable\"\n" +
				"PAL : PAL : NF : \"EXPENSES\".\"TotalExpenses\"\n" +
				"PAL : PAL : NF : \"EXPENSES\".\"Exceptional\"";

		CsvParserSettings settings = new CsvParserSettings();
		settings.setKeepQuotes(keepQuotes);
		settings.setUnescapedQuoteHandling(handling);

		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setDelimiter(':');

		CsvParser parser = new CsvParser(settings);
		List<String[]> rows = parser.parseAll(new StringReader(input));
		assertEquals(rows.size(), 3);
		assertEquals(rows.get(0)[3], first);
		assertEquals(rows.get(1)[3], second);
		assertEquals(rows.get(2)[3], third);

	}
}