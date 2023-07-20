package com.hyd.jsm.command.commands.backup;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.command.commands.process.JavaServiceStart;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Named("创建备份")
public class CreateBackup extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var javaService = CurrentContext.currentJavaService;
    var jarFile = JavaServiceStart.findJarFile(javaService);
    if (jarFile == null) {
      return Result.fail("没有找到需要备份的 jar 文件");
    }

    var backupDir = Path.of(javaService.getPath(), javaService.getBackupDir());
    FileUtil.createDirIfNotExists(backupDir);
    var backupFile = backupDir.resolve(
      jarFile.getFileName() + "-bak-" +
      DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd-HHmmss"));
    Files.copy(jarFile, backupFile);
    args.println("备份文件创建完成: " + backupFile.getFileName());
    return Result.success();
  }
}
