package com.hyd.jsm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "jsm")
public class JsmConf {

  public static class JavaService {

    private String path;

    private String name;

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

  private Map<String, JavaService> services;

  public Map<String, JavaService> getServices() {
    return services;
  }

  public void setServices(Map<String, JavaService> services) {
    this.services = services;
  }
}
