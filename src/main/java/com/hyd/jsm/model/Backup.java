package com.hyd.jsm.model;

import com.hyd.jsm.util.FileUtil;

import java.nio.file.Path;

public class Backup {

  private int index;

  private String fileName;

  private long lastModifiedTime;

  private long size;

  public Backup() {
  }

  public Backup(int index, Path path) {
    this.index = index;
    this.fileName = path.getFileName().toString();
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

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }
}
