package com.hyd.jsm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileUtil {

  public static List<Path> listFiles(Path dir, Predicate<Path> predicate) throws IOException {
    try (var stream = Files.list(dir)) {
      return stream.filter(predicate).collect(Collectors.toList());
    }
  }

  public static List<Path> listFilesByExtension(Path dir, String extension) throws IOException {
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
