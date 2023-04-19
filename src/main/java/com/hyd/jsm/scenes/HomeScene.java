package com.hyd.jsm.scenes;

import com.hyd.jsm.Scene;
import com.hyd.jsm.config.JsmConf;
import org.jline.reader.ParsedLine;
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
  public String getPrompt() {
    return "按 TAB 选择服务: ";
  }

  @Override
  public List<String> getSelections() {
    return jsmConf.getServices().keySet().stream().sorted().collect(Collectors.toList());
  }

  @Override
  public Scene processCommand(ParsedLine line) {
    if (jsmConf.getServices().containsKey(line.word())) {
      return serviceInfoScene.setJavaService(jsmConf.getServices().get(line.word()));
    } else {
      return null;
    }
  }
}
