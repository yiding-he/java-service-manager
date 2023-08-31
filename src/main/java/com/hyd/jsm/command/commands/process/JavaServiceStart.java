package com.hyd.jsm.command.commands.process;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.domain.project.AbstractJavaProject;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import static com.hyd.jsm.CurrentContext.currentJavaService;

@Component
@Named("启动进程")
public class JavaServiceStart extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var javaProject = AbstractJavaProject.create(currentJavaService);
    return javaProject.run(args.getPrintWriter());
  }
}
