package com.hyd.jsm;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

@Component
public class Console {

  private static final Logger log = LoggerFactory.getLogger(Console.class);

  public enum ProcessResult {
    SUCCESS, REPEAT, TERMINATED
  }

  private Terminal terminal;

  private LineReader lineReader;

  private Scene currentScene;

  @PostConstruct
  private void init() throws IOException {
    this.terminal = TerminalBuilder.terminal();
    this.lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(new StringsCompleter(() -> {
        if (this.currentScene == null) {
          return Collections.emptyList();
        } else {
          return this.currentScene.getSelections();
        }
      }))
      .build();
  }

  public void start(Scene startScene) {
    outputSystemInfo();

    this.currentScene = startScene;
    ProcessResult processResult = ProcessResult.SUCCESS;

    do {
      var greetings = this.currentScene.greetings();
      if (processResult == ProcessResult.SUCCESS && greetings != null) {
        terminal.writer().println(greetings);
      }

      var line = lineReader.readLine(this.currentScene.getPrompt());
      var parsedLine = lineReader.getParser().parse(line, 0);
      processResult = processCommand(this.currentScene, parsedLine);

    } while (processResult != ProcessResult.TERMINATED);

    terminal.writer().println("Bye!\n");
  }

  private ProcessResult processCommand(Scene scene, ParsedLine parsedLine) {
    if (parsedLine.word().equalsIgnoreCase("exit")) {
      return ProcessResult.TERMINATED;
    }

    var nextScene = scene.processCommand(parsedLine);
    if (nextScene == null) {
      return ProcessResult.REPEAT;
    } else {
      this.currentScene = nextScene;
      return ProcessResult.SUCCESS;
    }
  }

  private void outputSystemInfo() {
    try {
      var hostName = InetAddress.getLocalHost().getHostName();
      var writer = terminal.writer();
      writer.println("[Environment]");
      writer.println("  :: Host Name    :: " + hostName);
      writer.println("  :: OS           :: " + System.getProperty("os.name"));
      writer.println("  :: Java Version :: " + System.getProperty("java.version"));
      writer.println();
    } catch (Exception e) {
      log.error("", e);
    }
  }
}
