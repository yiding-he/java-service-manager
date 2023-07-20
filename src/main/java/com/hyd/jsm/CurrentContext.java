package com.hyd.jsm;

import com.hyd.jsm.interactive.Console;
import com.hyd.jsm.domain.Backup;
import com.hyd.jsm.domain.JsmConf;

import java.util.List;

public class CurrentContext {

  public static JsmConf.JavaService currentJavaService;

  public static ProcessHandle currentProcessHandle;

  public static List<Backup> currentBackups;

  public static Console currentConsole;
}
