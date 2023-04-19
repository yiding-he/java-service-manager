package com.hyd.jsm;

import com.hyd.jsm.events.SessionInitializedEvent;
import com.hyd.jsm.scenes.HomeScene;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;

@Component
public class Console {

  public enum ProcessResult {
    SUCCESS, REPEAT, TERMINATED
  }

  private Terminal terminal;

  private LineReader lineReader;

  private Scene currentScene;

  @Autowired
  private ApplicationEventPublisher publisher;

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

  public void writeLine(String s) {
    terminal.writer().println(s);
  }

  public void start(Scene startScene) {
    currentScene = startScene;
    publisher.publishEvent(new SessionInitializedEvent());

    while (true) {
      var line = lineReader.readLine(this.currentScene.getPrompt());
      var parsedLine = lineReader.getParser().parse(line, 0);
      var processResult = processCommand(this.currentScene, parsedLine);

      if (processResult == ProcessResult.TERMINATED) {
        break;
      }
    }

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

}
