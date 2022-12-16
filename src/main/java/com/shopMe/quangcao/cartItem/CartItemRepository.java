package com.shopMe.quangcao.cartItem;

import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

  List<CartItem> findByUser(User user);

  CartItem findByUserAndProduct(User user, Product product);


  @Query("SELECT c from CartItem c WHERE c.user.id = ?1 AND c.product.id = ?2")
  public CartItem findByUserAndProductId(Integer userId, Integer productId);

  @Modifying
  @Query("UPDATE CartItem c SET c.month = ?1 WHERE c.user.id = ?2 AND c.product.id = ?3")
  void updateDay(Integer day, Integer userId, Integer productId);

  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.user.id = ?1 AND c.product.id = ?2")
  void deleteByUserAndProduct(Integer customerId, Integer productId);

  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.user.id = ?1")
  void deleteByUser(Integer userId);
}
