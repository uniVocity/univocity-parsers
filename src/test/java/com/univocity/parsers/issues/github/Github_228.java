/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/228
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_228 {


	@Test
	public void testLastNullValueInQuotedInput() {
		String input = "\"A\",\n";

		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(s);
		String[] row = parser.parseAll(new StringReader(input)).get(0);

		assertEquals(row.length, 2);
		assertEquals(row[0], "A");
		assertEquals(row[1], null);
	}

	@Test
	public void testLastNullValueInQuotedInput2() {
		String input = "" +
				"\"Week\",\"Product Name\",\"SKU\",\"Stores Identifier\",\"Sales\",\"Sales in units\",\"Stores Name\",\"Store Category1\",\"Product Category1\",\"Unit price in store\",\"OOS\",\"Seasonality index\",\"Inventory\",\"Is distribution center\",\"DAYS IN PROMOTION\"\n" +
				"\"03/01/2015\",\"Product_name_qEub_7\",\"AMlNZDESIc\",\"ADutmUZqHJ\",\"503.93\",\"7\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_sruauesZ\",\"71.99\",\"2\",\"8\",\"4\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_kZdA_24\",\"AORZXUgfmh\",\"ADutmUZqHJ\",\"201.3\",\"3\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pmnGYCuN\",\"67.1\",\"3\",\"7\",\"2\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_mnJm_33\",\"DOqCPNzeQj\",\"ADutmUZqHJ\",\"1244.49\",\"13\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"95.73\",\"3\",\"6\",\"1\",\"False\",\"2\"\n" +
				"\"03/01/2015\",\"Product_name_PyWZ_54\",\"FITukqPSeW\",\"ADutmUZqHJ\",\"0\",\"0\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"48.14\",\"6\",\"5\",\"0\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_ZmBj_30\",\"FInNjGkAho\",\"ADutmUZqHJ\",\"678.23\",\"7\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"96.89\",\"4\",\"8\",\"0\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_nHEH_57\",\"FWKAjXyuhJ\",\"ADutmUZqHJ\",\"874.2\",\"12\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"72.85\",\"3\",\"1\",\"1\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_LKCb_46\",\"FZIftejbPF\",\"ADutmUZqHJ\",\"637.52\",\"13\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"49.04\",\"0\",\"8\",\"1\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_WWQD_61\",\"GhVkBbzUOC\",\"ADutmUZqHJ\",\"1113\",\"12\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"92.75\",\"5\",\"1\",\"0\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_TjNQ_78\",\"LkrVOFZrQO\",\"ADutmUZqHJ\",\"76.05\",\"1\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"76.05\",\"6\",\"4\",\"1\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_bCJF_5\",\"MWboMMDaUw\",\"ADutmUZqHJ\",\"1020.36\",\"12\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"85.03\",\"3\",\"3\",\"2\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_hvex_80\",\"NFfEozqUvH\",\"ADutmUZqHJ\",\"111.15\",\"3\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"37.05\",\"6\",\"3\",\"1\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_vNNf_11\",\"PfNtOSRqmp\",\"ADutmUZqHJ\",\"761.7\",\"10\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pmnGYCuN\",\"76.17\",\"2\",\"2\",\"4\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_ApkG_71\",\"QIQzmzZruT\",\"ADutmUZqHJ\",\"294.57\",\"3\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"98.19\",\"5\",\"3\",\"2\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_XSne_10\",\"RBcYqwYjKs\",\"ADutmUZqHJ\",\"656.2\",\"10\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"65.62\",\"3\",\"3\",\"2\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_xTva_34\",\"RvPxKdkmPz\",\"ADutmUZqHJ\",\"253.26\",\"3\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"84.42\",\"1\",\"7\",\"4\",\"False\",\"2\"\n" +
				"\"03/01/2015\",\"Product_name_ARjp_3\",\"SiqRMAxWDy\",\"ADutmUZqHJ\",\"1115.01\",\"13\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"85.77\",\"1\",\"7\",\"4\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_VEii_40\",\"SsuInIaWhQ\",\"ADutmUZqHJ\",\"491.4\",\"12\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_JHYpEccW\",\"40.95\",\"6\",\"4\",\"1\",\"False\",\n" +
				"\"03/01/2015\",\"Product_name_NMeu_69\",\"UPYEbXOfrA\",\"ADutmUZqHJ\",\"272.52\",\"3\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pWYTAtvJ\",\"90.84\",\"6\",\"5\",\"1\",\"False\",\"2\"\n" +
				"\"03/01/2015\",\"Product_name_UnoM_4\",\"WfudKLTPQP\",\"ADutmUZqHJ\",\"154.2\",\"6\",\"Store_name_HvZQ_52\",\"SC1_ORrJivUv\",\"PC1_pmnGYCuN\",\"25.7\",\"4\",\"8\",\"4\",\"False\",";

		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(s);
		List<String[]> rows = parser.parseAll(new StringReader(input));

		assertEquals(rows.size(), 20);
		for(String[] row : rows){
			assertEquals(row.length, 15);
		}
	}
}
