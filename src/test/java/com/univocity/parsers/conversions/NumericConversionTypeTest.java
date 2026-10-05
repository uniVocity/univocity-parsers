package com.univocity.parsers.conversions;

import java.math.BigInteger;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class NumericConversionTypeTest {
	@Test
	public void convertsToRequestedIntegerType() {
		NumericConversion<Integer> conversion = Conversions.formatToNumber(Integer.class, "0");
		Integer value = conversion.execute("123");
		assertEquals(value, Integer.valueOf(123));
		assertEquals(conversion.getNumberType(), Integer.class);
		assertNull(conversion.execute(null));
	}

	@Test
	public void convertsToRequestedDoubleType() {
		Double value = Conversions.formatToNumber(Double.class, "0").execute("123");
		assertEquals(value, Double.valueOf(123.0));
	}

	@Test
	public void convertsToRequestedBigIntegerType() {
		BigInteger value = Conversions.formatToNumber(BigInteger.class, "0").execute("123");
		assertEquals(value, BigInteger.valueOf(123));
	}

	@Test
	public void untypedConversionKeepsDefaultNumberType() {
		Number value = Conversions.formatToNumber("0").execute("123");
		assertEquals(value, Long.valueOf(123));
	}
}
