package com.hyd.jsm.commands;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.CurrentContext;
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

    var jarFiles = FileUtil.listFilesByExtension(root, "jar");
    if (jarFiles.isEmpty()) {
      return Result.fail("服务路径 '" + javaService.getPath() + "' 下暂无 jar 包");
    }

    var execution = javaService.getExecution();
    var jarFile = jarFiles.get(0).normalize().toAbsolutePath();
    var hostName = InetAddress.getLocalHost().getHostName();
    var configDir = root.resolve(javaService.getConfigDir()).normalize().toAbsolutePath();
    var logDir = root.resolve(javaService.getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var jvmArgs = javaService.getJvmArgs() == null? "": javaService.getJvmArgs();
    var appArgs = javaService.getAppArgs() == null? "": javaService.getAppArgs();

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

    console.writeLine("启动命令（供调试）:");
    console.writeLine("========================");
    console.writeLine(commandString);
    console.writeLine("========================");

    console.writeLine("服务启动中...");
    new ProcessBuilder("bash", "-c", "nohup " + commandString + " > /dev/null &")
      .directory(root.toFile())
      .redirectError(new File("/dev/null"))
      .redirectOutput(new File("/dev/null"))
      .redirectInput(new File("/dev/null"))
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
