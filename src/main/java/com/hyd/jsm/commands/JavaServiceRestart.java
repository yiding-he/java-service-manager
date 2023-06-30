package com.hyd.jsm.commands;

import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
      console.writeLine("结束进程失败: " + killResult.getMessage());
      if (processHandle.isAlive()) {
        return killResult;
      }
    }

    var startResult = javaServiceStart.execute(line, processHandle);
    if (!startResult.isSuccess()) {
      return startResult;
    }

    var logFilePath = javaServiceLog.getLogFilePath();
    return Result.success("重新启动成功，请用下面的命令查看日志\ntail -fn300 " + logFilePath.toAbsolutePath());
  }
}
