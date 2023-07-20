package com.hyd.jsm;

import com.hyd.jsm.interactive.Console;
import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.interactive.scenes.HomeScene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * Java 本地服务管理工具，具体的源码结构参考当前包下的 package-info.java
 */
@SpringBootApplication
@EnableConfigurationProperties(JsmConf.class)
public class JavaServiceManagerApp  {

  private static JavaServiceManagerApp instance;

  @Autowired
  private Console console;

  @PostConstruct
  private void init() {
    instance = this;
  }

  public static void main(String[] args) {
    var applicationContext = SpringApplication.run(JavaServiceManagerApp.class, args);

    JavaServiceManagerApp.instance.console.start(
      applicationContext.getBean(HomeScene.class)
    );
  }
}
