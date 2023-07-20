package com.hyd.jsm.command.commands;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
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

    var exeCommand = processHandle.info().command()
      .map(command -> {
        var binPath = Path.of(command).getParent();
        var exePath = binPath.resolve("jhsdb");
        if  (!Files.exists(exePath)) {
          exePath = binPath.resolve("jmap");
        }
        return Files.exists(exePath) ? exePath.toAbsolutePath().toString() : null;
      })
      .orElse("jhsdb");

    var fullCommand = exeCommand.endsWith("jhsdb") ?
      List.of(exeCommand, "jmap", "--heap", "--pid", String.valueOf(processHandle.pid())):
      List.of(exeCommand, "-heap", String.valueOf(processHandle.pid()));

    new ProcessBuilder(
      fullCommand
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).start().waitFor();

    return Result.success();
  }
}
