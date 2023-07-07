package com.hyd.jsm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 文件操作，将 IOException 封装为运行时异常
 */
public class FileUtil {

  public static final Comparator<Path> BY_NAME = Comparator.comparing(Path::getFileName);

  public static final Comparator<Path> BY_NEWEST = Comparator.comparing(FileUtil::getLastModifiedTime).reversed();

  public static FileTime getLastModifiedTime(Path path) {
    try {
      return Files.getLastModifiedTime(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static long getFileSize(Path path) {
    try {
      return path != null && Files.exists(path) ? Files.size(path) : 0;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Path> listFiles(Path dir) {
    return listFiles(dir, null, BY_NAME);
  }

  public static List<Path> listFiles(Path dir, Predicate<Path> predicate) {
    return listFiles(dir, predicate, BY_NAME);
  }

  public static List<Path> listFilesByNewest(Path dir) {
    return listFiles(dir, null, BY_NEWEST);
  }

  public static List<Path> listFiles(Path dir, Predicate<Path> predicate, Comparator<Path> comparator) {
    if (!Files.exists(dir)) {
      return Collections.emptyList();
    }
    try (var stream = Files.list(dir)) {
      var s = predicate == null ? stream : stream.filter(predicate);
      s = comparator == null ? s : s.sorted(comparator);
      return s.collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Path> listFilesByExtension(Path dir, String extension) {
    return listFiles(dir, path -> path.toString().endsWith("." + extension));
  }

  public static void createDirIfNotExists(Path path) {
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static String join(String... paths) {
    var p = Path.of("");
    for (var path : paths) {
      p = p.resolve(path).normalize();
    }
    return p.toAbsolutePath().toString();
  }
}
