package com.hyd.jsm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "jsm")
public class JsmConf {

  public static class JavaService {

    private String execution = "java";

    private String path;

    private String name;

    private String configDir = "config";

    private String logDir = "logs";

    private String jvmArgs;

    private String appArgs;

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

    public String getLogDir() {
      return logDir;
    }

    public void setLogDir(String logDir) {
      this.logDir = logDir;
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

  private Map<String, JavaService> services = new HashMap<>();

  public Map<String, JavaService> getServices() {
    return services;
  }

  public void setServices(Map<String, JavaService> services) {
    this.services = services;
  }
}
