package com.hyd.jsm.commands;

import com.hyd.jsm.util.Named;
import org.jline.reader.ParsedLine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Named("查看堆内存")
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
