package com.shopMe.quangcao.settings;

import com.shopMe.quangcao.settings.model.Setting;
import com.shopMe.quangcao.settings.model.SettingCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingRepository extends JpaRepository<Setting, String> {

  public List<Setting> findByCategory(SettingCategory category);

  @Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
  public List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);

  public Setting findByKey(String key);
}
