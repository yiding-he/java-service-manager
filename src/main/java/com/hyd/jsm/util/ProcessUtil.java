package com.hyd.jsm.util;

public class ProcessUtil {

  public static ProcessHandle findProcessByKeyword(String keyword) {
    return ProcessHandle.allProcesses()
      .filter(h -> h.info().commandLine().map(c -> c.contains(keyword)).orElse(false))
      .findFirst().orElse(null);
  }
}
