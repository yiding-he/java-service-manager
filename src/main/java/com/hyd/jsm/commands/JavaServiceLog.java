package com.hyd.jsm.commands;

import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.Named;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Named("查看日志（按 Ctrl+C 退出）")
public class JavaServiceLog extends AbstractCommand {

  @Autowired
  private ServiceInfoScene serviceInfoScene;

  @Override
  public void execute(ParsedLine line, ProcessHandle processHandle) throws Exception {

    var javaService = serviceInfoScene.getJavaService();
    var hostName = InetAddress.getLocalHost().getHostName();
    var root = Path.of(javaService.getPath());
    var logDir = root.resolve(javaService.getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var logFileName = "server-" + hostName + ".log";
    var logFilePath = logDir.resolve(logFileName);

    if (!Files.exists(logFilePath)) {
      console.writeError("日志文件没有找到");
      return;
    }

    new ProcessBuilder(
      List.of("tail", "-fn300", logFilePath.toString())
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).redirectInput(
      ProcessBuilder.Redirect.PIPE
    ).start().waitFor();
  }
}
