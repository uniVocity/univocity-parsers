package com.univocity.parsers.issues.github;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;
import org.testng.annotations.*;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;

public class Github_14 {

	private static final Conversion<Boolean, Double> boolenToDoubleConversion = new Conversion<Boolean, Double>() {
		@Override
		public Double execute(Boolean input) {
			if (input == null || input == Boolean.FALSE) {
				return 0.0;
			}
			return 1.0;
		}

		@Override
		public Boolean revert(Double input) {
			if (input == null || input == 0.0 || input.isNaN()) {
				return false;
			}
			return Boolean.TRUE;
		}
	};

	@DataProvider
	public Object[][] conversionProvider() {
		return new Object[][] { { true }, { false } };
	}

	@Test(dataProvider="conversionProvider")
	public void testConversion(boolean convertAll) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);

		ObjectColumnProcessor objectColumnProcessor = new ObjectColumnProcessor();
		if (convertAll) {
			objectColumnProcessor.convertAll(Conversions.toBoolean("T", "F"), boolenToDoubleConversion);
		} else {
			objectColumnProcessor.convertIndexes(Conversions.toBoolean("T", "F"), boolenToDoubleConversion).set(0, 1);
		}

		parserSettings.setRowProcessor(objectColumnProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("A,B\nT,F\nF,T"));

		Map<String, List<Object>> columnValues = objectColumnProcessor.getColumnValuesAsMapOfNames();
		assertEquals(columnValues.size(), 2);
		List<Object> values;
		values = columnValues.get("A");
		assertNotNull(values);
		assertEquals(values.size(), 2);
		assertEquals(Arrays.asList(1.0, 0.0), values);

		values = columnValues.get("B");
		assertNotNull(values);
		assertEquals(values.size(), 2);
		assertEquals(Arrays.asList(0.0, 1.0), values);
	}
}
