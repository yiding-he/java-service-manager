package com.hyd.jsm.commands;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.model.JsmConf;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Named("查看日志")
public class JavaServiceLog extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    Path logFilePath = JsmConf.getLogFilePath(CurrentContext.currentJavaService);
    if (!Files.exists(logFilePath)) {
      return Result.fail("日志文件没有找到");
    }

    var process = new ProcessBuilder(
      List.of("tail", "-fn300", logFilePath.normalize().toAbsolutePath().toString())
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).redirectInput(
      ProcessBuilder.Redirect.PIPE
    ).start();

    var console = CurrentContext.currentConsole;
    if (console != null) {
      try {
        console.setSignalHandler(Terminal.Signal.INT, signal -> {
          process.destroyForcibly();
        });
        process.waitFor();
      } finally {
        console.setSignalHandler(Terminal.Signal.INT, null);
        console.writeLine("\n结束查看日志");
      }
    }

    return Result.success();
  }
}
