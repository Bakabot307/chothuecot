package com.shopMe.demo.WebImage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WebImageRepository extends JpaRepository<WebImage, Integer> {

  @Query("SELECT w FROM WebImage w WHERE w.category = ?1 and w.orderNumber IS NOT NULL ORDER BY w.orderNumber ASc")
  public List<WebImage> findByCategory(String category);


  @Query("SELECT w FROM WebImage w WHERE w.category = ?1")
  public WebImage findLogo(String category);

  @Query("SELECT w FROM WebImage w WHERE w.orderNumber IS NOT NULL AND w.category = ?1 ORDER BY w.orderNumber ASC ")
  List<WebImage> findByProductThatHasOrder(String category);
}

