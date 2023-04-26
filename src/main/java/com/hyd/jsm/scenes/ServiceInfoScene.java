package com.hyd.jsm.scenes;

import com.hyd.jsm.Command;
import com.hyd.jsm.Scene;
import com.hyd.jsm.commands.JavaServiceLog;
import com.hyd.jsm.commands.JavaServiceStart;
import com.hyd.jsm.commands.JvmMemStat;
import com.hyd.jsm.commands.ProcessKill;
import com.hyd.jsm.config.JsmConf;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.ProcessUtil;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.hyd.jsm.util.ProcessUtil.findProcessByKeyword;

@Component
public class ServiceInfoScene extends AbstractScene {

  private static final Logger log = LoggerFactory.getLogger(ServiceInfoScene.class);

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private JsmConf.JavaService javaService;

  private ProcessHandle processHandle;

  private final Map<String, Class<? extends Command>> commands = new HashMap<>();

  {
    commands.put("1", JavaServiceStart.class);
    commands.put("2", JvmMemStat.class);
    commands.put("3", ProcessKill.class);
    commands.put("4", JavaServiceLog.class);
  }

  public ServiceInfoScene setJavaService(JsmConf.JavaService javaService) {
    this.javaService = javaService;
    return this;
  }

  private boolean processHandleAvailable() {
    return processHandle != null && processHandle.isAlive();
  }

  public JsmConf.JavaService getJavaService() {
    return javaService;
  }

  @Override
  public String greetings() {
    var servicePath = javaService.getPath();
    processHandle = findProcessByKeyword(FileUtil.join(servicePath, "config"));

    var greetings = new ArrayList<>(List.of("你选择了服务：" + javaService.getName()));
    greetings.add("运行路径：" + javaService.getPath());

    if (processHandleAvailable()) {
      greetings.add("进程ID：" + processHandle.pid());
      greetings.add("进程运行时间：" +
        processHandle.info().startInstant()
          .map(i -> ZonedDateTime.ofInstant(i, ZoneId.systemDefault()))
          .map(DATE_TIME_FORMATTER::format)
          .orElse("未知"));
    } else {
      greetings.add("服务没有运行。");
    }

    greetings.add("按 TAB 查看可用操作。");
    return String.join("\n",greetings);
  }

  @Override
  public AttributedStringBuilder getPrompt() {
    var color = processHandleAvailable() ? AttributedStyle.GREEN : AttributedStyle.RED;
    return new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(color))
      .append(this.javaService.getName())
      .append("> ");
  }

  @Override
  public List<String> getSelections() {
    return this.commands.entrySet().stream()
      .sorted(Comparator.comparing(entry -> Integer.parseInt(entry.getKey())))
      .map(entry -> entry.getKey() + "." + entry.getValue().getAnnotation(Named.class).value())
      .collect(Collectors.toList());
  }

  @Override
  public Scene processCommand(ParsedLine line) {
    var command = line.word().trim();
    if (command.isEmpty()) {
      return null;
    }

    var key = command.split("\\.")[0];
    if (!this.commands.containsKey(key)) {
      console.writeLine("操作尚未实现：" + command);

    } else {
      Class<? extends Command> commandType = this.commands.get(key);
      if (processUnavailable() && commandType != JavaServiceStart.class) {
        console.writeLine("服务不在运行状态");
        return null;
      }
      try {
        var commandObj = getBean(commandType);
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
