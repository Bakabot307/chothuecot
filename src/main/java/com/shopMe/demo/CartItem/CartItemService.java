package com.shopMe.demo.CartItem;

import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductRepository;
import com.shopMe.demo.Product.ProductStatus;
import com.shopMe.demo.exceptions.CartItemNotExistException;
import com.shopMe.demo.exceptions.ProductNotExistException;
import com.shopMe.demo.exceptions.ShoppingCartException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CartItemService {


  private final CartItemRepository cartItemRepository;

  private final ProductRepository productRepository;

  @Autowired
  public CartItemService(CartItemRepository cartItemRepository, UserService userService,
      ProductRepository productRepository) {
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  public void addCart(CartItem cart) throws ShoppingCartException {
    CartItem cartDB = cartItemRepository.findByUserAndProduct(cart.getUser(), cart.getProduct());
    if (cartDB != null) {
      throw new ShoppingCartException("product is already in cart");
    } else {
      cartItemRepository.save(cart);
    }

  }

  public void addPreOrder(CartItem cart) throws ShoppingCartException {

  }

  public void save(CartItem cart) {
    cartItemRepository.save(cart);

  }

  public void updateDay(Integer productId, Integer day, User user)
      throws ProductNotExistException {
    cartItemRepository.updateDay(day, user.getId(), productId);
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotExistException("product not exist"));
    productRepository.save(product);
  }

  public void removeProduct(Integer productId, User user) throws ProductNotExistException {
    CartItem cartItem = cartItemRepository.findByUserAndProductId(user.getId(), productId);
    if (cartItem != null) {
      cartItemRepository.deleteByUserAndProduct(user.getId(), productId);
    } else {
      throw new CartItemNotExistException("Item not exist in cart");
    }

  }

  public void deleteByUser(User user) {
    cartItemRepository.deleteByUser(user.getId());
  }

  public List<CartItem> getCartByUser(User user) {
    return cartItemRepository.findByUser(user);
  }

  public List<CartItem> getCartByUserHasProductAvailable(User user) {
    return cartItemRepository.findByUser(user).stream()
        .filter(cartItem -> cartItem.getProduct().getStatus() == ProductStatus.AVAILABLE)
        .collect(Collectors.toList());
  }


  public void deleteCartItem(CartItem c) {
    cartItemRepository.delete(c);
  }
}
