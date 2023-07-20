package com.hyd.jsm.util;

import com.hyd.jsm.interactive.Scene;

public class Result {

  public static Result success() {
    return new Result(true, null);
  }

  public static Result success(String message) {
    return new Result(true, message);
  }

  public static Result fail(String message) {
    return new Result(false, message);
  }

  private final boolean success;

  private final String message;

  private Scene scene;

  public Result(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public Result scene(Scene scene) {
    this.scene = scene;
    return this;
  }

  public Scene getScene() {
    return scene;
  }

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return success;
  }
}
