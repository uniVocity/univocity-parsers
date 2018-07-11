/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/253
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_253 {

	public static class AA {
		@NullString(nulls = { "", " " })
		@Parsed(
				field = "r1",
				defaultNullRead = "NULL")
		private String r1;

		@NullString(nulls = { "", " " })
		@Parsed(
				field = "r2",
				defaultNullRead = "NULL")
		private String r2;
	}

	@Test
	public void testBean(){
		CsvRoutines r = new CsvRoutines();
		r.getParserSettings().setNullValue("");

		for(AA o : r.iterate(AA.class, new StringReader("r1,r2\nref1,\nref1"))){
			assertEquals(o.r2, "NULL");
		}
	}

}