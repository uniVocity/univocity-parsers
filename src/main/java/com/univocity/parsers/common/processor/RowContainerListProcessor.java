package com.univocity.parsers.common.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.Format;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.containers.RecordMetaData;
import com.univocity.parsers.containers.Row;

/**
 * @author naveen.kasthuri
 */
public class RowContainerListProcessor implements RowProcessor {

  private List<Row> rows;
  private RecordMetaData metaData;
  /**
   * {@inheritDoc}
   */
  @Override
  public void processStarted(ParsingContext context) {
    metaData = new RecordMetaData();
    rows = new ArrayList<Row>(100);
  }

  /**
   * Stores the row extracted by the parser into a list.
   *
   * @param row the data extracted by the parser for an individual record. Note that:
   * <ul>
   * <li>it will never by null. </li>
   * <li>it will never be empty unless explicitly configured using {@link CommonSettings#setSkipEmptyLines(boolean)}</li>
   * <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link Format#setComment(char)} to '\0'</li>
   * </ul>
   * @param context A contextual object with information and controls over the current state of the parsing process
   */
  @Override
  public void rowProcessed(String[] row, ParsingContext context) {
    rows.add(new Row(row, context.currentRecord(), metaData));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processEnded(ParsingContext context) {
  }

  /**
   * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
   * @return the headers of all records parsed.
   */
  public Set getHeaders() {
    return metaData.getHeaders();
  }

  /**
   * Creates headers for the associated collection of rows.
   * @param enumType Enum.class object for the list of enums corresponding to the headers.
   */
  public void buildHeaders(Class<? extends Enum> enumType) {
    metaData.buildHeaders(enumType);
  }

  /**
   * @return list of {@link Row} objects, one for each row of the csv file.
   */
  public List<Row> getRows() {
    return rows;
  }
}
