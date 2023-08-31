package com.hyd.jsm.domain.project;

import com.hyd.jsm.domain.JsmConf;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.ProcessCommandBuilder;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;

public class FatJarProject extends AbstractJavaProject {

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

  protected FatJarProject(JsmConf.JavaService javaService) {
    super(javaService);
  }

  @Override
  protected String getProcessKeyword() {
    return "-Dservice.name=" + javaService.getName();
  }

  @Override
  protected String buildCommandString() throws Exception {
    var root = Path.of(javaService.getPath());
    var hostName = InetAddress.getLocalHost().getHostName();
    var configDir = root.resolve(javaService.getConfigDir()).normalize().toAbsolutePath();
    var logDir = root.resolve(javaService.getLog().getLogDir()).resolve(hostName).normalize().toAbsolutePath();
    var jvmArgs = javaService.getJvmArgs() == null ? "" : javaService.getJvmArgs();
    var appArgs = javaService.getAppArgs() == null ? "" : javaService.getAppArgs();

    var jarFile = findJarFile(javaService);
    if (jarFile == null) {
      throw new IllegalStateException("服务路径 '" + javaService.getPath() + "' 下暂无 jar 包");
    } else {
      jarFile = jarFile.normalize().toAbsolutePath();
    }

    FileUtil.createDirIfNotExists(configDir);
    FileUtil.createDirIfNotExists(logDir);

    var command = new ProcessCommandBuilder(START_COMMAND_TEMPLATE)
      .replace("java_cmd", javaService.getExecution())
      .replace("jvm_args", jvmArgs)
      .replace("hostname", hostName)
      .replace("service_name", javaService.getName())
      .replace("log_dir", logDir.toString())
      .replace("log_file_name", "server-" + hostName)
      .replace("config_dir", configDir.toString())
      .replace("jar_file", jarFile.toString())
      .replace("app_args", appArgs)
      .getCommand();

    return String.join(" ", command);
  }
}
