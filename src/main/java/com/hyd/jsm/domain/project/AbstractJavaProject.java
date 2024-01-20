package com.hyd.jsm.domain.project;

import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.ProcessUtil;
import com.hyd.jsm.util.Result;

import java.io.File;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;

public abstract class AbstractJavaProject {

  public static final String DEV_NULL = "/dev/null";

  public static final String DEFAULT_CONSOLE_OUTPUT = DEV_NULL;

  public static AbstractJavaProject create(JsmConf.JavaService javaService) {

    var root = Path.of(javaService.getPath());
    if (!Files.exists(root)) {
      throw new IllegalArgumentException("服务路径 '" + javaService.getPath() + "' 不存在");
    }

    var libDir = root.resolve("lib");
    if (!Files.exists(libDir)) {
      return new FatJarProject(javaService);
    } else {
      return new LibJarProject(javaService);
    }
  }

  public static Path findJarFile(JsmConf.JavaService javaService) {
    var dir = Path.of(javaService.getPath());
    return findJarFile(dir);
  }

  public static Path findUpgradeJarFile(JsmConf.JavaService javaService) {
    var dir = getUpgradeDir(javaService);
    return findJarFile(dir);
  }

  public static Path getUpgradeDir(JsmConf.JavaService javaService) {
      return Path.of(javaService.getPath()).resolve("upgrade");
  }

  private static Path findJarFile(Path dir) {
    return Files.exists(dir)?
      FileUtil.listFilesByExtension(dir, "jar").stream().findFirst().orElse(null) : null;
  }

  protected final JsmConf.JavaService javaService;

  protected AbstractJavaProject(JsmConf.JavaService javaService) {
    this.javaService = javaService;
  }

  ////////////////////////////////////////

  public Result run(PrintWriter ioPrinter) throws Exception {
    var processHandle = currentProcessHandle;
    if (processHandle != null && processHandle.isAlive()) {
      return Result.fail("服务已经在运行中。");
    }

    var commandString = buildCommandString();
    ioPrinter.println("启动命令（供调试）:");
    ioPrinter.println("========================");
    ioPrinter.println(commandString);
    ioPrinter.println("========================");
    ioPrinter.println("服务启动中...");
    ioPrinter.flush();

    // Redirect output from stdout to file.
    var output = parseOutput();

    var root = Path.of(javaService.getPath());
    new ProcessBuilder("bash", "-c", "nohup " + commandString + " > " + output + " &")
      .directory(root.toFile())
      .redirectError(new File(DEV_NULL))
      .redirectOutput(new File(DEV_NULL))
      .redirectInput(new File(DEV_NULL))
      .start();

    Thread.sleep(3000);
    processHandle = ProcessUtil.findProcessByKeyword(getProcessKeyword());
    if (processHandle != null) {
      currentProcessHandle = processHandle;
      return Result.success("服务成功启动。");
    } else {
      return Result.fail("服务启动失败。");
    }

  }

  private String parseOutput() throws UnknownHostException {
    var consoleOutput = DEFAULT_CONSOLE_OUTPUT;
    if (javaService.getLog().getOutput() == JsmConf.LogOutput.STDOUT) {
      if (javaService.getLog().logFileOverridden()) {
        consoleOutput = Path.of(javaService.getLog().getLogFileOverride()).normalize().toAbsolutePath().toString();
      } else {
        consoleOutput = JsmConf.getLogFilePath(javaService).normalize().toAbsolutePath().toString();
      }
    }
    return consoleOutput;
  }

  protected abstract String getProcessKeyword();

  protected abstract String buildCommandString() throws Exception;
}
