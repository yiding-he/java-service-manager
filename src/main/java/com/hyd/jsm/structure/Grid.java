package com.hyd.jsm.structure;

import com.hyd.jsm.cli.Text;
import com.hyd.jsm.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Grid {

  private List<Text> columns = new ArrayList<>();

  private List<List<Text>> rows = new ArrayList<>();

  public void setColumns(List<Text> columns) {
    this.columns = columns;
  }

  public List<Text> getColumns() {
    return columns;
  }

  public void setRows(List<List<Text>> rows) {
    this.rows = rows;
  }

  public List<List<Text>> getRows() {
    return rows;
  }

  ////////////////////////////////////////

  public void printWith(PrintWriter printWriter) {
    var widths = new int[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      widths[i] = Math.max(widths[i], StrUtil.widthOfString(columns.get(i).getText()));
      for (List<Text> row : rows) {
        if (row.size() > i) {
          widths[i] = Math.max(widths[i], StrUtil.widthOfString(row.get(i).getText()));
        }
      }
    }

    var header = new StringBuilder();
    for (int i = 0; i < columns.size(); i++) {
      header.append(StrUtil.rightPad(columns.get(i).getText(), widths[i]));
      if (i < columns.size() - 1) {
        header.append("  ");
      }
    }
    printWriter.println(header.toString());
    printWriter.println(StringUtils.repeat("-", StrUtil.widthOfString(header.toString())));

    for (List<Text> row : rows) {
      var line = new StringBuilder();
      for (int i = 0; i < columns.size(); i++) {
        line.append(StrUtil.rightPad(row.size() > i ? row.get(i).getText() : "", widths[i]));
        if (i < columns.size() - 1) {
          line.append("  ");
        }
      }
      printWriter.println(line.toString());
    }
  }
}
