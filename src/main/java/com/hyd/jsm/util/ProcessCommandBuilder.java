package com.hyd.jsm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessCommandBuilder {

  private final ArrayList<String> command;

  public ProcessCommandBuilder(List<String> commandTemplate) {
    this.command = new ArrayList<>(commandTemplate);
  }

  public ProcessCommandBuilder replace(String var, String replacement) {
    if (replacement == null) {
      throw new IllegalArgumentException("Replacement is null for variable '" + var + "'");
    }
    command.replaceAll(s1 -> s1.replace("${" + var + "}", replacement));
    return this;
  }

  public List<String> getCommand() {
    return this.command.stream()
      .filter(s -> s != null && !s.isBlank())
      .collect(Collectors.toList());
  }
}
