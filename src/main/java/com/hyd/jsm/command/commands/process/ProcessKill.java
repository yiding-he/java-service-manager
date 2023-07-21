package com.hyd.jsm.command.commands.process;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.hyd.jsm.CurrentContext.currentJavaService;
import static com.hyd.jsm.CurrentContext.currentProcessHandle;

@Component
@Named("停止进程")
public class ProcessKill extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var processHandle = currentProcessHandle;
    var forceKill = currentJavaService.isForceKill();

    if (processHandle == null) {
      args.println("没有指定要停止的进程。");
      return Result.success();
    }

    args.println("尝试终止进程（10秒后将强制结束进程）...");

    if (forceKill) {
      var canDestroyForcibly = processHandle.destroyForcibly();
      if (!canDestroyForcibly) {
        return Result.fail("进程无法强行结束。");
      }

    } else {
      Result normalKillResult = normalKill(args.getPrintWriter(), processHandle);
      if (normalKillResult != null) {
        return normalKillResult;
      }
    }

    return Result.success();
  }

  private static Result normalKill(
    PrintWriter printWriter, ProcessHandle processHandle
  ) throws InterruptedException, ExecutionException {

    var requested = processHandle.destroy();
    if (!requested) {
      return Result.fail("进程无法终止。");
    }

    try {
      processHandle.onExit()
        .thenAccept(h -> printWriter.println("进程已结束。"))
        .get(10, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      printWriter.println("进程结束超时，尝试强行结束");
      var canDestroyForcibly = processHandle.destroyForcibly();
      if (!canDestroyForcibly) {
        return Result.fail("进程无法强行结束。");
      }
    }

    return Result.success();
  }
}
