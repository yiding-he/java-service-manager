package com.hyd.jsm.commands.process;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.commands.AbstractCommand;
import com.hyd.jsm.commands.JavaServiceLog;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;

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
  public Result execute(CommandArgs args) throws Exception {
    var killResult = processKill.execute(args);
    if (!killResult.isSuccess()) {
      console.writeLine("结束进程失败: " + killResult.getMessage());
      if (currentProcessHandle.isAlive()) {
        return killResult;
      }
    }

    var startResult = javaServiceStart.execute(args);
    if (!startResult.isSuccess()) {
      return startResult;
    }

    var logFilePath = javaServiceLog.getLogFilePath();
    return Result.success("重新启动成功，请用下面的命令查看日志\ntail -fn300 " + logFilePath.toAbsolutePath());
  }
}
