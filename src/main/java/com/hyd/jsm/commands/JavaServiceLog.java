package com.hyd.jsm.commands;

import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.KeyPressUtil;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
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

    var tailProcess = new ProcessBuilder(
      List.of("tail", "-fn300", logFilePath.toString())
    ).redirectOutput(
      ProcessBuilder.Redirect.INHERIT
    ).start();

    var keyMap = new KeyMap<Keys>();
    keyMap.bind(Keys.EXIT, KeyPressUtil.ctrl('z'));

    var reader = console.newBindingReader();
    Keys keys;
    do {
      keys = reader.readBinding(keyMap);
    } while (keys != Keys.EXIT);

    tailProcess.destroy();
  }

  public enum Keys {
    EXIT
  }
}
