package com.hyd.jsm.commands;

import com.hyd.jsm.Command;
import com.hyd.jsm.Console;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCommand implements Command {

  @Autowired
  protected Console console;
}
