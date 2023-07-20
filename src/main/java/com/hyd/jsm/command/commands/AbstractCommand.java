package com.hyd.jsm.command.commands;

import com.hyd.jsm.command.Command;
import com.hyd.jsm.domain.JsmConf;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCommand implements Command {

  @Autowired
  protected JsmConf conf;

}
