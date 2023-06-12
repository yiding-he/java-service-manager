package com.hyd.jsm;

import com.hyd.jsm.util.Result;
import org.jline.reader.ParsedLine;

public interface Command {

  Result execute(ParsedLine line, ProcessHandle processHandle) throws Exception;
}
