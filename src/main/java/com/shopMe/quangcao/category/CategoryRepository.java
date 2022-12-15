package com.shopMe.quangcao.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

  @Query("SELECT c FROM Category c WHERE c.id = ?1 " +
      "OR c.name LIKE %?1%" +
      "OR c.description LIKE %?1%")
  Page<Category> findAll(String keyword, Pageable pageable);


}
