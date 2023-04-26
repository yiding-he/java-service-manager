package com.hyd.jsm;

import org.jline.reader.ParsedLine;

public interface Command {

  void execute(ParsedLine line, ProcessHandle processHandle) throws Exception;
}
