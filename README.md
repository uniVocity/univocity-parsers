![thumbnail](./images/uniVocity-parsers.png)

Welcome to uniVocity-parsers
============================

uniVocity-parsers is a collection of extremely fast and reliable parsers for Java. It provides a consistent interface for handling different file formats,
and a solid framework for the development of new parsers.

### Table of contents ###

 * [Introduction](#introduction)

  * [Parsers](#parsers)

  * [Installation](#installation)

  * [Background](#background)

 * [Examples](#examples)

  * [Reading CSV](#reading-csv)

  * [To read all rows of a CSV (the quick and easy way)](#to-read-all-rows-of-a-csv-the-quick-and-easy-way)

  * [To read all rows of a CSV (iterator-style)](#to-read-all-rows-of-a-csv-iterator-style)

  * [Escaping quote escape characters](#escaping-quote-escape-characters)

  * [Read all rows of a CSV (the powerful version)](#read-all-rows-of-a-csv-the-powerful-version)

  * [Using annotations to map your java beans](#using-annotations-to-map-your-java-beans)

  * [Using your own conversions in annotations](#using-your-own-conversions-in-annotations)

  * [Reading master-detail style files](#reading-master-detail-style-files)

  * [Parsing fixed-width files](#parsing-fixed-width-files)

  * [Parsing TSV files](#parsing-tsv-files)

  * [Column selection](#column-selection)

 * [Reading columns instead of rows](#reading-columns-instead-of-rows)

  * [Parsing columns from a CSV file](#parsing-columns-from-a-csv-file)

  * [Using the batched column processor in a Fixed-With input](#using-the-batched-column-processor-in-a-fixed-with-input)

  * [Reading columns from a TSV while converting the parsed content to Objects](#reading-columns-from-a-tsv-while-converting-the-parsed-content-to-objects)

  * [Processing rows in parallel](#processing-rows-in-parallel)

  * [Parsing individual Strings](#parsing-individual-strings)

 * [Settings](#settings)

  * [Fixed-width settings](#fixed-width-settings)

  * [Format Settings](#format-settings)

  * [CSV format](#csv-format)

  * [Fixed width format](#fixed-width-format)

  * [TSV format](#tsv-format)

 * [Writing](#writing)

  * [Quick and simple CSV writing example](#quick-and-simple-csv-writing-example)

  * [TSV writing example](#tsv-writing-example)

  * [Writing row by row, with comments](#writing-row-by-row-with-comments)

  * [Writing with column selection](#writing-with-column-selection)

  * [Writing with value conversions (using ObjectRowWriterProcessor)](#writing-with-value-conversions-using-objectrowwriterprocessor)

  * [Writing annotated java beans](#writing-annotated-java-beans)

  * [Writing value by value](#writing-value-by-value)

 

## Introduction ##

The project was started and coded by [uniVocity Software](http://www.univocity.com), an Australian company that develops 
[uniVocity](http://www.univocity.com), a commercial data integration API for Java.

It soon became apparent that many parsers out there didn't provide enough flexibility, throughput or reliability for massive and diverse (a nice word for messy) inputs.
Another inconvenience was the difficulty in extending these parsers and dealing with a different beast for each format.          

We decided to then build our own architecture for parsing text files from the ground up.
The main goal of this architecture is to provide maximum performance and flexibility while making it easy for anyone to create new parsers.

### Parsers ###
uniVocity-parsers currently provides parsers for:

- CSV files (it's the fastest CSV parser for Java you can find)

- Fixed-width files

- TSV files

We will introduce more parsers over time. Note many delimiter-separated formats, such as pipe-separated, are subsets of CSV and our CSV parser should handle them.
We are planning to introduce parsers for this and other specific formats to uniVocity-parsers later on.
Please let us know what you need the most by sending and e-mail to `parsers@univocity.com`.
We will introduce parsers for formats that are of public interest.      
 
We also documented every single class for you, so you can try to create your own parsers for your own particular purposes. 
We will help anyone building their own parsers, and offer commercial support for all parsers included in the API (send us an e-mail to `support@univocity.com`, 
a dedicated team of experts are ready to assist you).

### Installation ###


Just download the jar file from [here](http://oss.sonatype.org/content/repositories/releases/com/univocity/univocity-parsers/2.6.3/univocity-parsers-2.6.3.jar).

Or, if you use maven, simply add the following to your `pom.xml`

```xml

...
<dependency>
	<groupId>com.univocity</groupId>
	<artifactId>univocity-parsers</artifactId>
	<version>2.6.3</version>
	<type>jar</type>
</dependency>
...

```

### Background ###
uniVocity-parsers have the following functional requirements:

1. Support parsing and writing of text files in tabular format, especially:

	1.1 CSV files 
	
	1.2 Fixed-width files
	
	1.3 TSV files
	
2. Handle common non-standard functions such as

	2.1 File comments
	
	2.2 Partial reads
	
	2.3 Record skipping
	
3. Column selection

4. Annotation based mapping with data conversions

5. Handle edge cases such as multi-line fields and portable newlines  

6. Process the input in parallel.

And these non-functional requirements:

1. Be fast and flexible.

1. Have no external dependencies to existing libraries.

2. Be simple to use.

3. Provide a consistent API for different parsers.

4. Be flexible and heavily configurable.

5. Be extremely fast and memory efficient - yes, we micro optimize.  

6. Provide an extensible architecture: You should be able to write your own parser using ~200 lines of code and have all of the above for free.


## Examples ##

### Reading CSV ###

In the following examples, the [example.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/example.csv) file will be used as the input. It is not as simple as you might think. 
We've seen some known CSV parsers being unable to read this one correctly:


```

	
# This example was extracted from Wikipedia (en.wikipedia.org/wiki/Comma-separated_values)
#
# 2 double quotes ("") are used as the escape sequence for quoted fields, as per the RFC4180 standard
#  

Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
   
# Look, a multi line value. And blank rows around it!
     
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
,,"Venture ""Extended Edition""","",4900.00



```

All parsers work with an instance of `java.io.Reader`, so you will see calls such as `getReader("/examples/example.csv")` everywhere. This is just a helper method we use to build the examples ([source code here](https://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples)):
```
	public Reader getReader(String relativePath) {
		...
		return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
		...
	}
	
```

So let's get started!

#### To read all rows of a CSV (the quick and easy way) ####


```java

	
	
	CsvParserSettings settings = new CsvParserSettings();
	//the file used in the example uses '\n' as the line separator sequence.
	//the line separator sequence is defined here to ensure systems such as MacOS and Windows
	//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	settings.getFormat().setLineSeparator("\n");
	
	// creates a CSV parser
	CsvParser parser = new CsvParser(settings);
	
	// parses all rows in one go.
	List<String[]> allRows = parser.parseAll(getReader("/examples/example.csv"));
	
	


```

The output will be:


```

	1 [Year, Make, Model, Description, Price]
	-----------------------
	2 [1997, Ford, E350, ac, abs, moon, 3000.00]
	-----------------------
	3 [1999, Chevy, Venture "Extended Edition", null, 4900.00]
	-----------------------
	4 [1996, Jeep, Grand Cherokee, MUST SELL!
	air, moon roof, loaded, 4799.00]
	-----------------------
	5 [1999, Chevy, Venture "Extended Edition, Very Large", null, 5000.00]
	-----------------------
	6 [null, null, Venture "Extended Edition", null, 4900.00]
	-----------------------


```

#### To read all rows of a CSV (iterator-style) ####


```java

	
	
	// creates a CSV parser
	CsvParser parser = new CsvParser(settings);
	
	// call beginParsing to read records one by one, iterator-style.
	parser.beginParsing(getReader("/examples/example.csv"));
	
	String[] row;
	while ((row = parser.parseNext()) != null) {
		println(out, Arrays.toString(row));
	}
	
	// The resources are closed automatically when the end of the input is reached,
	// or when an error happens, but you can call stopParsing() at any time.
	
	// You only need to use this if you are not parsing the entire content.
	// But it doesn't hurt if you call it anyway.
	parser.stopParsing();
	
	


```

#### Escaping quote escape characters ####

In CSV, quotes inside quoted values must be escaped. For example, the sequence  [*\"*] will a quote character inside a quoted value. But what if your quoted value ends with the backslash?
In this case you need to escape the escape character. Consider the following input in [escape.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/escape.csv):


``` escape.csv

	"You are \"beautiful\""
	"Yes, \\\"in the inside\"\\"


```

To parse this properly, you need to define the *CharToEscapeQuoteEscaping*:


```java

	
	// quotes inside quoted values are escaped as \"
	settings.getFormat().setQuoteEscape('\\');
	
	// but if two backslashes are found before a quote symbol they represent a single slash.
	settings.getFormat().setCharToEscapeQuoteEscaping('\\');
	


```

This way the data will be correctly processed as:


```

	[You are "beautiful"]
	[Yes, \"in the inside"\]


```

#### Read all rows of a CSV (the powerful version) ####

To have greater control over the parsing process, use a [RowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowProcessor.java). uniVocity-parsers provides some useful default implementations but you can always provide your own.

The following example uses [RowListProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowListProcessor.java), which just stores the rows read from a file into a List:


```java

	
	
	// The settings object provides many configuration options
	CsvParserSettings parserSettings = new CsvParserSettings();
	
	//You can configure the parser to automatically detect what line separator sequence is in the input
	parserSettings.setLineSeparatorDetectionEnabled(true);
	
	// A RowListProcessor stores each parsed row in a List.
	RowListProcessor rowProcessor = new RowListProcessor();
	
	// You can configure the parser to use a RowProcessor to process the values of each parsed row.
	// You will find more RowProcessors in the 'com.univocity.parsers.common.processor' package, but you can also create your own.
	parserSettings.setProcessor(rowProcessor);
	
	// Let's consider the first parsed row as the headers of each column in the file.
	parserSettings.setHeaderExtractionEnabled(true);
	
	// creates a parser instance with the given settings
	CsvParser parser = new CsvParser(parserSettings);
	
	// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
	parser.parse(getReader("/examples/example.csv"));
	
	// get the parsed records from the RowListProcessor here.
	// Note that different implementations of RowProcessor will provide different sets of functionalities.
	String[] headers = rowProcessor.getHeaders();
	List<String[]> rows = rowProcessor.getRows();
	
	


```

Each row will contain: 


```

	[Year, Make, Model, Description, Price]
	=======================
	1 [1997, Ford, E350, ac, abs, moon, 3000.00]
	-----------------------
	2 [1999, Chevy, Venture "Extended Edition", null, 4900.00]
	-----------------------
	3 [1996, Jeep, Grand Cherokee, MUST SELL!
	air, moon roof, loaded, 4799.00]
	-----------------------
	4 [1999, Chevy, Venture "Extended Edition, Very Large", null, 5000.00]
	-----------------------
	5 [null, null, Venture "Extended Edition", null, 4900.00]
	-----------------------


```

You can also use a [ObjectRowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ObjectRowProcessor.java), which will produce rows of objects. You can convert values using an implementation of the [Conversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversion.java) interface.
The [Conversions](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversions.java) class provides some useful defaults for you.
For convenience, the [ObjectRowListProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ObjectRowListProcessor.java) can be used to store all rows into a list. 


```java

	
	
	// ObjectRowProcessor converts the parsed values and gives you the resulting row.
	ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
		@Override
		public void rowProcessed(Object[] row, ParsingContext context) {
			//here is the row. Let's just print it.
			println(out, Arrays.toString(row));
		}
	};
	
	// converts values in the "Price" column (index 4) to BigDecimal
	rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(4);
	
	// converts the values in columns "Make, Model and Description" to lower case, and sets the value "chevy" to null.
	rowProcessor.convertFields(Conversions.toLowerCase(), Conversions.toNull("chevy")).set("Make", "Model", "Description");
	
	// converts the values at index 0 (year) to BigInteger. Nulls are converted to BigInteger.ZERO.
	rowProcessor.convertFields(new BigIntegerConversion(BigInteger.ZERO, "0")).set("year");
	
	CsvParserSettings parserSettings = new CsvParserSettings();
	parserSettings.getFormat().setLineSeparator("\n");
	parserSettings.setProcessor(rowProcessor);
	parserSettings.setHeaderExtractionEnabled(true);
	
	CsvParser parser = new CsvParser(parserSettings);
	
	//the rowProcessor will be executed here.
	parser.parse(getReader("/examples/example.csv"));
	
	


```

After applying the conversions, the output will be:


```

	[1997, ford, e350, ac, abs, moon, 3000.00]
	[1999, null, venture "extended edition", null, 4900.00]
	[1996, jeep, grand cherokee, must sell!
	air, moon roof, loaded, 4799.00]
	[1999, null, venture "extended edition, very large", null, 5000.00]
	[0, null, venture "extended edition", null, 4900.00]


```

### Using annotations to map your java beans ###

Use the [Parsed](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/annotations/Parsed.java) annotation to map the property to a field in the CSV file. You can map the property using a field name as declared in the headers,
or the column index in the input.

Each annotated operation maps to a [Conversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversion.java) and they are executed in the same sequence they are declared. 

This example works with the csv file [bean_test.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/bean_test.csv)


```java

	class TestBean {
	
	// if the value parsed in the quantity column is "?" or "-", it will be replaced by null.
	@NullString(nulls = { "?", "-" })
	// if a value resolves to null, it will be converted to the String "0".
	@Parsed(defaultNullRead = "0")
	private Integer quantity;   // The attribute type defines which conversion will be executed when processing the value.
	// In this case, IntegerConversion will be used.
	// The attribute name will be matched against the column header in the file automatically.
	
	@Trim
	@LowerCase
	// the value for the comments attribute is in the column at index 4 (0 is the first column, so this means fifth column in the file)
	@Parsed(index = 4)
	private String comments;
	
	// you can also explicitly give the name of a column in the file.
	@Parsed(field = "amount")
	private BigDecimal amount;
	
	@Trim
	@LowerCase
	// values "no", "n" and "null" will be converted to false; values "yes" and "y" will be converted to true
	@BooleanString(falseStrings = { "no", "n", "null" }, trueStrings = { "yes", "y" })
	@Parsed
	private Boolean pending;
	
	//


```

Instances of annotated classes are created with by [BeanProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BeanProcessor.java) and [BeanListProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BeanListProcessor.java):


```java

	
	// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
	BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);
	
	CsvParserSettings parserSettings = new CsvParserSettings();
	parserSettings.setProcessor(rowProcessor);
	parserSettings.setHeaderExtractionEnabled(true);
	
	CsvParser parser = new CsvParser(parserSettings);
	parser.parse(getReader("/examples/bean_test.csv"));
	
	// The BeanListProcessor provides a list of objects extracted from the input.
	List<TestBean> beans = rowProcessor.getBeans();
	
	


```

Here is the output produced by the `toString()` method of each [TestBean](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/TestBean.java) instance:


```

	[TestBean [quantity=1, comments=?, amount=555.999, pending=true], TestBean [quantity=0, comments=" something ", amount=null, pending=false]]


```

### Using your own conversions in annotations ###

Any implementation of [Conversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversion.java) can be used in fields annotated with [Parsed](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/annotations/Parsed.java). The following class converts delimited Strings to a set of words (when reading) and a set of words to a delimited String with all words in the set (for writing). To do this, all you need is to introduce a varargs constructor to your class, so it can also be initialized with `String... args`:


```java

	class WordsToSetConversion implements Conversion<String, Set<String>> {
	
	private final String separator;
	private final boolean toUpperCase;
	
	public WordsToSetConversion(String... args) {
	String separator = ",";
	boolean toUpperCase = true;
	
	if (args.length == 1) {
		separator = args[0];
	}
	
	if (args.length == 2) {
		toUpperCase = Boolean.valueOf(args[1]);
	}
	
	this.separator = separator;
	this.toUpperCase = toUpperCase;
	}
	
	public WordsToSetConversion(String separator, boolean toUpperCase) {
	this.separator = separator;
	this.toUpperCase = toUpperCase;
	}
	
	@Override
	public Set<String> execute(String input) {
	if (input == null) {
		return Collections.emptySet();
	}
	
	if (toUpperCase) {
		input = input.toUpperCase();
	}
	
	Set<String> out = new TreeSet<String>();
	for (String token : input.split(separator)) {
		//extracting words separated by white space as well
		for (String word : token.trim().split("\\s")) {
			out.add(word.trim());
		}
	}
	
	return out;
	}
	
	//


```

Let's use our beaten up example to create instances of [Car](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/Car.java) from all entries in [example.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/example.csv). Now we want to split the words in the `description` field add them to a set of words. All we hate to do is this:


```java

	class Car {
	@Parsed
	private Integer year;
	
	@Convert(conversionClass = WordsToSetConversion.class, args = { ",", "true" })
	@Parsed
	private Set<String> description;
	
	//


```

uniVocity-parsers will create an instance of [WordsToSetConversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/WordsToSetConversion.java) using the given arguments. Now, let's use the good old [BeanListProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BeanListProcessor.java) to parse and generate a list of [Car](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/Car.java)s from our file 


```java

	
	BeanListProcessor<Car> rowProcessor = new BeanListProcessor<Car>(Car.class);
	parserSettings.setProcessor(rowProcessor);
	
	CsvParser parser = new CsvParser(parserSettings);
	parser.parse(getReader("/examples/example.csv"));
	
	//Let's get our cars
	List<Car> cars = rowProcessor.getBeans();
	for (Car car : cars) {
		// Let's get only those cars that actually have some description
		if (!car.getDescription().isEmpty()) {
			println(out, car.getDescription() + " - " + car.toString());
		}
	}
	


```

After executing this to print only those cars that have a description, the output will be:


```

	[ABS, AC, MOON] - year=1997, make=Ford, model=E350, price=3000.00
	[AIR, LOADED, MOON, MUST, ROOF, SELL!] - year=1996, make=Jeep, model=Grand Cherokee, price=4799.00


```

### Reading master-detail style files ###

Use [MasterDetailProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/MasterDetailProcessor.java) or [MasterDetailListProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/MasterDetailListProcessor.java) to produce [MasterDetailRecord](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/MasterDetailRecord.java) objects.
A simple example a master-detail file is in the [master_detail.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/master_detail.csv) file. 

Each [MasterDetailRecord](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/MasterDetailRecord.java) holds a master record row and its list of associated detail rows.


```java

	
	// 1st, Create a RowProcessor to process all "detail" elements
	ObjectRowListProcessor detailProcessor = new ObjectRowListProcessor();
	
	// converts values at in the "Amount" column (position 1 in the file) to integer.
	detailProcessor.convertIndexes(Conversions.toInteger()).set(1);
	
	// 2nd, Create MasterDetailProcessor to identify whether or not a row is the master row.
	// the row placement argument indicates whether the master detail row occurs before or after a sequence of "detail" rows.
	MasterDetailListProcessor masterRowProcessor = new MasterDetailListProcessor(RowPlacement.BOTTOM, detailProcessor) {
		@Override
		protected boolean isMasterRecord(String[] row, ParsingContext context) {
			//Returns true if the parsed row is the master row.
			//In this example, rows that have "Total" in the first column are master rows.
			return "Total".equals(row[0]);
		}
	};
	// We want our master rows to store BigIntegers in the "Amount" column
	masterRowProcessor.convertIndexes(Conversions.toBigInteger()).set(1);
	
	CsvParserSettings parserSettings = new CsvParserSettings();
	parserSettings.setHeaderExtractionEnabled(true);
	
	// Set the RowProcessor to the masterRowProcessor.
	parserSettings.setProcessor(masterRowProcessor);
	
	CsvParser parser = new CsvParser(parserSettings);
	parser.parse(getReader("/examples/master_detail.csv"));
	
	// Here we get the MasterDetailRecord elements.
	List<MasterDetailRecord> rows = masterRowProcessor.getRecords();
	MasterDetailRecord masterRecord = rows.get(0);
	
	// The master record has one master row and multiple detail rows.
	Object[] masterRow = masterRecord.getMasterRow();
	List<Object[]> detailRows = masterRecord.getDetailRows();
	


```

After printing the master row and its details rows, the output is:


```

	[Total, 100]
	=======================
	1 [Item1, 50]
	-----------------------
	2 [Item2, 40]
	-----------------------
	3 [Item3, 10]
	-----------------------


```

### Parsing fixed-width files ###

All functionalities you have with the CSV file format are available for the fixed-width format (and any other parser we introduce in the future).

In the [example.txt](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/example.txt) fixed-width file, we chose to fill the unwritten spaces with underscores ('_'), 
so in the parser settings we set the padding to underscore: 


``` example.txt

	
	YearMake_Model___________________________________Description_____________________________Price___
	1997Ford_E350____________________________________ac, abs, moon___________________________3000.00_
	1999ChevyVenture "Extended Edition"______________________________________________________4900.00_
	1996Jeep_Grand Cherokee__________________________MUST SELL!
	air, moon roof, loaded_______4799.00_
	1999ChevyVenture "Extended Edition, Very Large"__________________________________________5000.00_
	_________Venture "Extended Edition"______________________________________________________4900.00_


```

The only thing you need to do is to instantiate a different parser:
 

```java

	
	
	// creates the sequence of field lengths in the file to be parsed
	FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(4, 5, 40, 40, 8);
	
	// creates the default settings for a fixed width parser
	FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);
	
	//sets the character used for padding unwritten spaces in the file
	settings.getFormat().setPadding('_');
	
	//the file used in the example uses '\n' as the line separator sequence.
	//the line separator sequence is defined here to ensure systems such as MacOS and Windows
	//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	settings.getFormat().setLineSeparator("\n");
	
	// creates a fixed-width parser with the given settings
	FixedWidthParser parser = new FixedWidthParser(settings);
	
	// parses all rows in one go.
	List<String[]> allRows = parser.parseAll(getReader("/examples/example.txt"));
	
	


```
 
Use [FixedWidthFieldLengths](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/fixed/FixedWidthFieldLengths.java) to define what is the length of each field in the input. With that information we can then create the  [FixedWidthParserSettings](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/fixed/FixedWidthParserSettings.java). 

The output will be: 

```

	1 [Year, Make, Model, Description, Price]
	-----------------------
	2 [1997, Ford, E350, ac, abs, moon, 3000.00]
	-----------------------
	3 [1999, Chevy, Venture "Extended Edition", null, 4900.00]
	-----------------------
	4 [1996, Jeep, Grand Cherokee, MUST SELL!
	air, moon roof, loaded, 4799.00]
	-----------------------
	5 [1999, Chevy, Venture "Extended Edition, Very Large", null, 5000.00]
	-----------------------
	6 [null, null, Venture "Extended Edition", null, 4900.00]
	-----------------------


```

All the rest is the same as with CSV parsers. You can use all [RowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowProcessor.java)s for annotations, conversions, master-detail records 
and anything else we (or you) might introduce in the future.
 
We created a set of examples using fixed with parsing in the [FixedWidthParserExamples.java](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/FixedWidthParserExamples.java)


### Parsing TSV files ###

To parse TSV files, simply use a TsvParser. As we keep saying, the API is essentially same for every parser.

This is the input:

```
# TSV's can also have comments
# Multi-line records are escaped with \n.
# Accepted escape sequences are: \n, \t, \r and \\   

Year	Make	Model	Description	Price
1997	Ford	E350	ac, abs, moon	3000.00
1999	Chevy	Venture "Extended Edition"		4900.00
   
# Look	 a multi line value. And blank rows around it!
     
1996	Jeep	Grand Cherokee	MUST SELL!\nair, moon roof, loaded	4799.00
1999	Chevy	Venture "Extended Edition, Very Large"		5000.00
		Venture "Extended Edition"		4900.00
```

This is the code:


```java

	
	
	TsvParserSettings settings = new TsvParserSettings();
	//the file used in the example uses '\n' as the line separator sequence.
	//the line separator sequence is defined here to ensure systems such as MacOS and Windows
	//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	settings.getFormat().setLineSeparator("\n");
	
	// creates a TSV parser
	TsvParser parser = new TsvParser(settings);
	
	// parses all rows in one go.
	List<String[]> allRows = parser.parseAll(getReader("/examples/example.tsv"));
	
	


```

The output will be:


```

	1 [Year, Make, Model, Description, Price]
	-----------------------
	2 [1997, Ford, E350, ac, abs, moon, 3000.00]
	-----------------------
	3 [1999, Chevy, Venture "Extended Edition", null, 4900.00]
	-----------------------
	4 [1996, Jeep, Grand Cherokee, MUST SELL!
	air, moon roof, loaded, 4799.00]
	-----------------------
	5 [1999, Chevy, Venture "Extended Edition, Very Large", null, 5000.00]
	-----------------------
	6 [null, null, Venture "Extended Edition", null, 4900.00]
	-----------------------


```

### Column selection ###

Parsing the entire content of each record in a file is a waste of CPU and memory when you are not interested in all columns.
uniVocity-parsers lets you choose the columns you need, so values you don't want are simply bypassed.

The following examples can be found in the example class [SettingsExamples](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/SettingsExamples.java):

Consider the [example.csv](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/example.csv) file with:


``` example.csv

	
	Year,Make,Model,Description,Price
	1997,Ford,E350,"ac, abs, moon",3000.00
	1999,Chevy,"Venture ""Extended Edition""","",4900.00
	
	...


```

And the following selection:


```java

	
	// Here we select only the columns "Price", "Year" and "Make".
	// The parser just skips the other fields
	parserSettings.selectFields("Price", "Year", "Make");
	
	// let's parse with these settings and print the parsed rows.
	List<String[]> parsedRows = parseWithSettings(parserSettings);
	


```

The output will be:


```

	1 [3000.00, 1997, Ford]
	-----------------------
	2 [4900.00, 1999, Chevy]
	-----------------------
	...


```

The same output will be obtained with index-based selection.


```java

	
	// Here we select only the columns by their indexes.
	// The parser just skips the values in other columns
	parserSettings.selectIndexes(4, 0, 1);
	
	// let's parse with these settings and print the parsed rows.
	List<String[]> parsedRows = parseWithSettings(parserSettings);
	


```

You can also opt to keep the original row format with all columns, but only the values you are interested in being processed:


```java

	
	// Here we select only the columns "Price", "Year" and "Make".
	// The parser just skips the other fields
	parserSettings.selectFields("Price", "Year", "Make");
	
	// Column reordering is enabled by default. When you disable it,
	// all columns will be produced in the order they are defined in the file.
	// Fields that were not selected will be null, as they are not processed by the parser
	parserSettings.setColumnReorderingEnabled(false);
	
	// Let's parse with these settings and print the parsed rows.
	List<String[]> parsedRows = parseWithSettings(parserSettings);
	


```

Now the output will be:


```

	1 [1997, Ford, null, null, 3000.00]
	-----------------------
	2 [1999, Chevy, null, null, 4900.00]
	-----------------------
	3 [1996, Jeep, null, null, 4799.00]
	...


```


## Reading columns instead of rows ###

Since uniVocity-parsers 1.3.0, a few special types of [RowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowProcessor.java)s have been introduced to collect the values of columns instead of rows:

 * [ColumnProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ColumnProcessor.java) - reads values of all columns as plain Strings.
 * [ObjectColumnProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ObjectColumnProcessor.java) - reads column values as Objects. Any sequence of [Conversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversion.java)s can be used to convert the parsed values to the desired object. 
 
To avoid problems with memory when processing large inputs, we also introduced the following column processors. These will return the column values processed after a batch of a given number of rows:
 
 * [BatchedColumnProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BatchedColumnProcessor.java) - 
 * [BatchedObjectColumnProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BatchedObjectColumnProcessor.java)
 
Here are some examples on how to use them:

### Parsing columns from a CSV file ###


```java

	
	CsvParserSettings parserSettings = new CsvParserSettings();
	parserSettings.getFormat().setLineSeparator("\n");
	parserSettings.setHeaderExtractionEnabled(true);
	
	// To get the values of all columns, use a column processor
	ColumnProcessor rowProcessor = new ColumnProcessor();
	parserSettings.setProcessor(rowProcessor);
	
	CsvParser parser = new CsvParser(parserSettings);
	
	//This will kick in our column processor
	parser.parse(getReader("/examples/example.csv"));
	
	//Finally, we can get the column values:
	Map<String, List<String>> columnValues = rowProcessor.getColumnValuesAsMapOfNames();
	
	


```

Let's see the output. Each row displays the column name and the values parsed on each:


```

	Year -> [1997, 1999, 1996, 1999, null]
	Description -> [ac, abs, moon, null, MUST SELL!
	air, moon roof, loaded, null, null]
	Model -> [E350, Venture "Extended Edition", Grand Cherokee, Venture "Extended Edition, Very Large", Venture "Extended Edition"]
	Price -> [3000.00, 4900.00, 4799.00, 5000.00, 4900.00]
	Make -> [Ford, Chevy, Jeep, Chevy, null]


```


### Using the batched column processor in a Fixed-With input ###


```java

	
	
	//To process larger inputs, we can use a batched column processor.
	//Here we set the batch size to 3, meaning we'll get the column values of at most 3 rows in each batch.
	settings.setProcessor(new BatchedColumnProcessor(3) {
	
		@Override
		public void batchProcessed(int rowsInThisBatch) {
			List<List<String>> columnValues = getColumnValuesAsList();
	
			println(out, "Batch " + getBatchesProcessed() + ":");
			int i = 0;
			for (List<String> column : columnValues) {
				println(out, "Column " + (i++) + ":" + column);
			}
		}
	});
	
	FixedWidthParser parser = new FixedWidthParser(settings);
	parser.parse(getReader("/examples/example.txt"));
	
	


```

Here we print the column values from each batch of 3 rows. As we have 5 rows in the input, the last batch will have 2 values per column:


```

	Batch 0:
	Column 0:[1997, 1999, 1996]
	Column 1:[Ford, Chevy, Jeep]
	Column 2:[E350, Venture "Extended Edition", Grand Cherokee]
	Column 3:[ac, abs, moon, null, MUST SELL!
	air, moon roof, loaded]
	Column 4:[3000.00, 4900.00, 4799.00]
	Batch 1:
	Column 0:[1999, null]
	Column 1:[Chevy, null]
	Column 2:[Venture "Extended Edition, Very Large", Venture "Extended Edition"]
	Column 3:[null, null]
	Column 4:[5000.00, 4900.00]


```


### Reading columns from a TSV while converting the parsed content to Objects ###


```java

	
	
	// ObjectColumnProcessor converts the parsed values and stores them in columns
	// Use BatchedObjectColumnProcessor to process columns in batches
	ObjectColumnProcessor rowProcessor = new ObjectColumnProcessor();
	
	// converts values in the "Price" column (index 4) to BigDecimal
	rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(4);
	
	// converts the values in columns "Make, Model and Description" to lower case, and sets the value "chevy" to null.
	rowProcessor.convertFields(Conversions.toLowerCase(), Conversions.toNull("chevy")).set("Make", "Model", "Description");
	
	// converts the values at index 0 (year) to BigInteger. Nulls are converted to BigInteger.ZERO.
	rowProcessor.convertFields(new BigIntegerConversion(BigInteger.ZERO, "0")).set("year");
	
	parserSettings.setProcessor(rowProcessor);
	
	TsvParser parser = new TsvParser(parserSettings);
	
	//the rowProcessor will be executed here.
	parser.parse(getReader("/examples/example.tsv"));
	
	//Let's get the column values:
	Map<Integer, List<Object>> columnValues = rowProcessor.getColumnValuesAsMapOfIndexes();
	
	


```

Now we will print the column indexes and their values:


```

	0 -> [1997, 1999, 1996, 1999, 0]
	1 -> [ford, null, jeep, null, null]
	2 -> [e350, venture "extended edition", grand cherokee, venture "extended edition, very large", venture "extended edition"]
	3 -> [ac, abs, moon, null, must sell!
	air, moon roof, loaded, null, null]
	4 -> [3000.00, 4900.00, 4799.00, 5000.00, 4900.00]


```

### Processing rows in parallel ###

As of uniVocity-parsers 1.4.0 you can process rows as they are parsed in a separate thread easily. All you've got to do is to wrap your [RowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowProcessor.java) in a [ConcurrentRowProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ConcurrentRowProcessor.java):


```java

	
	parserSettings.setProcessor(new ConcurrentRowProcessor(rowProcessor));
	


```

Note that this may not always produce faster processing times. uniVocity-parsers is highly optimized and processing your data sequentially will still be faster than in parallel in many cases. 
We recommend you to profile your particular processing scenario before blindly deciding whether to use this feature.


### Parsing individual Strings ###

If you are getting rows from an external source, and just need to parse each one, you can simply use the *parseLine(String)* method. The following example parses TSV lines:
 

```java

	
	// creates a TSV parser
	TsvParser parser = new TsvParser(new TsvParserSettings());
	
	String[] line;
	line = parser.parseLine("A	B	C");
	println(out, Arrays.toString(line));
	
	line = parser.parseLine("1	2	3	4");
	println(out, Arrays.toString(line));
	


```

Which yields:


```

	[A, B, C]
	[1, 2, 3, 4]


```

## Settings ##

Each parser has its own settings class, but many configuration options are common across all parsers. The following snippet demonstrates how to use each one of them: 


```java

	
	//You can configure the parser to automatically detect what line separator sequence is in the input
	parserSettings.setLineSeparatorDetectionEnabled(true);
	
	// sets what is the default value to use when the parsed value is null
	parserSettings.setNullValue("<NULL>");
	
	// sets what is the default value to use when the parsed value is empty
	parserSettings.setEmptyValue("<EMPTY>"); // for CSV only
	
	// sets the headers of the parsed file. If the headers are set then 'setHeaderExtractionEnabled(true)'
	// will make the parser simply ignore the first input row.
	parserSettings.setHeaders("a", "b", "c", "d", "e");
	
	// prints the columns in reverse order.
	// NOTE: when fields are selected, all rows produced will have the exact same number of columns
	parserSettings.selectFields("e", "d", "c", "b", "a");
	
	// does not skip leading whitespaces
	parserSettings.setIgnoreLeadingWhitespaces(false);
	
	// does not skip trailing whitespaces
	parserSettings.setIgnoreTrailingWhitespaces(false);
	
	// reads a fixed number of records then stop and close any resources
	parserSettings.setNumberOfRecordsToRead(9);
	
	// does not skip empty lines
	parserSettings.setSkipEmptyLines(false);
	
	// sets the maximum number of characters to read in each column.
	// The default is 4096 characters. You need this to avoid OutOfMemoryErrors in case a file
	// does not have a valid format. In such cases the parser might just keep reading from the input
	// until its end or the memory is exhausted. This sets a limit which avoids unwanted JVM crashes.
	parserSettings.setMaxCharsPerColumn(100);
	
	// for the same reasons as above, this sets a hard limit on how many columns an input row can have.
	// The default is 512.
	parserSettings.setMaxColumns(10);
	
	// Sets the number of characters held by the parser's buffer at any given time.
	parserSettings.setInputBufferSize(1000);
	
	// Disables the separate thread that loads the input buffer. By default, the input is going to be loaded incrementally
	// on a separate thread if the available processor number is greater than 1. Leave this enabled to get better performance
	// when parsing big files (> 100 Mb).
	parserSettings.setReadInputOnSeparateThread(false);
	
	// let's parse with these settings and print the parsed rows.
	List<String[]> parsedRows = parseWithSettings(parserSettings);
	


```

The output of the CSV parser with all these settings will be:


```

	1 [<NULL>, <NULL>, <NULL>, <NULL>, <NULL>]
	-----------------------
	2 [Price, Description, Model, Make, Year]
	-----------------------
	3 [3000.00, ac, abs, moon, E350, Ford, 1997]
	-----------------------
	4 [4900.00, <EMPTY>, Venture "Extended Edition", Chevy, 1999]
	-----------------------
	5 [<NULL>, <NULL>, <NULL>, <NULL>,    ]
	-----------------------
	6 [<NULL>, <NULL>, <NULL>, <NULL>,      ]
	-----------------------
	7 [4799.00, MUST SELL!
	air, moon roof, loaded, Grand Cherokee, Jeep, 1996]
	-----------------------
	8 [5000.00, <NULL>, Venture "Extended Edition, Very Large", Chevy, 1999]
	-----------------------
	9 [4900.00, <EMPTY>, Venture "Extended Edition", <NULL>, <NULL>]
	-----------------------
	...


```

### Fixed-width settings ###


```java

	
	// For the sake of the example, we will not read the last 8 characters (for the Year column).
	// We will also NOT set the padding character to '_' so the output makes more sense for reading
	// and you can see what characters are being processed
	FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(new FixedWidthFieldLengths(4, 5, 40, 40 /*, 8*/));
	
	//the file used in the example uses '\n' as the line separator sequence.
	//the line separator sequence is defined here to ensure systems such as MacOS and Windows
	//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	parserSettings.getFormat().setLineSeparator("\n");
	
	// The fixed width parser settings has most of the settings for CSV.
	// These are the only extra settings you need:
	
	// If a row has more characters than what is defined, skip them until the end of the line.
	parserSettings.setSkipTrailingCharsUntilNewline(true);
	
	// If a record has less characters than what is expected and a new line is found,
	// this record is considered parsed. Data in the next row will be parsed as a new record.
	parserSettings.setRecordEndsOnNewline(true);
	
	RowListProcessor rowProcessor = new RowListProcessor();
	
	parserSettings.setProcessor(rowProcessor);
	parserSettings.setHeaderExtractionEnabled(true);
	
	FixedWidthParser parser = new FixedWidthParser(parserSettings);
	parser.parse(getReader("/examples/example.txt"));
	
	List<String[]> rows = rowProcessor.getRows();
	


```

The parser output with such configuration for parsing the [example.txt](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/resources/examples/example.txt) file will be:


```

	1 [1997, Ford_, E350____________________________________, ac, abs, moon___________________________]
	-----------------------
	2 [1999, Chevy, Venture "Extended Edition"______________, ________________________________________]
	-----------------------
	3 [1996, Jeep_, Grand Cherokee__________________________, MUST SELL!]
	-----------------------
	4 [air,, moon, roof, loaded_______4799.00_]
	-----------------------
	5 [1999, Chevy, Venture "Extended Edition, Very Large"__, ________________________________________]
	-----------------------
	6 [____, _____, Venture "Extended Edition"______________, ________________________________________]
	-----------------------


```

As `recordEndsOnNewline = true `, lines 3 and 4 are considered different records, instead of a single, multi-line record.
To clarity: in line 4, the value of the *first column* is 'air,', the *second column* has value 'moon', and the *third* is 'roof, loaded_______4799.00_'.

### Format Settings ###

All parser settings have a default format definition. The following attributes are set by default for all parsers:

* `lineSeparator` (default *System.getProperty("line.separator");*): this is an array of 1 or 2 characters with the sequence that indicates the end of a line. 
	Using this, you should be able to handle files produced by different operating systems.
	Of course, if you want your line separator to be "#$", you can.    
	
* `normalizedNewline` (default *\n*): used to represent the sequence of 2 characters used as a line separator (e.g. *\r\n* in Windows). 
It is used by our parsers/writers to easily handle portable line separators.
 
  * When parsing, if the sequence of characters defined in *lineSeparator* is found while reading from the input, 
	it will be transparently replaced by the *normalizedNewline* character.
	 
  * When writing, *normalizedNewline* is replaced by the *lineSeparator* sequence. 
	
* `comment` (default *#*): if the first character of a line of text matches the comment character, then the row will be
	considered a comment and discarded from the input.  
	

### CSV format ###


* `delimiter` (default *,*): value used to separate individual fields in the input.

* `quote` (default *"*): value used for escaping values where the field delimiter is part of the value (e.g. the value " a , b " is parsed as ` a , b `). 

* `quoteEscape` (default *"*): value used for escaping the quote character inside an already escaped value (e.g. the value " "" a , b "" " is parsed as ` " a , b " `).


### Fixed width format ###

In addition to the default format definition, the fixed with format contains:

* `padding` (default *' '*): value used for filling unwritten spaces.


### TSV format ###

The TSV format lets you set the default escape character for values that contain \n, \r, \t and \\.

* `escapeChar` (default *\\*): value used to escape special characters in TSV.

## Writing ##

As you can see in [WriterExamples.java](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/WriterExamples.java), writing is quite straightforward. All you need is an 
instance of java.io.Writer (to write the values you provide to some output resource) and a settings object with the configuration of how the values should be written.

### Quick and simple CSV writing example ###

You can write your data in CSV format using just 3 lines of code:


```java

	
	
	// All you need is to create an instance of CsvWriter with the default CsvWriterSettings.
	// By default, only values that contain a field separator are enclosed within quotes.
	// If quotes are part of the value, they are escaped automatically as well.
	// Empty rows are discarded automatically.
	CsvWriter writer = new CsvWriter(outputWriter, new CsvWriterSettings());
	
	// Write the record headers of this file
	writer.writeHeaders("Year", "Make", "Model", "Description", "Price");
	
	// Here we just tell the writer to write everything and close the given output Writer instance.
	writer.writeRowsAndClose(rows);
	
	


```

This will produce the following output:


```

	Year,Make,Model,Description,Price
	1997,Ford,E350,"ac, abs, moon",3000.00
	1999,Chevy,Venture "Extended Edition",,4900.00
	1996,Jeep,Grand Cherokee,"MUST SELL!
	air, moon roof, loaded",4799.00
	1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
	,,Venture "Extended Edition",,4900.00


```

If you want to write the same content in fixed width format, all you need is to create an instance of [FixedWidthWriter](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/fixed/FixedWidthWriter.java) instead. The remainder of the code remains the same.

This will be the case for any other writers/parsers we might introduce in the future, and applies to all examples presented here.

### TSV writing example ###

This is exactly the same as the CSV example seen above. All you need is to instantiate a new writer:


```java

	
	
	// As with the CsvWriter, all you need is to create an instance of TsvWriter with the default TsvWriterSettings.
	TsvWriter writer = new TsvWriter(outputWriter, new TsvWriterSettings());
	
	// Write the record headers of this file
	writer.writeHeaders("Year", "Make", "Model", "Description", "Price");
	
	// Here we just tell the writer to write everything and close the given output Writer instance.
	writer.writeRowsAndClose(rows);
	
	


```

This will produce the following output:


```

	Year	Make	Model	Description	Price
	1997	Ford	E350	ac, abs, moon	3000.00
	1999	Chevy	Venture "Extended Edition"		4900.00
	1996	Jeep	Grand Cherokee	MUST SELL!\nair, moon roof, loaded	4799.00
	1999	Chevy	Venture "Extended Edition, Very Large"		5000.00
	Venture "Extended Edition"		4900.00


```

### Writing row by row, with comments ###


```java

	
	CsvWriterSettings settings = new CsvWriterSettings();
	// Sets the character sequence to write for the values that are null.
	settings.setNullValue("?");
	
	//Changes the comment character to -
	settings.getFormat().setComment('-');
	
	// Sets the character sequence to write for the values that are empty.
	settings.setEmptyValue("!");
	
	// writes empty lines as well.
	settings.setSkipEmptyLines(false);
	
	// Creates a writer with the above settings;
	CsvWriter writer = new CsvWriter(outputWriter, settings);
	
	// writes the file headers
	writer.writeHeaders("a", "b", "c", "d", "e");
	
	// Let's write the rows one by one (the first row will be skipped)
	for (int i = 1; i < rows.size(); i++) {
		// You can write comments above each row
		writer.commentRow("This is row " + i);
		// writes the row
		writer.writeRow(rows.get(i));
	}
	
	// we must close the writer. This also closes the java.io.Writer you used to create the CsvWriter instance
	// note no checked exceptions are thrown here. If anything bad happens you'll get an IllegalStateException wrapping the original error.
	writer.close();
	


```

The output of the above code should be:


```

	a,b,c,d,e
	-This is row 1
	1999,Chevy,Venture "Extended Edition",!,4900.00
	-This is row 2
	1996,Jeep,Grand Cherokee,"MUST SELL!
	...


```

### Writing with column selection ###

You can write transparently to *some* fields of a CSV file, while keeping the output format consistent. Let's say you have a CSV
file with 5 columns but only have data for 3 of them, in a different order. All you have to do is configure the file headers
and select what fields you have values for. 


```java

	
	CsvWriterSettings settings = new CsvWriterSettings();
	
	// when writing, nulls are printed using the empty value (defaults to "").
	// Here we configure the writer to print ? to describe null values.
	settings.setNullValue("?");
	
	// if the value is not null, but is empty (e.g. ""), the writer will can be configured to
	// print some default representation for a non-null/empty value
	settings.setEmptyValue("!");
	
	// Encloses all records within quotes even when they are not required.
	settings.setQuoteAllFields(true);
	
	// Sets the file headers (used for selection, these values won't be written automatically)
	settings.setHeaders("Year", "Make", "Model", "Description", "Price");
	
	// Selects which fields from the input should be written. In this case, fields "make" and "model" will be empty
	// The field selection is not case sensitive
	settings.selectFields("description", "price", "year");
	
	// Creates a writer with the above settings;
	CsvWriter writer = new CsvWriter(outputWriter, settings);
	
	// Writes the headers specified in the settings
	writer.writeHeaders();
	
	// writes each row providing values for the selected fields (note the values and field selection order must match)
	writer.writeRow("ac, abs, moon", 3000.00, 1997);
	writer.writeRow("", 4900.00, 1999); // NOTE: empty string will be replaced by "!" as per configured emptyQuotedValue.
	writer.writeRow("MUST SELL!\nair, moon roof, loaded", 4799.00, 1996);
	
	writer.close();
	


```

The output of such setting will be:


```

	"Year","Make","Model","Description","Price"
	"1997","?","?","ac, abs, moon","3000.0"
	"1999","?","?","!","4900.0"
	"1996","?","?","MUST SELL!
	...


```

### Writing with value conversions (using ObjectRowWriterProcessor) ###

All writers have a settings object that accepts an instance of [RowWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowWriterProcessor.java). 
Use the writer methods prefixed with "processRecord" to execute the [RowWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowWriterProcessor.java) against your input. 

In the following example, we use [ObjectRowWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/ObjectRowWriterProcessor.java) to execute custom value conversions on each element of a row of objects.
This object executes a sequence of [Conversion](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/conversions/Conversion.java) actions on the row elements before they are written.


```java

	
	FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(15, 10, 35);
	FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);
	
	// Any null values will be written as ?
	settings.setNullValue("nil");
	settings.getFormat().setPadding('_');
	settings.setIgnoreLeadingWhitespaces(false);
	settings.setIgnoreTrailingWhitespaces(false);
	
	// Creates an ObjectRowWriterProcessor that handles annotated fields in the TestBean class.
	ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
	settings.setRowWriterProcessor(processor);
	
	// Converts objects in the "date" field using the yyyy-MMM-dd format.
	processor.convertFields(Conversions.toDate(" yyyy MMM dd "), Conversions.trim()).add("date");
	
	// Trims Strings at position 2 of the input row.
	processor.convertIndexes(Conversions.trim(), Conversions.toUpperCase()).add(2);
	
	// Sets the file headers so the writer knows the correct order when writing values taken from a TestBean instance
	settings.setHeaders("date", "quantity", "comments");
	
	// Creates a writer with the above settings;
	FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);
	
	// Writes the headers specified in the settings
	writer.writeHeaders();
	
	// writes a Fixed Width row with the values set in "bean". Notice that there's no annotated
	// attribute for the "date" column, so it will just be null (an then converted to ? a )
	writer.processRecord(new Date(0), null, "  a comment  ");
	writer.processRecord(null, 1000, "");
	
	writer.close();
	


```

The output will be:


```

	date___________quantity__comments___________________________
	1970 Jan 01____nil_______A COMMENT__________________________
	nil____________1000_________________________________________


```

### Writing annotated java beans ###

If you have a java class with fields annotated with the annotations defined in package `com.univocity.parsers.annotations`, you can use a [BeanWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BeanWriterProcessor.java)
to map its attributes directly to the output.

A [RowWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/RowWriterProcessor.java) is just an interface that "knows" how to map a given object to a sequence of values. By default, uniVocity-parsers provides the [BeanWriterProcessor](http://github.com/uniVocity/univocity-parsers/tree/master/src/main/java/com/univocity/parsers/common/processor/BeanWriterProcessor.java) to map annotated beans to rows.

The following example writes instances of [TestBean](http://github.com/uniVocity/univocity-parsers/tree/master/src/test/java/com/univocity/parsers/examples/TestBean.java): 


```java

	
	FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(10, 10, 35, 10, 40);
	FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);
	
	// Any null values will be written as ?
	settings.setNullValue("?");
	
	// Creates a BeanWriterProcessor that handles annotated fields in the TestBean class.
	settings.setRowWriterProcessor(new BeanWriterProcessor<TestBean>(TestBean.class));
	
	// Sets the file headers so the writer knows the correct order when writing values taken from a TestBean instance
	settings.setHeaders("amount", "pending", "date", "quantity", "comments");
	
	// Creates a writer with the above settings;
	FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);
	
	// Writes the headers specified in the settings
	writer.writeHeaders();
	
	// writes a fixed width row with empty values (as nothing was set in the TestBean instance).
	writer.processRecord(new TestBean());
	
	TestBean bean = new TestBean();
	bean.setAmount(new BigDecimal("500.33"));
	bean.setComments("Blah,blah");
	bean.setPending(false);
	bean.setQuantity(100);
	
	// writes a Fixed Width row with the values set in "bean". Notice that there's no annotated
	// attribute for the "date" column, so it will just be null (an then converted to ?, as we have settings.setNullValue("?");)
	writer.processRecord(bean);
	
	// you can still write rows passing in its values directly.
	writer.writeRow(BigDecimal.ONE, true, "1990-01-10", 3, null);
	
	writer.close();
	


```

The resulting output of the above code should be: 


```

	amount    pending   date                               quantity  comments
	?         ?         ?                                  ?         ?
	500.33    no        ?                                  100       blah,blah
	1         true      1990-01-10                         3         ?


```

### Writing value by value ###

If you don't have entire rows available when writing your data, you can simply define the values of each row one by one.


```java

	
	TsvWriter writer = new TsvWriter(outputWriter, new TsvWriterSettings());
	
	writer.writeHeaders("A", "B", "C", "D", "E");
	
	//writes a value to the first column
	writer.writeValue(10);
	
	//writes a value to the second column
	writer.writeValue(20);
	
	//writes a value to the fourth column (index 3 represents the 4th column - the one with header "D")
	writer.writeValue(3, 40);
	
	//overrides the value in the first column. "A" indicates the header name.
	writer.writeValue("A", 100.0);
	
	//flushes all values to the output, creating a row.
	writer.writeValuesToRow();
	
	


```

Which will produce:


```

	A	B	C	D	E
	100.0	20		40


```
