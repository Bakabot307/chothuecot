package com.shopMe.quangcao.product;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

  @Query("SELECT p FROM Product p WHERE p.address.id = ?1")
  List<Product> getByAddress(Integer addressId);

  @Query("SELECT p FROM Product p WHERE p.address.id = ?1 AND p.number > ?2 AND p.number < 3")
  List<Product> getByAddressAndByPoints(Integer addressId, Double number1, Double number2);


  @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " +
      "OR p.price = ?1 " +
      "OR p.address.street LIKE %?1%")
  List<Product> findAllP(String keyword);


  @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " +
      "OR p.description LIKE %?1%" +
      "OR CONCAT(p.address.street, ' ' , p.address.city) LIKE %?1%" +
      "OR CONCAT(p.price, ' ') LIKE %?1% ")
  Page<Product> findAllByKeyWord(String keyword, Pageable pageable);

  @Query("SELECT p from Product p where p.category.id = ?1")
  List<Product> findByCategory(Integer category);

  @Query("SELECT p from Product p where p.address.id = ?1 AND p.number > ?2 AND p.number < ?3")
  List<Product> findByAddressIdAndPoint(Integer addressId, Double num1, Double num2);

  @Query("SELECT p from Product p where p.name LIKE ?1")
  Product findByName(String name);
}
