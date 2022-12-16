package com.shopMe.quangcao.settings;

import com.shopMe.quangcao.settings.model.Setting;
import com.shopMe.quangcao.settings.model.SettingCategory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

  @Autowired
  private SettingRepository repo;


  public List<Setting> getGeneralSettings() {
    return repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
  }

  public EmailSettingBag getEmailSettings() {
    List<Setting> settings = repo.findByCategory(SettingCategory.MAIL_SERVER);
    settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));

    return new EmailSettingBag(settings);
  }

  public List<Setting> listAllEmailSettings() {
    return repo.findByCategory(SettingCategory.MAIL_SERVER);
  }

  public void saveAll(Iterable<Setting> settings) {
    repo.saveAll(settings);
  }


}