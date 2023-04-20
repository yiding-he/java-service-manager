package com.hyd.jsm.commands;

import org.jline.reader.ParsedLine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JvmMemStat extends AbstractCommand {

  @Override
  public void execute(ParsedLine line, ProcessHandle processHandle) throws Exception {

    if (processHandle == null || !processHandle.isAlive()) {
      console.writeLine("进程已停止。");
      return;
    }

    new ProcessBuilder(
      List.of("jhsdb", "jmap", "--heap", "--pid", String.valueOf(processHandle.pid()))
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).start().waitFor();
  }
}
