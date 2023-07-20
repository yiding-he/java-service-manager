package com.hyd.jsm.command;

import org.jline.reader.ParsedLine;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Command 执行上下文
 */
public class CommandArgs {

  private final ParsedLine parsedLine;

  private final PrintWriter printWriter;

  private final Map<String, Object> args = new HashMap<>();

  public CommandArgs(ParsedLine parsedLine, PrintWriter printWriter) {
    this.parsedLine = parsedLine;
    this.printWriter = printWriter;
  }

  public ParsedLine getParsedLine() {
    return this.parsedLine;
  }

  @SuppressWarnings("unchecked")
  public <T> T getArgValue(String key) {
    return (T) this.args.get(key);
  }

  public CommandArgs putArgValue(String key, Object value) {
    this.args.put(key, value);
    return this;
  }

  public PrintWriter getPrintWriter() {
    return printWriter;
  }

  public void println(Object obj) {
    this.printWriter.println(obj);
  }
}
