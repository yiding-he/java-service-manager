package com.hyd.jsm.cli;

import com.hyd.jsm.CliArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.command.commands.process.JavaServiceRestart;
import com.hyd.jsm.command.commands.process.JavaServiceStart;
import com.hyd.jsm.command.commands.process.ProcessKill;
import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.ProcessUtil;
import com.hyd.jsm.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

import static com.hyd.jsm.JavaServiceManagerApp.exitWithError;

@Component
public class CliConsole {

  @Autowired
  private JsmConf config;

  @Autowired
  private JavaServiceStart javaServiceStart;

  @Autowired
  private JavaServiceRestart javaServiceRestart;

  @Autowired
  private ProcessKill processKill;

  public void processCommand(CliArgs cliArgs) throws Exception {
    var serviceName = cliArgs.getArgValue("service");
    if (StringUtils.isBlank(serviceName)) {
      exitWithError("没有在命令行参数中指定要处理的服务，请使用 --service [name] 指定要处理的服务");
    }

    var javaService = config.getServices().stream()
      .filter(s -> s.getName().equals(serviceName)).findFirst().orElse(null);

    if (javaService == null) {
      exitWithError("没有找到服务 '" + serviceName + "'");
    }

    // 获取服务进程信息
    CurrentContext.currentJavaService = javaService;
    CurrentContext.currentProcessHandle =
      ProcessUtil.findProcessByKeyword(FileUtil.join(javaService.getPath(), "config"));

    var commands = cliArgs.getArgValues("command");
    if (commands.isEmpty()) {
      exitWithError("没有指定要对服务 '" + serviceName + "' 执行的操作");
    }

    for (String cmd : commands) {
      var commandArgs = new CommandArgs(null, new PrintWriter(System.out, true));
      Result result = Result.fail("未识别的命令 " + cmd);

      switch (cmd) {
        case "start":
          result = javaServiceStart.execute(commandArgs);
          break;
        case "stop":
          result = processKill.execute(commandArgs);
          break;
        case "restart":
          result = javaServiceRestart.execute(commandArgs);
          break;
      }

      if (!result.isSuccess()) {
        exitWithError(result.getMessage());
      }
    }
  }
}
