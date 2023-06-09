package com.hyd.jsm.commands;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;

@Component
@Named("查看堆内存")
public class JvmMemStat extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {

    var processHandle = currentProcessHandle;
    if (processHandle == null || !processHandle.isAlive()) {
      return Result.fail("进程已停止。");
    }

    var jhsdbCommand = processHandle.info().command()
      .map(command -> Path.of(command).getParent().resolve("jhsdb").toAbsolutePath().toString())
      .orElse("jhsdb");

    new ProcessBuilder(
      List.of(jhsdbCommand, "jmap", "--heap", "--pid", String.valueOf(processHandle.pid()))
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).start().waitFor();

    return Result.success();
  }
}
