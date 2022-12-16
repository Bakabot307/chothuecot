package com.shopMe.quangcao.settings;

import com.shopMe.quangcao.settings.model.Setting;
import com.shopMe.quangcao.common.ApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/settings")
public class SettingController {

  @Autowired
  private SettingService settingService;

  @GetMapping("/settings")
  public ResponseEntity<List<Setting>> listAll() {
    List<Setting> listSettings = settingService.listAllEmailSettings();
    return ResponseEntity.ok().body(listSettings);
  }

  @PostMapping("/settings/update")
  public @ResponseBody ResponseEntity<ApiResponse> update(
      @RequestBody List<Setting> Settings) {
    List<Setting> mailServerSettings = settingService.listAllEmailSettings();
    mailServerSettings.forEach(setting -> {
      Settings.forEach(setting1 -> {
        if (setting.getKey().equals(setting1.getKey())) {
          setting.setValue(setting1.getValue());
        }
      });
    });
    settingService.saveAll(mailServerSettings);
    return new ResponseEntity<>(new ApiResponse(true, "Cập nhật thành công"),
        HttpStatus.OK);
  }

}
