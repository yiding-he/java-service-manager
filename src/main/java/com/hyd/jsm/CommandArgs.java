package com.hyd.jsm;

import org.jline.reader.ParsedLine;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class CommandArgs extends HashMap<String, Object> {

  public CommandArgs(ParsedLine parsedLine) {
    putValue("line", parsedLine);
  }

  public ParsedLine getParsedLine() {
    return getValue("line");
  }

  public boolean matchCommandName(String commandName) {
    var parsedLine = getParsedLine();
    if (parsedLine == null) {
      return false;
    }
    var split = parsedLine.words().get(0).split("\\.", 2);
    return split[1].startsWith(commandName);
  }

  public <T> T getValue(String key) {
    return (T) get(key);
  }

  public CommandArgs putValue(String key, Object value) {
    put(key, value);
    return this;
  }
}
