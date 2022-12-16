package com.shopMe.quangcao.cartItem;

import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductRepository;
import com.shopMe.quangcao.product.ProductStatus;
import com.shopMe.quangcao.exceptions.CartItemNotExistException;
import com.shopMe.quangcao.exceptions.ProductNotExistException;
import com.shopMe.quangcao.exceptions.ShoppingCartException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
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
      throw new ShoppingCartException("Trụ đã có trong danh sách thanh toán");
    } else {
      cartItemRepository.save(cart);
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

  public void removeProduct(Integer productId, User user)  {
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
    List<CartItem> list = cartItemRepository.findByUser(user);
    return list;
  }

  public TreeMap<String,List<CartItem>> getCartCombo(User user) {
    List<CartItem> list = cartItemRepository.findByUser(user);
    Comparator<String> c = Comparator.comparingInt(String::length);
    TreeMap<String,List<CartItem>> map = list.stream()
        .collect(Collectors.groupingBy(
            cartItem -> cartItem.getProduct().getPoints().toString(),
            () -> new TreeMap<>(c),
            Collectors.toList()
            ));
    return map;
  }

  public List<CartItem> getCartByUserHasProductAvailable(User user) {
    return cartItemRepository.findByUser(user).stream()
        .filter(cartItem -> cartItem.getProduct().getStatus() == ProductStatus.AVAILABLE)
        .collect(Collectors.toList());
  }

  public void deleteCartItem(CartItem c) {
    cartItemRepository.delete(c);
  }

  public void addCombo(Integer addressId, Double num1, Double num2, User user) {
    List<Product> products = productRepository.findByAddressIdAndPoint(addressId, num1, num2);
    List<CartItem> cart = cartItemRepository.findByUser(user);
    if (cart.size() == 0) {
      for (Product product : products) {
        CartItem cartItem = new CartItem();
        if(product.getStatus() == ProductStatus.AVAILABLE){
          cartItem.setProduct(product);
          cartItem.setUser(user);
          cartItem.setMonth(1);
          cartItemRepository.save(cartItem);
        }

      }
    } else {
      products.removeAll(cart.stream().map(CartItem::getProduct).toList());
      products.forEach(product -> {
        if(product.getStatus() == ProductStatus.AVAILABLE){
          CartItem cartItem = new CartItem();
          cartItem.setProduct(product);
          cartItem.setUser(user);
          cartItem.setMonth(1);
          cartItemRepository.save(cartItem);
        }
      });
    }
  }
}
