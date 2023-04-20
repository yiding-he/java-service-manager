package com.hyd.jsm.commands;

import org.jline.reader.ParsedLine;
import org.springframework.stereotype.Component;

@Component
public class JavaServiceStart extends AbstractCommand {

  @Override
  public void execute(ParsedLine line, ProcessHandle processHandle) throws Exception {
    if (processHandle != null && processHandle.isAlive()) {
        console.writeLine("服务正在运行中。");
        return;
    }
    console.writeLine(".....................");
  }
}
