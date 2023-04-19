package com.hyd.jsm;

import com.hyd.jsm.config.JsmConf;
import com.hyd.jsm.events.SessionInitializedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ServiceManager {

  @Autowired
  private Console console;

  @Autowired
  private JsmConf jsmConf;

  @EventListener
  public void onSessionInitialized(SessionInitializedEvent event) {
    console.writeLine("检查配置...");
    console.writeLine("已读取 " + jsmConf.getServices().size() + " 个 Java 服务配置：");
    jsmConf.getServices().forEach((serviceName, service) -> {
      service.setName(serviceName);
      console.writeLine("  - " + serviceName);
    });
  }
}
