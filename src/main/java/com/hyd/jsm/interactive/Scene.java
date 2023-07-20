package com.hyd.jsm.interactive;

import com.hyd.jsm.components.Text;
import com.hyd.jsm.command.Command;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Scene extends Command {

  String greetings();

  Text getPrompt();

  List<Command> getAvailableCommands();

  default List<String> availableCommandNames() {
    var availableCommands = getAvailableCommands();
    if (availableCommands == null || availableCommands.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<>();
    for (int i = 0; i < availableCommands.size(); i++) {
      Command command = availableCommands.get(i);
      result.add((i + 1) + "." + command.getName());
    }
    return result;
  }

  default Command matchCommand(ParsedLine parsedLine) {
    var availableCommands = getAvailableCommands();
      if (availableCommands == null) {
          return null;
      }

    return availableCommands.stream()
      .filter(c -> c.match(parsedLine))
      .findFirst().orElse(null);
  }

}
