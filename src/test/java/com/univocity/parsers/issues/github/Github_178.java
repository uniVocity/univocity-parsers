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

import com.univocity.parsers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/178
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_178 extends ParserTestCase {


	@DataProvider
	public Object[][] inputs() {
		return new Object[][]{
				{'|', "a|b|c\nd|e|f"},
				{';', "a;b;c\nd;e;f"},
		};
	}

	@Test(dataProvider = "inputs")
	public void testDetectionOnInputWithoutExplicitCharset(char delimiter, String input) throws Exception {
		CsvParserSettings settings = new CsvParserSettings();

		settings.setDelimiterDetectionEnabled(true);
		settings.setQuoteDetectionEnabled(true);

		CsvParser parser = new CsvParser(settings);
		parser.parseAll(new ByteArrayInputStream(input.getBytes()));
		assertEquals(parser.getDetectedFormat().getDelimiter(), delimiter);

	}

	@Test
	public void testAnotherInput() {
		String input = "" +
				"Matricule;Nom;Prenoms;Classe;Date de naissance;Date d'inscription;Tel\n" +
				" 'A12075';Mako;yao;6eme1;'01/02/2007';15/10/2015;'07108954'\n" +
				"A12076; 'Mako';yao;6eme1;01/02/2007;15/10/2015;07108954\n" +
				"'A12087';Mako;yao;6eme1;01/02/2007;15/10/2015;07108954";


		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setDelimiterDetectionEnabled(true);
		parserSettings.setQuoteDetectionEnabled(true);

		RowListProcessor rowProcessor = new RowListProcessor();
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		CsvParser parser = new CsvParser(parserSettings);

		parser.parseAll(new StringReader(input));
		List<String[]> lignerslt = rowProcessor.getRows();

		assertEquals(lignerslt.size(), 3);
		assertEquals(Arrays.toString(lignerslt.get(0)), "[A12075, Mako, yao, 6eme1, 01/02/2007, 15/10/2015, 07108954]");
		assertEquals(Arrays.toString(lignerslt.get(1)), "[A12076, Mako, yao, 6eme1, 01/02/2007, 15/10/2015, 07108954]");
		assertEquals(Arrays.toString(lignerslt.get(2)), "[A12087, Mako, yao, 6eme1, 01/02/2007, 15/10/2015, 07108954]");
	}
}
