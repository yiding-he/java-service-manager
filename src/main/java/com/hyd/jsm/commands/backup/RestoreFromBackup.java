package com.hyd.jsm.commands.backup;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.commands.AbstractCommand;
import com.hyd.jsm.commands.process.JavaServiceStart;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import com.hyd.jsm.util.StrUtil;
import org.springframework.stereotype.Component;

import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@Named("从备份中恢复")
public class RestoreFromBackup extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var words = args.getParsedLine().words();
    if (words.size() != 2) {
      return Result.fail("使用方法: " + getName() + " [编号]");
    }

    var backups = ListBackups.listBackups(false);
    if (backups.isEmpty()) {
      return Result.fail("没有可用的备份。");
    }

    int index = StrUtil.parseInt(words.get(1), -1);
    var backup = backups.stream().filter(b -> b.getIndex() == index).findFirst().orElse(null);
    if (backup == null) {
      return Result.fail("没有找到编号对应的备份。");
    }

    var javaService = CurrentContext.currentJavaService;
    var backupPath = Path.of(javaService.getPath(), javaService.getBackupDir(), backup.getFileName());
    if (!Files.exists(backupPath)) {
      return Result.fail("备份文件没有找到。");
    }

    var restorePath = JavaServiceStart.findJarFile(javaService);
    Files.copy(backupPath, restorePath, StandardCopyOption.REPLACE_EXISTING);
    console.writeLine("已成功从备份 [" + index + "] '" + backup.getFileName() + "' 中恢复。");
    return Result.success();
  }
}
