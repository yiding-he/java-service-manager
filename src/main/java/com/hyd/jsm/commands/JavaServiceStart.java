package com.hyd.jsm.commands;

import com.hyd.jsm.config.JsmConf;
import com.hyd.jsm.scenes.ServiceInfoScene;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.ProcessCommandBuilder;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JavaServiceStart extends AbstractCommand {

  public static final List<String> START_COMMAND_TEMPLATE = List.of(
    "nohup", "${java_cmd}", "${jvm_args}",
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
      console.writeError("服务正在运行中。");
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

    var jarFile = jarFiles.get(0).normalize().toAbsolutePath();
    var hostName = InetAddress.getLocalHost().getHostName();
    var configDir = root.resolve(javaService.getConfigDir()).normalize().toAbsolutePath();
    var logDir = root.resolve(javaService.getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var jvmArgs = javaService.getJvmArgs() == null? "": javaService.getJvmArgs();
    var appArgs = javaService.getAppArgs() == null? "": javaService.getAppArgs();

    var command = new ProcessCommandBuilder(START_COMMAND_TEMPLATE)
      .replace("java_cmd", "java")
      .replace("jvm_args", jvmArgs)
      .replace("hostname", hostName)
      .replace("service_name", javaService.getName())
      .replace("log_dir", logDir.toString())
      .replace("log_file_name", "server-" + hostName)
      .replace("config_dir", configDir.toString())
      .replace("jar_file", jarFile.toString())
      .replace("app_args", appArgs)
      .getCommand();

    console.writeLine("========================");
    console.writeLine(String.join(" ", command));
    console.writeLine("========================");

    console.writeLine("服务启动中...");
    var process = new ProcessBuilder(command)
      .redirectError(new File("/dev/null"))
      .redirectOutput(new File("/dev/null"))
      .redirectInput(new File("/dev/null"))
      .start();


    var exited = process.waitFor(3, TimeUnit.SECONDS);
    if (exited) {
      var error = new String(process.getErrorStream().readAllBytes());
      console.writeError("服务启动失败：" + error);
      return;
    }

    console.writeLine("服务成功启动。");
  }
}
