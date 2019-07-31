/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/337
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_337 {

	@DataProvider
	public Object[][] maxProvider() {
		return new Object[][]{
				{-1},
				{1000},
		};
	}

	@Test(dataProvider = "maxProvider")
	public void testInconsistentParsing(int max) {

		CsvParserSettings settings = new CsvParserSettings();
		// we handle headers by ourselves
		settings.setHeaderExtractionEnabled(false);
		settings.setReadInputOnSeparateThread(false);
		settings.setInputBufferSize(1024 * 1024);
		// Assume 1st line EOL indicates what the others will have as well
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.RAISE_ERROR);
		settings.setMaxCharsPerColumn(max);
		CsvFormat format = settings.getFormat();
		format.setDelimiter(',');
		format.setCharToEscapeQuoteEscaping('\\');

		String input = "18/01/2015,\"c_Pg,%W\\\",Ci\\tHpSDgA,\"!DLBpRjdV,\",306,5,!MuhlLqK,SPC_nTA%uZG$,SSC1_Vy\\K\\HEw,SSC2_ktmaDk!b,SSC3_#pbNlkTf,SSC4_a@J\\%dDL,PC_UfKoRZsI,PSC2_oHyn\\hMn,\"PSC3_\\QvO#,iy\",PSC4_x_KwV\\Z?,PSC5_!W\\ZI#l!,61.24,4,5,7,3,False\n";
		String[] result = new CsvParser(settings).parseLine(input);
		assertEquals(result.length, 23);

		assertEquals(result[0], "18/01/2015");
		assertEquals(result[1], "c_Pg,%W\\");
		assertEquals(result[2], "Ci\\tHpSDgA");
		assertEquals(result[3], "!DLBpRjdV,");
		assertEquals(result[4], "306");
		assertEquals(result[5], "5");
		assertEquals(result[6], "!MuhlLqK");
		assertEquals(result[7], "SPC_nTA%uZG$");
		assertEquals(result[8], "SSC1_Vy\\K\\HEw");
		assertEquals(result[9], "SSC2_ktmaDk!b");
		assertEquals(result[10], "SSC3_#pbNlkTf");
		assertEquals(result[11], "SSC4_a@J\\%dDL");
		assertEquals(result[12], "PC_UfKoRZsI");
		assertEquals(result[13], "PSC2_oHyn\\hMn");
		assertEquals(result[14], "PSC3_\\QvO#,iy");
		assertEquals(result[15], "PSC4_x_KwV\\Z?");
		assertEquals(result[16], "PSC5_!W\\ZI#l!");
		assertEquals(result[17], "61.24");
		assertEquals(result[18], "4");
		assertEquals(result[19], "5");
		assertEquals(result[20], "7");
		assertEquals(result[21], "3");
		assertEquals(result[22], "False");
	}
}
