package com.hyd.jsm.commands;

import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Named("查看日志")
public class JavaServiceLog extends AbstractCommand {

  @Autowired
  private ServiceInfoScene serviceInfoScene;

  @Override
  public Result execute(ParsedLine line, ProcessHandle processHandle) throws Exception {
    Path logFilePath = getLogFilePath();
    if (!Files.exists(logFilePath)) {
      return Result.fail("日志文件没有找到");
    }

    var process = new ProcessBuilder(
      List.of("tail", "-fn300", logFilePath.toString())
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).redirectInput(
      ProcessBuilder.Redirect.PIPE
    ).start();

    try {
      console.setSignalHandler(Terminal.Signal.INT, signal -> process.destroyForcibly());
      process.waitFor();
    } finally {
      console.setSignalHandler(Terminal.Signal.INT, null);
    }

    return Result.success();
  }

  public Path getLogFilePath() throws UnknownHostException {
    var javaService = serviceInfoScene.getJavaService();
    var hostName = InetAddress.getLocalHost().getHostName();
    var root = Path.of(javaService.getPath());
    var logDir = root.resolve(javaService.getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var logFileName = "server-" + hostName + ".log";
    return logDir.resolve(logFileName);
  }
}
