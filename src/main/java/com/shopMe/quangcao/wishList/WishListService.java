package com.shopMe.quangcao.wishList;

import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductService;
import com.shopMe.quangcao.exceptions.ProductNotExistException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import com.shopMe.quangcao.user.UserRepository;
import com.shopMe.quangcao.user.UserService;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WishListService {


  @Autowired
  private ProductService productService;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  public void add(Integer productId, User user)
      throws ProductNotExistException, UserNotFoundException {
    Product product = productService.findById(productId);
    User userDB = userService.getById(user.getId());
    userDB.addProduct(product);
    userService.updateUser(userDB);
  }

  public void remove(Integer productId, User user)
      throws ProductNotExistException, UserNotFoundException {
    Product product = productService.findById(productId);
    User userDB = userService.getById(user.getId());

    userDB.removeProduct(product);
    userService.updateUser(userDB);
  }

  public Set<Product> getWishList(User user) throws UserNotFoundException {
    User userDB = userService.getById(user.getId());
    return userDB.getWishlist();
  }

  public List<WishList> getallWishList() {
    return userRepository.findAllWishList();
  }
}
