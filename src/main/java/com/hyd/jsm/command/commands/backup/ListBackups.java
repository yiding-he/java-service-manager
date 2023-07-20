package com.hyd.jsm.command.commands.backup;

import com.hyd.jsm.command.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.components.Text;
import com.hyd.jsm.command.commands.AbstractCommand;
import com.hyd.jsm.domain.Backup;
import com.hyd.jsm.components.Grid;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Named("查看备份列表")
public class ListBackups extends AbstractCommand {

  public static List<Backup> listBackups(boolean refresh) {

    if (!refresh && CurrentContext.currentBackups != null) {
      return CurrentContext.currentBackups;
    }

    var javaService = CurrentContext.currentJavaService;
    var backups = new ArrayList<Backup>();
    var backupDir = Path.of(javaService.getPath(), javaService.getBackupDir());

    var backupFiles = FileUtil.listFilesByNewest(backupDir);
    for (int i = 0; i < backupFiles.size(); i++) {
      Path backupFile = backupFiles.get(i);
      backups.add(new Backup(i + 1, backupFile));
    }

    CurrentContext.currentBackups = backups;
    return backups;
  }

  @Override
  public Result execute(CommandArgs args) throws Exception {
    List<Backup> backups = listBackups(true);

    var grid = new Grid();
    grid.setColumns(List.of(
      Text.of("编号").bold(),
      Text.of("名称").bold(),
      Text.of("文件修改时间").bold(),
      Text.of("大小").bold()
    ));
    for (Backup backup : backups) {
      grid.getRows().add(List.of(
        Text.of(backup.getIndex()),
        Text.of(backup.getFileName()),
        Text.of(DateFormatUtils.format(backup.getLastModifiedTime(), "yyyy-MM-dd HH:mm:ss")),
        Text.of(backup.getSize())
      ));
    }
    grid.printWith(args.getPrintWriter());

    if (backups.isEmpty()) {
      args.println("暂无备份数据。\n");
    }

    return Result.success();
  }
}
