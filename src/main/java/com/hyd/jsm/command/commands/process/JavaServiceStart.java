package com.hyd.jsm.command.commands.process;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.util.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.hyd.jsm.CurrentContext.currentProcessHandle;

@Component
@Named("启动进程")
public class JavaServiceStart extends AbstractCommand {

  public static final String DEV_NULL = "/dev/null";

  public static final String DEFAULT_CONSOLE_OUTPUT = DEV_NULL;

  public static Path findJarFile(JsmConf.JavaService javaService) {
    var dir = Path.of(javaService.getPath());
    return FileUtil.listFilesByExtension(dir, "jar").stream().findFirst().orElse(null);
  }

  public static final List<String> START_COMMAND_TEMPLATE = List.of(
    "${java_cmd}", "${jvm_args}",
    "-Dhostname=${hostname}",
    "-Dservice.name=${service_name}",
    "-Djava.security.egd=file:/dev/./urandom",
    "-Dlog.root=${log_dir}",
    "-Dlog.file=${log_file_name}",
    "-Dloader.path=${config_dir}",
    "-jar", "${jar_file}",
    "--spring.config.additional-location=${config_dir}/",
    "${app_args}"
  );

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var processHandle = currentProcessHandle;
    if (processHandle != null && processHandle.isAlive()) {
      return Result.fail("服务已经在运行中。");
    }

    var javaService = CurrentContext.currentJavaService;
    var root = Path.of(javaService.getPath());
    if (!Files.exists(root)) {
      return Result.fail("服务路径 '" + javaService.getPath() + "' 不存在");
    }

    var jarFile = findJarFile(javaService);
    if (jarFile == null) {
      return Result.fail("服务路径 '" + javaService.getPath() + "' 下暂无 jar 包");
    } else {
      jarFile = jarFile.normalize().toAbsolutePath();
    }

    var execution = javaService.getExecution();
    var hostName = InetAddress.getLocalHost().getHostName();
    var configDir = root.resolve(javaService.getConfigDir()).normalize().toAbsolutePath();
    var logDir = root.resolve(javaService.getLog().getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var jvmArgs = javaService.getJvmArgs() == null ? "" : javaService.getJvmArgs();
    var appArgs = javaService.getAppArgs() == null ? "" : javaService.getAppArgs();

    FileUtil.createDirIfNotExists(configDir);
    FileUtil.createDirIfNotExists(logDir);

    var command = new ProcessCommandBuilder(START_COMMAND_TEMPLATE)
      .replace("java_cmd", execution)
      .replace("jvm_args", jvmArgs)
      .replace("hostname", hostName)
      .replace("service_name", javaService.getName())
      .replace("log_dir", logDir.toString())
      .replace("log_file_name", "server-" + hostName)
      .replace("config_dir", configDir.toString())
      .replace("jar_file", jarFile.toString())
      .replace("app_args", appArgs)
      .getCommand();

    var commandString = String.join(" ", command);

    args.println("启动命令（供调试）:");
    args.println("========================");
    args.println(commandString);
    args.println("========================");

    args.println("服务启动中...");

    // Redirect output from stdout to file.
    var consoleOutput = DEFAULT_CONSOLE_OUTPUT;
    if (javaService.getLog().getOutput() == JsmConf.LogOutput.STDOUT) {
      if (javaService.getLog().logFileOverridden()) {
        consoleOutput = Path.of(javaService.getLog().getLogFileOverride()).normalize().toAbsolutePath().toString();
      } else {
        consoleOutput = JsmConf.getLogFilePath(javaService).normalize().toAbsolutePath().toString();
      }
    }

    new ProcessBuilder("bash", "-c", "nohup " + commandString + " > " + consoleOutput + " &")
      .directory(root.toFile())
      .redirectError(new File(DEV_NULL))
      .redirectOutput(new File(DEV_NULL))
      .redirectInput(new File(DEV_NULL))
      .start();

    Thread.sleep(3000);
    processHandle = ProcessUtil.findProcessByKeyword(jarFile.toString());
    if (processHandle != null) {
      currentProcessHandle = processHandle;
      return Result.success("服务成功启动。");
    } else {
      return Result.fail("服务启动失败。");
    }
  }
}
