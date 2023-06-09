package com.hyd.jsm.scenes;

import com.hyd.jsm.Command;
import com.hyd.jsm.cli.Text;
import com.hyd.jsm.commands.process.JavaServiceRestart;
import com.hyd.jsm.commands.process.JavaServiceStart;
import com.hyd.jsm.commands.process.ProcessKill;
import com.hyd.jsm.util.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Named("进程管理")
public class ProcessManagementScene extends AbstractScene {

  @Autowired
  private JavaServiceStart javaServiceStart;

  @Autowired
  private ProcessKill processKill;

  @Autowired
  private JavaServiceRestart javaServiceRestart;

  @Override
  public String greetings() {
    return null;
  }

  @Override
  public List<Command> getAvailableCommands() {
    return ServiceInfoScene.processHandleAvailable() ?
      List.of(processKill, javaServiceRestart) :
      List.of(javaServiceStart);
  }
}
