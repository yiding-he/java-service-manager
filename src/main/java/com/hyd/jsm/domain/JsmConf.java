package com.hyd.jsm.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for Java Service Manager.
 */
@ConfigurationProperties(prefix = "jsm")
public class JsmConf {

  public enum LogOutput {
    /**
     * Log will be printed to stdout.
     */
    STDOUT,
    /**
     * Log will be printed to files according to logging configuration.
     */
    FILE
  }

  public static class LogConf {

    private LogOutput output = LogOutput.FILE;

    private String logDir = "logs";

    /**
     * Override the default log file path if it is not
     * under {@code [logDir]/[hostname]/server-[hostname].log}.
     * {@link #logDir} will be ignored if this is set.
     */
    private String logFileOverride;

    public LogOutput getOutput() {
      return output;
    }

    public void setOutput(LogOutput output) {
      this.output = output;
    }

    public String getLogDir() {
      return logDir;
    }

    public void setLogDir(String logDir) {
      this.logDir = logDir;
    }

    public String getLogFileOverride() {
      return logFileOverride;
    }

    public void setLogFileOverride(String logFileOverride) {
      this.logFileOverride = logFileOverride;
    }

    public boolean logFileOverridden() {
      return StringUtils.isNotBlank(logFileOverride);
    }
  }

  public static class JavaService {

    private String execution = "java";

    private String path;

    private String name;

    private String configDir = "config";

    private String backupDir = "backups";

    private String jvmArgs;

    private String mainClass;

    private String appArgs;

    private boolean forceKill;

    private String updateUrl;

    private LogConf log = new LogConf();

    ////////////////////////////////////////

    public String getUpdateUrl() {
      return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
      this.updateUrl = updateUrl;
    }

    public String getMainClass() {
      return mainClass;
    }

    public void setMainClass(String mainClass) {
      this.mainClass = mainClass;
    }

    public boolean isForceKill() {
      return forceKill;
    }

    public void setForceKill(boolean forceKill) {
      this.forceKill = forceKill;
    }

    public LogConf getLog() {
      return log;
    }

    public void setLog(LogConf log) {
      this.log = log;
    }

    public String getBackupDir() {
      return backupDir;
    }

    public void setBackupDir(String backupDir) {
      this.backupDir = backupDir;
    }

    public String getExecution() {
      return execution;
    }

    public void setExecution(String execution) {
      this.execution = execution;
    }

    public String getAppArgs() {
      return appArgs;
    }

    public void setAppArgs(String appArgs) {
      this.appArgs = appArgs;
    }

    public String getJvmArgs() {
      return jvmArgs;
    }

    public void setJvmArgs(String jvmArgs) {
      this.jvmArgs = jvmArgs;
    }

    public String getConfigDir() {
      return configDir;
    }

    public void setConfigDir(String configDir) {
      this.configDir = configDir;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPath() {
      return path == null ? null : path.endsWith("/") ? path : (path + "/");
    }

    public void setPath(String path) {
      this.path = path;
    }
  }

  private List<JavaService> services = new ArrayList<>();

  public List<JavaService> getServices() {
    return services;
  }

  public void setServices(List<JavaService> services) {
    this.services = services;
  }

  ////////////////////////////////////////

  public static Path getLogFilePath(JsmConf.JavaService javaService) throws UnknownHostException {

    var logFilePathOverride = javaService.getLog().getLogFileOverride();
    if (StringUtils.isNotBlank(logFilePathOverride)) {
      return Path.of(logFilePathOverride);
    }

    var hostName = InetAddress.getLocalHost().getHostName();
    var logFileName = "server-" + hostName + ".log";

    return Path.of(
      javaService.getPath(),
      javaService.getLog().getLogDir(),
      hostName,
      logFileName
    ).normalize().toAbsolutePath();
  }

}
