package com.hyd.jsm;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * Parsed command line arguments.
 */
public class CliArgs {

  private final Map<String, List<String>> data = new HashMap<>();

  public CliArgs(String[] tokens) {
    String currentArgName = null;
    for (String token : tokens) {
      if (token.startsWith("-")) {
        token = token.replaceAll("^-+", "");
        if (!Objects.equals(token, currentArgName)) {
            addArg(currentArgName, null);
        }
        if (token.contains("=")) {
          var argNameAndValue = token.split("=", 2);
          currentArgName = argNameAndValue[0];
          addArg(currentArgName, argNameAndValue[1]);
        } else {
          currentArgName = token;
          addArg(currentArgName, null);
        }
      } else if (currentArgName != null) {
        addArg(currentArgName, token);
      }
    }
  }

  private void addArg(String argName, String argValue) {
    if (argName == null) {
      return;
    }
    if (argValue == null) {
      this.data.putIfAbsent(argName, emptyList());
    } else {
      this.data.computeIfAbsent(argName, k -> new ArrayList<>()).add(argValue);
    }
  }

  public Map<String, List<String>> getData() {
    return data;
  }

  public List<String> getArgValues(String argName) {
    return data.getOrDefault(argName, emptyList());
  }

  public String getArgValue(String argName) {
    var argValues = getArgValues(argName);
    return argValues.isEmpty() ? null : argValues.get(0);
  }

  public boolean containsArg(String argName) {
    return data.containsKey(argName);
  }
}
