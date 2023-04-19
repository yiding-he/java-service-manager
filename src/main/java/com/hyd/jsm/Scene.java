package com.hyd.jsm;

import org.jline.reader.ParsedLine;

import java.util.List;

public interface Scene {

  String getPrompt();

  List<String> getSelections();

  Scene processCommand(ParsedLine line);
}
