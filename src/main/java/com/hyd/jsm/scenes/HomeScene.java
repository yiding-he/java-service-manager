package com.hyd.jsm.scenes;

import com.hyd.jsm.Scene;
import com.hyd.jsm.config.JsmConf;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HomeScene extends AbstractScene {

  @Autowired
  private JsmConf jsmConf;

  @Autowired
  private ServiceInfoScene serviceInfoScene;

  @Override
  public String greetings() {
    StringBuilder sb = new StringBuilder()
      .append("检查配置...\n")
      .append("已读取 ").append(jsmConf.getServices().size()).append(" 个 Java 服务配置：\n");

    jsmConf.getServices().forEach((service) -> {
      sb.append("  * ").append(service.getName()).append(" : ").append(service.getPath()).append("\n");
    });

    return sb.toString();
  }

  @Override
  public AttributedStringBuilder getPrompt() {
    return new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
      .append("按 TAB 选择服务> ");
  }

  @Override
  public List<String> getSelections() {
    return jsmConf.getServices().stream()
      .map(JsmConf.JavaService::getName)
      .collect(Collectors.toList());
  }

  @Override
  public Scene processCommand(ParsedLine line) {
    return jsmConf.getServices().stream()
      .filter(s -> s.getName().equals(line.word()))
      .findFirst()
      .map(s -> serviceInfoScene.setJavaService(s))
      .orElse(null);
  }
}
