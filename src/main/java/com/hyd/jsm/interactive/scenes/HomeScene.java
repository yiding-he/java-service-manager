package com.hyd.jsm.interactive.scenes;

import com.hyd.jsm.command.Command;
import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.components.Text;
import com.hyd.jsm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HomeScene extends AbstractScene {

  @Autowired
  private ServiceInfoScene serviceInfoScene;

  private List<Command> availableCommands;

  @PostConstruct
  private void init() {
    this.availableCommands = jsmConf.getServices().stream().map(
      service -> new Command() {
        @Override
        public String getName() {
          return service.getName();
        }

        @Override
        public Result execute(CommandArgs args) {
          CurrentContext.currentJavaService = service;
          return Result.success().scene(serviceInfoScene);
        }
      }
    ).collect(Collectors.toList());
  }

  @Override
  public String greetings() {
    StringBuilder sb = new StringBuilder()
      .append("检查配置...\n")
      .append("已读取 ").append(jsmConf.getServices().size()).append(" 个 Java 服务配置：\n");

    jsmConf.getServices().forEach((service) ->
      sb.append(Text.of(" * " + service.getName() + " : " + service.getPath()).yellow().value().toAnsi()).append("\n"));

    sb.append("按 TAB 选择服务。");
    return sb.toString();
  }

  @Override
  public Text getPrompt() {
    return null;
  }

  @Override
  public List<Command> getAvailableCommands() {
    return availableCommands;
  }
}
