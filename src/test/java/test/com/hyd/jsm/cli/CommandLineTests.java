package test.com.hyd.jsm.cli;

import com.hyd.jsm.JavaServiceManagerApp;
import org.junit.jupiter.api.Test;

public class CommandLineTests {

  @Test
  public void test1() throws Exception {
    JavaServiceManagerApp.main(new String[]{"--command"});
  }

  @Test
  public void test2() throws Exception {
    JavaServiceManagerApp.main(new String[]{
      "--service=service1", "--command"
    });
  }
}
