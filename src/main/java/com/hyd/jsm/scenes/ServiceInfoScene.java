package com.hyd.jsm.scenes;

import com.hyd.jsm.Scene;
import com.hyd.jsm.config.JsmConf;
import org.jline.reader.ParsedLine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceInfoScene extends AbstractScene {

  private JsmConf.JavaService javaService;

  public ServiceInfoScene setJavaService(JsmConf.JavaService javaService) {
    this.javaService = javaService;
    return this;
  }

  @Override
  public String greetings() {
    return null;
  }

  @Override
  public String getPrompt() {
    return this.javaService.getName() + ": ";
  }

  @Override
  public List<String> getSelections() {
    return List.of("运行情况", "启动", "停止", "备份", "重启");
  }

  @Override
  public Scene processCommand(ParsedLine line) {
    return null;
  }
}
