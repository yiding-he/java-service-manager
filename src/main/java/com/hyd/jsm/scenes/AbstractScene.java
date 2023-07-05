package com.hyd.jsm.scenes;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.Console;
import com.hyd.jsm.Scene;
import com.hyd.jsm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractScene implements Scene {

  @Autowired
  protected Console console;

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var command = matchCommand(args.getParsedLine());
    if (command == null) {
      if (!args.getParsedLine().line().isBlank()) {
        console.writeLine("操作尚未实现：" + args.getParsedLine().line());
      }
      return Result.success();
    } else if (command instanceof Scene) {
      return Result.success().scene((Scene) command);
    } else {
      return command.execute(args);
    }
  }
}
