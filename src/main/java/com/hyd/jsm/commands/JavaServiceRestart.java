package com.hyd.jsm.commands;

import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.*;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Named("重新启动")
public class JavaServiceRestart extends AbstractCommand {

  @Autowired
  private ProcessKill processKill;

  @Autowired
  private JavaServiceStart javaServiceStart;

  @Autowired
  private JavaServiceLog javaServiceLog;

  @Override
  public Result execute(ParsedLine line, ProcessHandle processHandle) throws Exception {
    var killResult = processKill.execute(line, processHandle);
    if (!killResult.isSuccess()) {
      return killResult;
    }

    var startResult = javaServiceStart.execute(line, processHandle);
    if (!startResult.isSuccess()) {
      return startResult;
    }

    var logFilePath = javaServiceLog.getLogFilePath();
    return Result.success("重新启动成功，请用下面的命令查看日志\ntail -fn300 " + logFilePath.toAbsolutePath());
  }
}
