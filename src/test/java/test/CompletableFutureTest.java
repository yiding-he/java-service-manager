package test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTest {

  public static void main(String[] args) {
    try {
      CompletableFuture.supplyAsync(() -> {
          throw new RuntimeException();
        }).exceptionally(e -> {
          System.err.println("Captured by exceptionally: " + e.toString());  // [1]
          return "";
        })
        .get(1, TimeUnit.SECONDS);
    } catch (Exception e) {
      System.err.println("Threw by get(): " + e); // [2]
    }
  }
}
