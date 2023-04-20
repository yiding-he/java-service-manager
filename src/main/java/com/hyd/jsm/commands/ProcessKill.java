package com.hyd.jsm.commands;

import org.jline.reader.ParsedLine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ProcessKill extends AbstractCommand {

  @Override
  public void execute(ParsedLine line, ProcessHandle processHandle) throws Exception {
    var requested = processHandle.destroy();
    if (!requested) {
      console.writeLine("进程无法终止。");
      return;
    }

    processHandle.onExit()
      .thenAccept(h -> console.writeLine("进程已结束。"))
      .exceptionally(t -> {
        if (t instanceof TimeoutException) {
          console.writeLine("进程结束超时，尝试强行结束");
          var canDestroyForcibly = processHandle.destroyForcibly();
          if (!canDestroyForcibly) {
            console.writeLine("进程无法强行结束。");
          }
        }
        return null;
      })
      .get(10, TimeUnit.SECONDS);
  }
}
