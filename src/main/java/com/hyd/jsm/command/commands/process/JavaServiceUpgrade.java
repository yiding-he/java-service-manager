package com.hyd.jsm.command.commands.process;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.hyd.jsm.CurrentContext.currentJavaService;
import static com.hyd.jsm.CurrentContext.currentProcessHandle;
import static com.hyd.jsm.domain.project.AbstractJavaProject.*;

@Component
@Named("升级并重启")
public class JavaServiceUpgrade extends AbstractCommand {

  @Autowired
  private ProcessKill processKill;

  @Autowired
  private JavaServiceStart javaServiceStart;

  @Override
  public Result execute(CommandArgs args) throws Exception {

    // 如果配置了更新包下载地址，则下载到 upgrade 目录下
    var updateUrl = currentJavaService.getUpdateUrl();
    if (StringUtils.isNotBlank(updateUrl)) {
      tryDownloadUpdateFile(updateUrl, getUpgradeDir(currentJavaService));
    }

    // 确认 upgrade 目录下的更新包存在
    var jarFile = findJarFile(currentJavaService);
    var upgradeJarFile = findUpgradeJarFile(currentJavaService);
    if (upgradeJarFile == null) {
      return Result.fail("没有找到更新包");
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

  private void tryDownloadUpdateFile(String url, Path saveDir) throws IOException {
    var fileName = url;
    var questionMarkIndex = fileName.indexOf("?");
    var slashIndex = fileName.lastIndexOf("/");
    if (slashIndex >= 0) {
      fileName = fileName.substring(slashIndex + 1);
    }
    if (questionMarkIndex >= 0) {
      fileName = fileName.substring(0, questionMarkIndex);
    }

    Files.createDirectories(saveDir);

    try (
      var is = new URL(url).openStream();
      var os = Files.newOutputStream(saveDir.resolve(fileName));
    ) {
      StreamUtils.copy(is, os);
    }
  }
}
