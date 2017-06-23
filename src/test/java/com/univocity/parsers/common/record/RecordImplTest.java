/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.record;

import com.univocity.parsers.conversions.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class RecordImplTest {

	private Record record;

	@BeforeClass
	public void setup() {
		TsvParserSettings settings = new TsvParserSettings();
		settings.setHeaders("boolean,byte,short,int,long,bigint,float,double,bigdec,char,string,date,calendar".split(","));
		TsvParser parser = new TsvParser(settings);
		record = parser.parseRecord("Y	1	2		4	5	6.6	7.7	$8.888	B	blah	10/10/10	11/11/11");
		assertNotNull(record);

		RecordMetaData md = record.getMetaData();
		assertNotNull(md);

		md.setTypeOfColumns(BigInteger.class, E.bigint);
		md.convertFields(E.class, new DateConversion("dd/MM/yy")).add(E.date);

		md.setTypeOfColumns(Short.class, 2);
		md.setTypeOfColumns(float.class, 6);

		md.convertFields(new CalendarConversion("dd/MM/yy")).add("calendar");
	}

	enum E {
		bigint, bigdec, date, calendar
	}

	@Test
	public void fillEnumMap() {
		EnumMap<E, String> map = new EnumMap<E, String>(E.class);
		record.fillEnumMap(map, E.date, E.bigint);
		assertEquals(map.size(), 2);
		assertEquals(map.get(E.date), "10/10/10");
		assertEquals(map.get(E.bigint), "5");
	}

	@Test
	public void fillEnumObjectMap() {
		EnumMap<E, Object> map = new EnumMap<E, Object>(E.class);
		record.fillEnumObjectMap(map, E.date, E.bigint);
		assertEquals(map.size(), 2);
		assertEquals((Date) map.get(E.date), new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime());
		assertEquals((BigInteger) map.get(E.bigint), new BigInteger("5"));
	}

	@Test
	public void fillFieldMap() {
		Map<String, String> map = new TreeMap<String, String>();
		record.fillFieldMap(map, "short", "float");
		assertEquals(map.size(), 2);
		assertEquals(map.get("short"), "2");
		assertEquals(map.get("float"), "6.6");
	}

	@Test
	public void fillFieldObjectMap() {
		Map<String, Object> map = new TreeMap<String, Object>();
		record.fillFieldObjectMap(map, "short", "float");
		assertEquals(map.size(), 2);
		assertEquals((Short) map.get("short"), Short.valueOf((short) 2));
		assertEquals((Float) map.get("float"), (float) 6.6);
	}

	@Test
	public void fillIndexMap() {
		Map<Integer, String> map = new TreeMap<Integer, String>();
		record.fillIndexMap(map, 2, 6);
		assertEquals(map.size(), 2);
		assertEquals(map.get(2), "2");
		assertEquals(map.get(6), "6.6");
	}

	@Test
	public void fillIndexObjectMap() {
		Map<Integer, Object> map = new TreeMap<Integer, Object>();
		record.fillIndexObjectMap(map, 2, 6);
		assertEquals(map.size(), 2);
		assertEquals((Short) map.get(2), Short.valueOf((short) 2));
		assertEquals((Float) map.get(6), (float) 6.6);
	}

	@Test
	public void getBigDecimalStringStringString() {
		BigDecimal dec = record.getBigDecimal("bigdec", "$#0.00", "decimalSeparator=.");
		assertEquals(dec, new BigDecimal("8.888"));
	}

	@Test
	public void getBigDecimalEnumStringString() {
		BigDecimal dec = record.getBigDecimal(E.bigdec, "$#0.00", "decimalSeparator=.");
		assertEquals(dec, new BigDecimal("8.888"));
	}

	@Test
	public void getBigDecimalintStringString() {
		BigDecimal dec = record.getBigDecimal(8, "$#0.00", "decimalSeparator=.");
		assertEquals(dec, new BigDecimal("8.888"));
	}

	@Test
	public void getBigIntegerStringStringString() {
		BigInteger i = record.getBigInteger("bigint");
		assertEquals(i, new BigInteger("5"));
	}

	@Test
	public void getBigIntegerEnumStringString() {
		BigInteger i = record.getBigInteger(E.bigint);
		assertEquals(i, new BigInteger("5"));
	}

	@Test
	public void getBigIntegerintStringString() {
		BigInteger i = record.getBigInteger(5);
		assertEquals(i, new BigInteger("5"));
	}

	@Test
	public void getBoolean() {
		assertTrue(record.getBoolean("boolean", "Y", "N"));
		assertFalse(record.getBoolean("boolean", "N", "Y"));
	}

	@Test
	public void getByte() {
		assertEquals((byte) record.getByte("byte"), (byte) 1);
	}

	@Test
	public void getCalendar() {
		Calendar cal = record.getCalendar(12, "hh/mm/ss");//ha!
		assertEquals(cal.get(Calendar.HOUR), 11);
		assertEquals(cal.get(Calendar.MINUTE), 11);
		assertEquals(cal.get(Calendar.SECOND), 11);

		cal = record.getCalendar("calendar");//format defined in metaData's column type - see @BeforeClass method. 
		assertEquals(cal, new GregorianCalendar(2011, Calendar.NOVEMBER, 11));
	}

	@Test
	public void getChar() {
		assertEquals(record.getChar("char"), Character.valueOf('B'));
	}

	@Test
	public void getDate() {
		Date dt = record.getDate("date", "hh/mm/ss");//ha!

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		assertEquals(cal.get(Calendar.HOUR), 10);
		assertEquals(cal.get(Calendar.MINUTE), 10);
		assertEquals(cal.get(Calendar.SECOND), 10);

		dt = record.getDate(11);//format defined in metaData's column type - see @BeforeClass method.
		cal.setTime(dt);
		assertEquals(cal, new GregorianCalendar(2010, Calendar.OCTOBER, 10));
	}

	@Test
	public void getDouble() {
		assertEquals(record.getDouble("double"), Double.valueOf(7.7));
	}

	@Test
	public void getFloat() {
		assertEquals(record.getFloat("float"), Float.valueOf(6.6f));
	}

	@Test
	public void getInt() {
		assertEquals(record.getInt("int"), null);
	}

	@Test
	public void getLong() {
		assertEquals(record.getLong("long"), Long.valueOf(4L));
	}

	@Test
	public void getString() {
		assertEquals(record.getString("string"), "blah");
	}

	@Test
	public void getValue() {
		assertEquals(record.getValue(E.bigdec, String.class), "$8.888");
		assertEquals(record.getValue("int", Integer.class), null);
		assertEquals(record.getValue("int", Long.class), null);
		assertEquals(record.getValue("int", Long.valueOf(0L)), Long.valueOf(0L));
		assertEquals(record.getValue("int", Integer.valueOf(100)), Integer.valueOf(100));
	}

	@Test
	public void getValues() {
		assertEquals(record.getValues(E.bigdec, E.date), new String[]{"$8.888", "10/10/10"});
		assertEquals(record.getValues("bigdec", "date"), new String[]{"$8.888", "10/10/10"});
		assertEquals(record.getValues(8, 11), new String[]{"$8.888", "10/10/10"});
	}
}
