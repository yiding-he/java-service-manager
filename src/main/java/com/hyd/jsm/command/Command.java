package com.hyd.jsm.command;

import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.jline.reader.ParsedLine;

public interface Command {

  static Command emptyCommand(String name) {
    return new Command() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Result execute(CommandArgs args) {
        return null;
      }
    };
  }

  Result execute(CommandArgs args) throws Exception;

  default boolean match(ParsedLine line) {
    var split = line.words().get(0).split("\\.", 2);
    return split.length >= 2 && split[1].startsWith(getName());
  }

  default String getName() {
    return getClass().isAnnotationPresent(Named.class) ?
      getClass().getAnnotation(Named.class).value() :
      getClass().getSimpleName();
  }
}
