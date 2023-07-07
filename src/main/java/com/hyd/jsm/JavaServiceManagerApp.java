package com.hyd.jsm;

import com.hyd.jsm.model.JsmConf;
import com.hyd.jsm.scenes.HomeScene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

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
