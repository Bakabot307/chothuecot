package com.shopMe.quangcao.user;

import com.shopMe.quangcao.wishList.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

  @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ' , u.email, ' ', u.firstName, ' ' ,"
      + " u.lastName, ' ' , u.phoneNumber) LIKE %?1%")
  public Page<User> findAll(String keyword, Pageable pageable);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);


  @Query("SELECT u FROM User u WHERE u.emailVerifyCode = ?1")
  public User findByEmailVerifyCode(String code);

  @Query("UPDATE User u SET  u.emailVerifyCode = null, u.emailVerified = true WHERE u.id = ?1")
  @Modifying
  public void verify(Integer id);

  @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
  @Modifying
  public void updateEnabledStatus(Integer id, boolean enabled);


  @Query("select w FROM Product p "
      + "INNER JOIN WishList w ON p.id = w.product.id "
      + "INNER JOIN User u ON w.user.id = u.id")
  List<WishList> findAllWishList();
@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = ?1")
  List<User> getAllByRole(String role);
}
