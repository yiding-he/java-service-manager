package com.hyd.jsm.cli;

import com.hyd.jsm.CliArgs;
import com.hyd.jsm.domain.JsmConf;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hyd.jsm.JavaServiceManagerApp.exitWithError;

@Component
public class CliConsole {

  @Autowired
  private JsmConf config;

  public void processCommand(CliArgs cliArgs) {
    var serviceName = cliArgs.getArgValue("service");
    if (StringUtils.isBlank(serviceName)) {
      exitWithError("没有在命令行参数中指定要处理的服务，请使用 --service [name] 指定要处理的服务");
    }

    var javaService = config.getServices().stream()
      .filter(s -> s.getName().equals(serviceName)).findFirst().orElse(null);

    if (javaService == null) {
      exitWithError("没有找到服务 '" + serviceName + "'");
    }

    var command = cliArgs.getArgValues("command");
    if (command.isEmpty()) {
      exitWithError("没有指定要对服务 '" + serviceName + "' 执行的操作");
    }
  }
}
