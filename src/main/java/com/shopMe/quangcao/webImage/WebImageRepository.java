package com.shopMe.quangcao.webImage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WebImageRepository extends JpaRepository<WebImage, Integer> {

  @Query("SELECT w FROM WebImage w WHERE w.category = ?1 and w.active = true")
  public List<WebImage> findByCategory(String category);


}

