package com.hyd.jsm.commands;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;

@Component
@Named("停止进程")
public class ProcessKill extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    console.writeLine("尝试终止进程（10秒后将强制结束进程）...");

    var processHandle = currentProcessHandle;
    var requested = processHandle.destroy();
    if (!requested) {
      return Result.fail("进程无法终止。");
    }

    try {
      processHandle.onExit()
        .thenAccept(h -> console.writeLine("进程已结束。"))
        .get(10, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      console.writeLine("进程结束超时，尝试强行结束");
      var canDestroyForcibly = processHandle.destroyForcibly();
      if (!canDestroyForcibly) {
        return Result.fail("进程无法强行结束。");
      }
    }

    return Result.success();
  }
}
