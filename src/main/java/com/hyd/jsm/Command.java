package com.hyd.jsm;

import org.jline.reader.ParsedLine;

@FunctionalInterface
public interface Command {

  void execute(ParsedLine line, ProcessHandle processHandle) throws Exception;
}
