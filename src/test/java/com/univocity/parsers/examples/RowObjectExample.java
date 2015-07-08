package com.univocity.parsers.examples;

import java.util.Arrays;
import java.util.List;

import com.univocity.parsers.common.processor.RowContainerListProcessor;
import com.univocity.parsers.containers.Row;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.Test;

/**
 * @author naveen.kasthuri
 */
public class RowObjectExample extends Example {

  public enum CarColumn {
    Year, Make, Model, Description, Price
  }

  @Test
  public void example013ReadCsvWithRowProcessorUsingRowObjects() throws Exception {
    //##CODE_START

    // The settings object provides many configuration options
    CsvParserSettings parserSettings = new CsvParserSettings();

    //You can configure the parser to automatically detect what line separator sequence is in the input
    parserSettings.setLineSeparatorDetectionEnabled(true);

    // A RowListProcessor stores each parsed row in a List.
    RowContainerListProcessor rowProcessor = new RowContainerListProcessor();

    // You can configure the parser to use a RowProcessor to process the values of each parsed row.
    // You will find more RowProcessors in the 'com.univocity.parsers.common.processor' package, but you can also create your own.
    parserSettings.setRowProcessor(rowProcessor);

    // Let's consider the first parsed row as the headers of each column in the file.
    parserSettings.setHeaderExtractionEnabled(true);

    // creates a parser instance with the given settings
    CsvParser parser = new CsvParser(parserSettings);

    // the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
    parser.parse(getReader("/examples/example.csv"));

    // get the parsed records from the RowListProcessor here.
    // Note that different implementations of RowProcessor will provide different sets of functionalities.
    List<Row> rows = rowProcessor.getRows();

    //##CODE_END

    printAndValidate(rows);
  }

  public void printAndValidate(List<Row> rows) {

    println(Arrays.toString(rows.get(0).getHeaders().toArray()));
    println("=======================");

    // Print by row objects
    for (Row row : rows) {
      println((row.getLineNumber()) + " " + row);
      println("-----------------------");
    }

    // Print using row columns
    for (Row row : rows) {
      println(row.getLineNumber() + " Make: " + row.get(CarColumn.Make));
    }

    printAndValidate();
  }
}
