package com.hyd.jsm.commands;

import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.ProcessCommandBuilder;
import com.hyd.jsm.util.ProcessUtil;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

  @Autowired
  private ServiceInfoScene serviceInfoScene;

  @Override
  public void execute(ParsedLine line, ProcessHandle processHandle) throws Exception {
    if (processHandle != null && processHandle.isAlive()) {
      console.writeError("服务已经在运行中。");
      return;
    }

    var javaService = serviceInfoScene.getJavaService();
    var root = Path.of(javaService.getPath());
    if (!Files.exists(root)) {
      console.writeError("服务路径 '" + javaService.getPath() + "' 不存在");
      return;
    }

    var jarFiles = FileUtil.listFilesByExtension(root, "jar");
    if (jarFiles.isEmpty()) {
      console.writeError("服务路径 '" + javaService.getPath() + "' 下暂无 jar 包");
      return;
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
      console.writeLine("服务成功启动。");
    } else {
      console.writeError("服务启动失败。");
    }
  }
}
