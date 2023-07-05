package com.hyd.jsm;

import com.hyd.jsm.util.Result;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Console {

  private static final Logger log = LoggerFactory.getLogger(Console.class);

  public enum ProcessResult {
    SUCCESS, REPEAT, TERMINATED
  }

  private final LinkedList<Scene> scenes = new LinkedList<>();

  private final Map<Terminal.Signal, Terminal.SignalHandler> signalHandlers = new HashMap<>();

  private Terminal terminal;

  private LineReader lineReader;

  public void setSignalHandler(Terminal.Signal signal, Terminal.SignalHandler signalHandler) {
    if (signalHandler != null) {
      this.signalHandlers.put(signal, signalHandler);
    } else {
      this.signalHandlers.remove(signal);
    }
  }

  @PostConstruct
  private void init() throws IOException {
    this.terminal = TerminalBuilder.builder()
      .nativeSignals(true)
      .signalHandler(signal -> {
        var handler = Console.this.signalHandlers.get(signal);
        if (handler != null) {
          handler.handle(signal);
        }
      })
      .build();
    this.lineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(new StringsCompleter(() -> {
        if (this.scenes.isEmpty()) {
          return Collections.emptyList();
        } else {
          return this.scenes.getLast().availableCommandNames();
        }
      }))
      .build();
  }

  public void writeLine() {
    writeLine(null);
  }

  public void writeLine(String s) {
    var writer = this.terminal.writer();
    if (s != null) {
      writer.println(s);
    } else {
      writer.println();
    }
    writer.flush();
  }

  public void writeError(String s) {
    this.terminal.writer().println(
      new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
        .append(s).toAnsi()
    );
  }

  public void start(Scene startScene) {
    outputSystemInfo();

    this.scenes.addLast(startScene);
    ProcessResult processResult = ProcessResult.SUCCESS;

    do {
      var greetings = this.scenes.getLast().greetings();
      if (processResult == ProcessResult.SUCCESS && greetings != null) {
        terminal.writer().println(greetings);
      }

      var line = lineReader.readLine(generatePrompt());
      var parsedLine = lineReader.getParser().parse(line, 0);
      processResult = processCommand(this.scenes.getLast(), parsedLine);

    } while (processResult != ProcessResult.TERMINATED);

    terminal.writer().println("Bye!\n");
  }

  private String generatePrompt() {
    return this.scenes.stream()
             .map(scene -> {
               if (scene.getPrompt() != null) {
                 return scene.getPrompt().value().toAnsi();
               } else {
                 return null;
               }
             })
             .filter(Objects::nonNull)
             .collect(Collectors.joining("/")) + " > ";
  }

  private ProcessResult processCommand(Scene scene, ParsedLine parsedLine) {

    if (parsedLine.word().equalsIgnoreCase("exit")) {
      return ProcessResult.TERMINATED;

    } else if (parsedLine.word().equalsIgnoreCase("..")) {
      if (scenes.size() > 1) {
        scenes.removeLast();
      }
      return ProcessResult.SUCCESS;
    }

    writeLine();  // For readability
    Result result;
    try {
      result = scene.execute(new CommandArgs(parsedLine));
    } catch (Exception e) {
      e.printStackTrace();
      result = Result.fail(e.getMessage());
    }
    if (result.getMessage() != null && !result.getMessage().isBlank()) {
      if (!result.isSuccess()) {
        writeError(result.getMessage());
      } else {
        writeLine(result.getMessage());
      }
    }
    var nextScene = result.getScene();
    if (nextScene == null) {
      return ProcessResult.REPEAT;
    } else {
      this.scenes.addLast(nextScene);
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
