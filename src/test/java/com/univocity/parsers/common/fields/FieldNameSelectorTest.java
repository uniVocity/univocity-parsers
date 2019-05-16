/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
package com.univocity.parsers.common.fields;

import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class FieldNameSelectorTest {

	@Test
	public void getFieldsToExtract() {
		FieldNameSelector selector = new FieldNameSelector();
		selector.add("D", "A");

		int[] indexes = selector.getFieldIndexes(new String[]{"A", "B", "C", "D", "E", "F"});

		int[] expected = new int[]{3, 0};

		assertEquals(indexes, expected);
	}

	@Test
	public void getFieldsToExtract2() {
		String[] fields = new String[]{"SellerID", "Brand", "MPN", "SoldPrice", "Shipping", "TotalAmount", "Currency", "SoldPriceUSD", "ShippingUSD", "TotalAmountUSD", "SoldQuantity", "Condition", "Format", "SoldDate", "ProductRating", "UPC", "EAN", "ItemLocation", "Title", "PictureURL", "ListingURL"};
		String[] selection = new String[]{"Format", "PictureURL", "SellerID", "SoldDate", "ListingURL", "Currency", "Condition", "Title", "BidPrice", "SoldPrice", "SoldPriceUSD", "SystemCurrency", "SoldPriceDiscounted", "Shipping", "ShippingUSD", "TotalAmount", "TotalAmountUSD", "SoldQuantity", "ProductRating", "Brand", "EAN", "UPC", "MPN", "ItemLocation", "SellerID", "SellerFeedback", "StockQuantity", "eBayItemNumber", "PicURL1", "PicURL2", "PicURL3", "PicURL4", "PicURL5", "PicURL6", "PicURL7", "PicURL8", "PicURL9", "PicURL10"};
		FieldNameSelector selector = new FieldNameSelector();
		selector.add(selection);

		int[] indexes = selector.getFieldIndexes(fields);

		assertEquals(Arrays.toString(indexes), "[12, 19, 0, 13, 20, 6, 11, 18, -1, 3, 7, -1, -1, 4, 8, 5, 9, 10, 14, 1, 16, 15, 2, 17, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]");
	}
}
