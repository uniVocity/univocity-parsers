![thumbnail](./images/uniVocity-parsers.png)

Welcome to uniVocity-parsers
============================

uniVocity-parsers is a collection of extremely fast and reliable parsers for Java. It provides a consistent interface for handling different file formats,
and a solid framework for the development of new parsers.

@@TOC 

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


Just download the jar file from [here](http://oss.sonatype.org/content/repositories/releases/com/univocity/univocity-parsers/[version]/univocity-parsers-[version].jar). 

Or, if you use maven, simply add the following to your `pom.xml`

```xml

...
<dependency>
	<groupId>com.univocity</groupId>
	<artifactId>univocity-parsers</artifactId>
	<version>[version]</version>
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

In the following examples, the @@LINK(example.csv) file will be used as the input. It is not as simple as you might think. 
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

#### To read all rows of a CSV (the quick and easy way) ####

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example001ParseAll)

The output will be:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example001ParseAll)

#### To read all rows of a CSV (iterator-style) ####

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example002ReadSimpleCsv)

#### Read all rows of a CSV (the powerful version) ####

To have greater control over the parsing process, use a @@LINK(RowProcessor). uniVocity-parsers provides some useful default implementations but you can always provide your own.

The following example uses @@LINK(RowListProcessor), which just stores the rows read from a file into a List:

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example003ReadCsvWithRowProcessor)

Each row will contain: 

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example003ReadCsvWithRowProcessor)

You can also use a @@LINK(ObjectRowProcessor), which will produce rows of objects. You can convert values using an implementation of the @@LINK(Conversion) interface.
The @@LINK(Conversions) class provides some useful defaults for you.
For convenience, the @@LINK(ObjectRowListProcessor) can be used to store all rows into a list. 

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example004ReadCsvAndConvertValues)

After applying the conversions, the output will be:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example004ReadCsvAndConvertValues)

### Using annotations to map your java beans ###

Use the @@LINK(Parsed) annotation to map the property to a field in the CSV file. You can map the property using a field name as declared in the headers,
or the column index in the input.

Each annotated operation maps to a @@LINK(Conversion) and they are executed in the same sequence they are declared. 

This example works with [this csv file]@@LINK(bean_test.csv)

@@INCLUDE_CLASS(/src/test/java/com/univocity/parsers/examples/TestBean)

Instances of annotated classes are created with by @@LINK(BeanProcessor) and @@LINK(BeanListProcessor):

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example005UsingAnnotations)

Here is the output produced by the `toString()` method of each @@LINK(TestBean) instance:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example005UsingAnnotations)

### Using your own conversions in annotations ###

Any implementation of @@LINK(Conversion) can be used in fields annotated with @@LINK(Parsed). The following class converts delimited Strings to a set of words (when reading) and a set of words to a delimited String with all words in the set (for writing). To do this, all you need is to introduce a varargs constructor to your class, so it can also be initialized with `String... args`:

@@INCLUDE_CLASS(/src/test/java/com/univocity/parsers/examples/WordsToSetConversion)

Let's use our beaten up example to create instances of @@LINK(Car) from all entries in @@LINK(example.csv). Now we want to split the words in the `description` field add them to a set of words. All we hate to do is this:

@@INCLUDE_CLASS(/src/test/java/com/univocity/parsers/examples/Car)

uniVocity-parsers will create an instance of @@LINK(WordsToSetConversion) using the given arguments. Now, let's use the good old @@LINK(BeanListProcessor) to parse and generate a list of @@LINK(Car)s from our file 

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example008CustomConversionAnnotation)

After executing this to print only those cars that have a description, the output will be:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example008CustomConversionAnnotation)

### Reading master-detail style files ###

Use @@LINK(MasterDetailProcessor) or @@LINK(MasterDetailListProcessor) to produce @@LINK(MasterDetailRecord) objects.
A simple example a master-detail file is in the @@LINK(master_detail.csv) file. 

Each @@LINK(MasterDetailRecord) holds a master record row and its list of associated detail rows.

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example006MasterDetail)

After printing the master row and its details rows, the output is:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example006MasterDetail)

### Parsing fixed-width files ###

All functionalities you have with the CSV file format are available for the fixed-width format (and any other parser we introduce in the future).

In the @@LINK(example.txt) fixed-width file, we chose to fill the unwritten spaces with underscores ('_'), 
so in the parser settings we set the padding to underscore: 

@@INCLUDE_CONTENT(0, /src/test/resources/examples/example.txt)

The only thing you need to do is to instantiate a different parser:
 
@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/FixedWidthParserExamples.example001ParseAll)
 
Use @@LINK(FixedWidthFieldLengths) to define what is the length of each field in the input. With that information we can then create the  @@LINK(FixedWidthParserSettings). 

The output will be: 
@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/FixedWidthParserExamples/example001ParseAll)

All the rest is the same as with CSV parsers. You can use all @@LINK(RowProcessor)s for annotations, conversions, master-detail records 
and anything else we (or you) might introduce in the future.
 
We created a set of examples using fixed with parsing in the @@LINK(FixedWidthParserExamples.java)


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

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/TsvParserExamples.example001ParseAll)

The output will be:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/TsvParserExamples/example001ParseAll)

### Column selection ###

Parsing the entire content of each record in a file is a waste of CPU and memory when you are not interested in all columns.
uniVocity-parsers lets you choose the columns you need, so values you don't want are simply bypassed.

The following examples can be found in the example class @@LINK(SettingsExamples):

Consider the @@LINK(example.csv) file with:

@@INCLUDE_CONTENT(4, /src/test/resources/examples/example.csv)

And the following selection:

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/SettingsExamples.example001ColumnSelection)

The output will be:

@@INCLUDE_CONTENT(3, /src/test/resources/examples/expectedOutputs/SettingsExamples/example001ColumnSelection)

The same output will be obtained with index-based selection.

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/SettingsExamples.example003ColumnSelectionByIndex)

You can also opt to keep the original row format with all columns, but only the values you are interested in being processed:

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/SettingsExamples.example002ColumnSelectionWithNoReordering)

Now the output will be:

@@INCLUDE_CONTENT(4, /src/test/resources/examples/expectedOutputs/SettingsExamples/example002ColumnSelectionWithNoReordering)


## Reading columns instead of rows ###

Since uniVocity-parsers 1.3.0, a few special types of @@LINK(RowProcessor)s have been introduced to collect the values of columns instead of rows:

 * @@LINK(ColumnProcessor) - reads values of all columns as plain Strings.
 * @@LINK(ObjectColumnProcessor) - reads column values as Objects. Any sequence of @@LINK(Conversion)s can be used to convert the parsed values to the desired object. 
 
To avoid problems with memory when processing large inputs, we also introduced the following column processors. These will return the column values processed after a batch of a given number of rows:
 
 * @@LINK(BatchedColumnProcessor) - 
 * @@LINK(BatchedObjectColumnProcessor)
 
Here are some examples on how to use them:

### Parsing columns from a CSV file ###

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example007Columns)

Let's see the output. Each row displays the column name and the values parsed on each:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/CsvParserExamples/example007Columns)


### Using the batched column processor in a Fixed-With input ###

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/FixedWidthParserExamples.example007BatchedColumns)

Here we print the column values from each batch of 3 rows. As we have 5 rows in the input, the last batch will have 2 values per column:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/FixedWidthParserExamples/example007BatchedColumns)


### Reading columns from a TSV while converting the parsed content to Objects ###

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/TsvParserExamples.example007ConvertColumns)

Now we will print the column indexes and their values:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/TsvParserExamples/example007ConvertColumns)

### Processing rows in parallel ###

As of uniVocity-parsers 1.4.0 you can process rows as they are parsed in a separate thread easily. All you've got to do is to wrap your @@LINK(RowProcessor) in a @@LINK(ConcurrentRowProcessor):

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/CsvParserExamples.example009ParallelProcessing)

Note that this may not always produce faster processing times. uniVocity-parsers is highly optimized and processing your data sequentially will still be faster than in parallel in many cases. 
We recommend you to profile your particular processing scenario before blindly deciding whether to use this feature.

## Settings ##

Each parser has its own settings class, but many configuration options are common across all parsers. The following snippet demonstrates how to use each one of them: 

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/SettingsExamples.example004LotsOfDifferentSettings)

The output of the CSV parser with all these settings will be:

@@INCLUDE_CONTENT(18, /src/test/resources/examples/expectedOutputs/SettingsExamples/example004LotsOfDifferentSettings)

### Fixed-width settings ###

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/SettingsExamples.example005FixedWidthSettings)

The parser output with such configuration for parsing the @@LINK(example.txt) file will be:

@@INCLUDE_CONTENT(12, /src/test/resources/examples/expectedOutputs/SettingsExamples/example005FixedWidthSettings)

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

As you can see in @@LINK(WriterExamples.java), writing is quite straightforward. All you need is an 
instance of java.io.Writer (to write the values you provide to some output resource) and a settings object with the configuration of how the values should be written.

### Quick and simple CSV writing example ###

You can write your data in CSV format using just 3 lines of code:

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example001WriteSimpleCsv)

This will produce the following output:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/WriterExamples/example001WriteSimpleCsv)

If you want to write the same content in fixed width format, all you need is to create an instance of @@LINK(FixedWidthWriter) instead. The remainder of the code remains the same.

This will be the case for any other writers/parsers we might introduce in the future, and applies to all examples presented here.

### TSV writing example ###

This is exactly the same as the CSV example seen above. All you need is to instantiate a new writer:

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example006WriteSimpleTsv)

This will produce the following output:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/WriterExamples/example006WriteSimpleTsv)

### Writing row by row, with comments ###

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example002WriteCsvOneByOne)

The output of the above code should be:

@@INCLUDE_CONTENT(4, /src/test/resources/examples/expectedOutputs/WriterExamples/example002WriteCsvOneByOne)

### Writing with column selection ###

You can write transparently to *some* fields of a CSV file, while keeping the output format consistent. Let's say you have a CSV
file with 5 columns but only have data for 3 of them, in a different order. All you have to do is configure the file headers
and select what fields you have values for. 

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example003WriteCsvWithFieldSelection)

The output of such setting will be:

@@INCLUDE_CONTENT(3, /src/test/resources/examples/expectedOutputs/WriterExamples/example003WriteCsvWithFieldSelection)

### Writing with value conversions (using ObjectRowWriterProcessor) ###

All writers have a settings object that accepts an instance of @@LINK(RowWriterProcessor). 
Use the writer methods prefixed with "processRecord" to execute the @@LINK(RowWriterProcessor) against your input. 

In the following example, we use @@LINK(ObjectRowWriterProcessor) to execute custom value conversions on each element of a row of objects.
This object executes a sequence of @@LINK(Conversion) actions on the row elements before they are written.

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example004WriteFixedWidthUsingConversions)

The output will be:

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/WriterExamples/example004WriteFixedWidthUsingConversions)

### Writing annotated java beans ###

If you have a java class with fields annotated with the annotations defined in package `com.univocity.parsers.annotations`, you can use a @@LINK(BeanWriterProcessor)
to map its attributes directly to the output.

A @@LINK(RowWriterProcessor) is just an interface that "knows" how to map a given object to a sequence of values. By default, uniVocity-parsers provides the @@LINK(BeanWriterProcessor) to map annotated beans to rows.

The following example writes instances of @@LINK(TestBean): 

@@INCLUDE_METHOD(/src/test/java/com/univocity/parsers/examples/WriterExamples.example005WriteFixedWidthUsingAnnotatedBean)

The resulting output of the above code should be: 

@@INCLUDE_CONTENT(0, /src/test/resources/examples/expectedOutputs/WriterExamples/example005WriteFixedWidthUsingAnnotatedBean)
