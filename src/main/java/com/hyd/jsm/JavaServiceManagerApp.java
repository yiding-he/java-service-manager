package com.hyd.jsm;

import com.hyd.jsm.cli.CliConsole;
import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.interactive.InteractiveConsole;
import com.hyd.jsm.interactive.scenes.HomeScene;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Java 本地服务管理工具，具体的源码结构参考当前包下的 package-info.java
 */
@SpringBootApplication
@EnableConfigurationProperties(JsmConf.class)
public class JavaServiceManagerApp {

  private static JavaServiceManagerApp instance;

  @PostConstruct
  private void init() {
    instance = this;
  }

  public static void main(String[] args) throws Exception {
    var applicationContext = SpringApplication.run(JavaServiceManagerApp.class, args);
    var cliArgs = new CliArgs(args);
    var interactive = !cliArgs.containsArg("command");

    if (interactive) {
      InteractiveConsole console = new InteractiveConsole();
      console.start(applicationContext.getBean(HomeScene.class));

    } else {
      CliConsole console = applicationContext.getBean(CliConsole.class);
      console.processCommand(cliArgs);
    }
  }

  public static void exitWithError(String errorMessage) {
    System.err.println(errorMessage);
    System.exit(1);
  }
}
