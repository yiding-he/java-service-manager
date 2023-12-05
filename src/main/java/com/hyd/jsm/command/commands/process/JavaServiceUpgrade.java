package com.hyd.jsm.command.commands.process;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.domain.project.AbstractJavaProject;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.hyd.jsm.CurrentContext.currentJavaService;
import static com.hyd.jsm.CurrentContext.currentProcessHandle;

@Component
@Named("升级并重启")
public class JavaServiceUpgrade extends AbstractCommand {

  @Autowired
  private ProcessKill processKill;

  @Autowired
  private JavaServiceStart javaServiceStart;

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var jarFile = AbstractJavaProject.findJarFile(currentJavaService);
    var upgradeJarFile = AbstractJavaProject.findUpgradeJarFile(currentJavaService);
    if (upgradeJarFile == null) {
      return Result.fail("没有找到升级包");
    }

    var killResult = processKill.execute(args);
    if (!killResult.isSuccess()) {
      args.println("结束进程失败: " + killResult.getMessage());
      if (currentProcessHandle.isAlive()) {
        return killResult;
      }
    }

    try {
      Files.copy(upgradeJarFile, jarFile, StandardCopyOption.REPLACE_EXISTING);
      args.println("升级文件拷贝完成");
    } catch (Exception e) {
      return Result.fail("升级失败: " + e);
    }

    var startResult = javaServiceStart.execute(args);
    if (!startResult.isSuccess()) {
      return startResult;
    }

    return Result.success("重新启动成功。");
 }
}
