package com.univocity.parsers.issues.support;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

public class Ticket_14 {

	private static final String[] headers = new String[]{"SellerID", "Brand", "MPN", "SoldPrice", "Shipping", "TotalAmount", "Currency", "SoldPriceUSD", "ShippingUSD", "TotalAmountUSD", "SoldQuantity", "Condition", "Format", "SoldDate", "ProductRating", "UPC", "EAN", "ItemLocation", "Title", "PictureURL", "ListingURL"};
	private static final String[] selection = new String[]{"Format", "PictureURL", "SellerID", "SoldDate", "ListingURL", "Currency", "Condition", "Title", "BidPrice", "SoldPrice", "SoldPriceUSD", "SystemCurrency", "SoldPriceDiscounted", "Shipping", "ShippingUSD", "TotalAmount", "TotalAmountUSD", "SoldQuantity", "ProductRating", "Brand", "EAN", "UPC", "MPN", "ItemLocation", "SellerID", "SellerFeedback", "StockQuantity", "eBayItemNumber", "PicURL1", "PicURL2", "PicURL3", "PicURL4", "PicURL5", "PicURL6", "PicURL7", "PicURL8", "PicURL9", "PicURL10"};
	private static final String[] values = new String[]{null, "https://i.ebayimg.com/thumbs/images/g/LOAAAOSwKvJauWjF/s-l225.jpg", "studiony", null, "https://www.ebay.com.au/itm/DIESEL-DZ7315-GUNMETAL-MENS-MR-DADDY-2-0-57MM-CHRONOGRAPH-WATCH-NEW/202272286020?hash=item2f185e2544:g:LOAAAOSwKvJauWjF", "USD", "New with tags", "DIESEL DZ7315 GUNMETAL MENS MR DADDY 2.0 57MM CHRONOGRAPH WATCH NEW", null, "137.00", "137.00", "AU $", "0", "0", "0", "137.00", "137.00", "21", "54", null, null, null, null, "Hong Kong; Hong Kong", "studiony", "514", "6", "202272286020", "https://i.ebayimg.com/images/g/LOAAAOSwKvJauWjF/s-l500.jpg", "https://i.ebayimg.com/images/g/7VwAAOSwKNhauWjG/s-l500.jpg", "https://i.ebayimg.com/images/g/RUAAAOSw-NFauWjJ/s-l500.jpg", "https://i.ebayimg.com/images/g/zCsAAOSw90xauWjK/s-l500.jpg", null, null, null, null, null, null};

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

	@Test(dataProvider = "provider")
	public void testWritingWithSelection(boolean reorder) {
		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, getSettings(reorder));
		writer.writeRow(values);
		writer.close();

		String[] written = out.toString().split(",");

		StringBuilder expected = new StringBuilder();
		StringBuilder actual = new StringBuilder();
		for (int i = 0; i < headers.length; i++) {
			actual.append(headers[i]).append(" -> ").append(written[i]).append('\n');
			expected.append(headers[i]).append(" -> ").append(values[i]).append('\n');
		}

		assertEquals(actual.toString(), expected.toString());
	}

}
