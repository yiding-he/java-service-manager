package com.hyd.jsm.scenes;

import com.hyd.jsm.Command;
import com.hyd.jsm.commands.backup.CreateBackup;
import com.hyd.jsm.commands.backup.ListBackups;
import com.hyd.jsm.commands.backup.RestoreFromBackup;
import com.hyd.jsm.util.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 备份管理
 */
@Component
@Named("备份管理")
public class BackupScene extends AbstractScene {

  @Autowired
  private ListBackups listBackups;

  @Autowired
  private CreateBackup createBackup;

  @Autowired
  private RestoreFromBackup restoreFromBackup;

  @Override
  public String greetings() {
    return "你已进入备份管理，在这里可以创建、查看、删除备份，以及从备份中恢复";
  }

  @Override
  public List<Command> getAvailableCommands() {
    return List.of(listBackups, createBackup, restoreFromBackup);
  }
}
