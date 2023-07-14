package com.hyd.jsm.commands;

import com.hyd.jsm.Command;
import com.hyd.jsm.Console;
import com.hyd.jsm.model.JsmConf;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCommand implements Command {

  @Autowired
  protected JsmConf conf;

  @Autowired
  protected Console console;
}
