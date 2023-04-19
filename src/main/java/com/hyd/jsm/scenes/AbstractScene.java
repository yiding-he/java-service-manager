package com.hyd.jsm.scenes;

import com.hyd.jsm.Console;
import com.hyd.jsm.Scene;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractScene implements Scene {

  @Autowired
  protected Console console;

}
