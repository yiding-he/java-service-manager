package com.hyd.jsm.interactive.scenes;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.components.Text;
import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.interactive.Scene;
import com.hyd.jsm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractScene implements Scene {

  @Autowired
  protected JsmConf jsmConf;

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var command = matchCommand(args.getParsedLine());
    if (command == null) {
      if (!args.getParsedLine().line().isBlank()) {
        args.println("操作尚未实现：" + args.getParsedLine().line());
      }
      return Result.success();
    } else if (command instanceof Scene) {
      return Result.success().scene((Scene) command);
    } else {
      return command.execute(args);
    }
  }

  @Override
  public Text getPrompt() {
    return Text.of(getName());
  }
}
