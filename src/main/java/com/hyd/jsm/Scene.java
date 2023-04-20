package com.hyd.jsm;

import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedStringBuilder;

import java.util.List;

public interface Scene {

  String greetings();

  AttributedStringBuilder getPrompt();

  List<String> getSelections();

  Scene processCommand(ParsedLine line);
}
