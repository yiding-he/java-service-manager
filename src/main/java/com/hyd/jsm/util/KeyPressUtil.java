package com.hyd.jsm.util;

public class KeyPressUtil {

  public static String esc() {
    return "\033";
  }

  public static String alt(char c) {
    return "\033" + c;
  }

  public static String alt(String c) {
    return "\033" + c;
  }

  public static String del() {
    return "\177";
  }

  public static String ctrl(char key) {
    return key == '?' ? del() : Character.toString((char) (Character.toUpperCase(key) & 0x1f));
  }

}
