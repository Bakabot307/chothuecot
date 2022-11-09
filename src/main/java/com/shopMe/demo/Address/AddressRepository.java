package com.shopMe.demo.Address;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AddressRepository extends PagingAndSortingRepository<Address, Integer> {

  @Query("SELECT a FROM Address a WHERE a.street LIKE %?1% " +
      "OR a.city LIKE %?1%" +
      "OR a.description LIKE %?1%")
  Page<Address> findAllAddress(String keyword, Pageable pageable);

  @Query("SELECT distinct a from Address a JOIN fetch a.products p  WHERE "
      + "a.street like %?1% "
      + "OR a.city like %?1% "
      + "OR p.name like %?1%"
  )
  List<Address> search(String keyword);
}
