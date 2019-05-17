package com.univocity.parsers.issues.support;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

public class Ticket_14 {

	private static final String[] headers = new String[]{"SellerID", "Brand", "MPN", "SoldPrice", "Shipping", "TotalAmount", "Currency", "SoldPriceUSD", "ShippingUSD", "TotalAmountUSD", "SoldQuantity", "Condition", "Format", "SoldDate", "ProductRating", "UPC", "EAN", "ItemLocation", "Title", "PictureURL", "ListingURL"};
	private static final String[] selection = new String[]{"Format", "PictureURL"                                                       , "SellerID", "SoldDate", "ListingURL"                                                                                                                                         , "Currency", "Condition"    , "Title"                                                              , "BidPrice", "SoldPrice", "SoldPriceUSD", "SystemCurrency", "SoldPriceDiscounted", "Shipping", "ShippingUSD", "TotalAmount", "TotalAmountUSD", "SoldQuantity", "ProductRating", "Brand", "EAN", "UPC", "MPN", "ItemLocation"        , "SellerID", "SellerFeedback", "StockQuantity", "eBayItemNumber", "PicURL1",                                                    "PicURL2",                                                    "PicURL3",                                                    "PicURL4",                                                   "PicURL5", "PicURL6", "PicURL7", "PicURL8", "PicURL9", "PicURL10"};
	private static final String[] values = new String[]   {null    , "https://i.ebayimg.com/thumbs/images/g/LOAAAOSwKvJauWjF/s-l225.jpg", "studiony", null      , "https://www.ebay.com.au/itm/DIESEL-DZ7315-GUNMETAL-MENS-MR-DADDY-2-0-57MM-CHRONOGRAPH-WATCH-NEW/202272286020?hash=item2f185e2544:g:LOAAAOSwKvJauWjF", "USD"     , "New with tags", "DIESEL DZ7315 GUNMETAL MENS MR DADDY 2.0 57MM CHRONOGRAPH WATCH NEW", null,       "137.00"   , "137.00"      , "AU $"          , "0"                  , "0"       , "0"          , "137.00"     , "137.00"        , "21",           "54",             null,   null,  null,  null , "Hong Kong; Hong Kong", "studiony", "514"           , "6"            , "202272286020"  , "https://i.ebayimg.com/images/g/LOAAAOSwKvJauWjF/s-l500.jpg", "https://i.ebayimg.com/images/g/7VwAAOSwKNhauWjG/s-l500.jpg", "https://i.ebayimg.com/images/g/RUAAAOSw-NFauWjJ/s-l500.jpg", "https://i.ebayimg.com/images/g/zCsAAOSw90xauWjK/s-l500.jpg", null, null, null, null, null, null};
	                                                      // 0        1                                                                   2           3           4                                                                                                                                                       5           6               7                                                                      8            9            10              11                12                     13          14            15              16                17             18                19       20    21      22     23                     24           25                26              27                 28                                                            29                                                            30                                                            31                                                           32    33    34    35    36    37

	private CsvWriterSettings getSettings(boolean reorder) {
		CsvWriterSettings writerSettings = Csv.writeExcel();
		writerSettings.setHeaders(headers);
		writerSettings.selectFields(selection);
		writerSettings.setNullValue("null");
		writerSettings.setHeaderWritingEnabled(false);
		writerSettings.setColumnReorderingEnabled(reorder);

		return writerSettings;
	}

	@DataProvider
	public Object[][] provider() {
		return new Object[][]{
				{true},
				{false},
		};
	}

	@Test(dataProvider = "provider")
	public void testIndexOutOfBoundsErrorWritingSecondRow(boolean reorder) {
		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, getSettings(reorder));
		writer.writeRow(values);
		writer.writeRow(values);
		writer.close();

		//no error, all good.
	}

	private int indexOf(String[] original, String selected){
		for(int i = 0; i < original.length; i++){
			if(original[i].equals(selected)){
				return i;
			}
		}
		return -1;
	}

	@Test(dataProvider = "provider")
	public void testWritingWithSelection(boolean reorder) {
		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, getSettings(reorder));
		writer.writeRow(values);
		writer.close();

		String[] written = out.toString().trim().split(",");

		StringBuilder expected = new StringBuilder();
		StringBuilder actual = new StringBuilder();
		for (int i = 0; i < headers.length; i++) {
			actual.append(headers[i]).append(" -> ").append(written[i]).append('\n');

			int index = indexOf(selection, headers[i]);
			String value = values[index];

			expected.append(headers[i]).append(" -> ").append(value).append('\n');
		}

		assertEquals(actual.toString(), expected.toString());
	}

}
