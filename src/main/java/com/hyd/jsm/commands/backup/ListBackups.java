package com.hyd.jsm.commands.backup;

import com.hyd.jsm.CommandArgs;
import com.hyd.jsm.CurrentContext;
import com.hyd.jsm.cli.Text;
import com.hyd.jsm.commands.AbstractCommand;
import com.hyd.jsm.model.Backup;
import com.hyd.jsm.structure.Grid;
import com.hyd.jsm.util.FileUtil;
import com.hyd.jsm.util.Named;
import com.hyd.jsm.util.Result;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Named("查看备份列表")
public class ListBackups extends AbstractCommand {

  @Override
  public Result execute(CommandArgs args) throws Exception {
    var javaService = CurrentContext.currentJavaService;
    var backups = new ArrayList<Backup>();
    var backupDir = Path.of(javaService.getPath(), javaService.getBackupDir());

    var backupFiles = FileUtil.listFilesByNewest(backupDir);
    for (int i = 0; i < backupFiles.size(); i++) {
      Path backupFile = backupFiles.get(i);
      backups.add(new Backup(i + 1, backupFile));
    }

    var grid = new Grid();
    grid.setColumns(List.of(
      Text.of("编号").bold(),
      Text.of("名称").bold(),
      Text.of("创建时间").bold(),
      Text.of("大小").bold()
    ));
    for (Backup backup : backups) {
      grid.getRows().add(List.of(
        Text.of(backup.getIndex()),
        Text.of(backup.getAbsolutePath()),
        Text.of(backup.getLastModifiedTime()),
        Text.of(backup.getSize())
      ));
    }
    grid.printToConsole(console);

    if (backups.isEmpty()) {
      console.writeLine("暂无备份数据。\n");
    }

    return Result.success();
  }
}
