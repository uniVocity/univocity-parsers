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


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/272
 *
 *  * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Github_272 {

	String input = "CPF MOTORISTA (Obrigatório - APENAS NÚMEROS);PLACA (Obrigatório - APENAS NÚMEROS);DATA VIAGEM (Obrigatório - EX.: 28/10/2018); VALOR (Obrigatório) ;USINA (Obrigatório);FAZENDA (Obrigatório);RAIO EM KM (Obrigatório);TONELADA (Obrigatório)\n" +
			"333;mmm1234;21/08/2018; 5.000,50 ;Test;Test;28.98;18.98";

	@Test
	public void testDelimiterDetection() {
		final CsvParserSettings settings = new CsvParserSettings();
		settings.setDelimiterDetectionEnabled(true);
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setHeaderExtractionEnabled(true);

		final CsvParser parser = new CsvParser(settings);
		parser.beginParsing(new StringReader(input));

		System.out.println(parser.getDetectedFormat());

		final List<String[]> rows = parser.parseAll();
		assertEquals(1, rows.size());
		assertEquals(8, rows.get(0).length);
	}

}
