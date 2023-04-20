package com.hyd.jsm.scenes;

import com.hyd.jsm.Console;
import com.hyd.jsm.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class AbstractScene implements Scene {

  @Autowired
  protected Console console;

  @Autowired
  protected ApplicationContext applicationContext;

  protected <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }
}
