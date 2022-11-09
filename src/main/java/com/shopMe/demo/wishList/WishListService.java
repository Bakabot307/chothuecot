package com.shopMe.demo.wishList;

import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductService;
import com.shopMe.demo.exceptions.ProductNotExistException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserNotFoundException;
import com.shopMe.demo.user.UserRepository;
import com.shopMe.demo.user.UserService;
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
