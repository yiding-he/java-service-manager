package com.hyd.jsm.model;

import com.hyd.jsm.util.FileUtil;

import java.nio.file.Path;

public class Backup {

  private int index;

  private String absolutePath;

  private long lastModifiedTime;

  private long size;

  public Backup() {
  }

  public Backup(int index, Path path) {
    this.index = index;
    this.absolutePath = path.toAbsolutePath().toString();
    this.lastModifiedTime = FileUtil.getLastModifiedTime(path).toMillis();
    this.size = FileUtil.getFileSize(path);
  }

  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  public void setLastModifiedTime(long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }
}
