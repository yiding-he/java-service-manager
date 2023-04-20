package com.hyd.jsm.scenes;

import com.hyd.jsm.Command;
import com.hyd.jsm.Scene;
import com.hyd.jsm.commands.JavaServiceStart;
import com.hyd.jsm.commands.JvmMemStat;
import com.hyd.jsm.commands.ProcessKill;
import com.hyd.jsm.config.JsmConf;
import org.jline.reader.ParsedLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServiceInfoScene extends AbstractScene {

  private static final Logger log = LoggerFactory.getLogger(ServiceInfoScene.class);

  private JsmConf.JavaService javaService;

  private ProcessHandle processHandle;

  private final Map<String, Class<? extends Command>> commands = new HashMap<>();

  {
    commands.put("内存情况", JvmMemStat.class);
    commands.put("停止进程", ProcessKill.class);
    commands.put("启动进程", JavaServiceStart.class);
  }

  public ServiceInfoScene setJavaService(JsmConf.JavaService javaService) {
    this.javaService = javaService;
    return this;
  }

  @Override
  public String greetings() {
    var servicePath = javaService.getPath();
    processHandle = ProcessHandle.allProcesses()
      .filter(h -> h.info().commandLine().map(c -> c.contains(servicePath)).orElse(false))
      .findFirst().orElse(null);

    return String.join("\n",
      "你选择了服务：" + javaService.getName(),
      (processHandle == null? "服务没有运行。": ("进程ID：" + processHandle.pid())),
      "按 TAB 查看可用操作。"
    );
  }

  @Override
  public String getPrompt() {
    return this.javaService.getName() + "> ";
  }

  @Override
  public List<String> getSelections() {
    return new ArrayList<>(this.commands.keySet());
  }

  @Override
  public Scene processCommand(ParsedLine line) {
    var command = line.word().trim();

    if (!this.commands.containsKey(command)) {
      console.writeLine("操作尚未实现：" + command);

    } else {
      if (processUnavailable() && !"启动进程".equals(command)) {
        console.writeLine("服务不在运行状态");
        return null;
      }
      try {
        var commandObj = getBean(this.commands.get(command));
        commandObj.execute(line, this.processHandle);
      } catch (Exception e) {
        log.error("", e);
      }
    }

    return null;
  }

  private boolean processUnavailable() {
    return processHandle == null || !processHandle.isAlive();
  }
}
