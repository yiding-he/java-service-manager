package com.hyd.jsm.scenes;

import com.hyd.jsm.Command;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.cli.Text;
import com.hyd.jsm.commands.JavaServiceLog;
import com.hyd.jsm.commands.JvmMemStat;
import com.hyd.jsm.util.FileUtil;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;
import static com.hyd.jsm.util.ProcessUtil.findProcessByKeyword;

@Component
public class ServiceInfoScene extends AbstractScene {

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private JavaServiceLog javaServiceLog;

  @Autowired
  private JvmMemStat jvmMemStat;

  @Autowired
  private ProcessManagementScene processManagementScene;

  public static boolean processHandleAvailable() {
    return currentProcessHandle != null && currentProcessHandle.isAlive();
  }

  @Override
  public String greetings() {
    var javaService = CurrentContext.currentJavaService;
    var servicePath = javaService.getPath();
    currentProcessHandle = findProcessByKeyword(FileUtil.join(servicePath, "config"));

    var greetings = new ArrayList<>(List.of("你选择了服务：" + javaService.getName()));
    greetings.add("  运行路径：" + javaService.getPath());

    if (processHandleAvailable()) {
      greetings.add("  进程ID：" + currentProcessHandle.pid());
      greetings.add("  进程运行时间：" +
                    currentProcessHandle.info().startInstant()
                      .map(i -> ZonedDateTime.ofInstant(i, ZoneId.systemDefault()))
                      .map(DATE_TIME_FORMATTER::format)
                      .orElse("未知"));
    } else {
      greetings.add("服务没有运行。");
    }

    greetings.add("按 TAB 查看可用操作，输入 \"..\" 回到服务选择，输入 \"exit\" 退出管理工具。");
    return String.join("\n", greetings);
  }

  @Override
  public Text getPrompt() {
    var color = processHandleAvailable() ? AttributedStyle.GREEN : AttributedStyle.RED;
    return Text.of(CurrentContext.currentJavaService.getName()).color(color);
  }

  @Override
  public List<Command> getAvailableCommands() {
    return List.of(jvmMemStat, javaServiceLog, processManagementScene);
  }

}
