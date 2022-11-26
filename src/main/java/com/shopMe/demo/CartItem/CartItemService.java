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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CartItemService {

  @Autowired
  private SimpMessagingTemplate template;

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
      template.convertAndSendToUser(cart.getUser().getId().toString(), "/private",
          "cart");
    }

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
      template.convertAndSendToUser(user.getId().toString(), "/private",
          "cart");
    } else {
      throw new CartItemNotExistException("Item not exist in cart");
    }

  }

  public void deleteByUser(User user) {
    template.convertAndSendToUser(user.getId().toString(), "/private",
        "cart");
    cartItemRepository.deleteByUser(user.getId());
  }

  public List<CartItem> getCartByUser(User user) {
    List<CartItem> list = cartItemRepository.findByUser(user);
    return list;
  }

  public List<CartItem> getCartByUserHasProductAvailable(User user) {
    return cartItemRepository.findByUser(user).stream()
        .filter(cartItem -> cartItem.getProduct().getStatus() == ProductStatus.AVAILABLE)
        .collect(Collectors.toList());
  }

  public void deleteCartItem(CartItem c) {
    cartItemRepository.delete(c);
  }

  public void addCombo(Integer addressId, Double num1, Double num2, Integer month, User user) {
    List<Product> products = productRepository.findByAddressIdAndPoint(addressId, num1, num2);
    products.forEach(product -> {
      CartItem cartItem = new CartItem();
      cartItem.setProduct(product);
      cartItem.setUser(user);
      cartItem.setMonth(month);
      cartItemRepository.save(cartItem);
    });
  }
}
