package com.shopMe.demo.CartItem;

import com.shopMe.demo.Address.AddressPoint.AddressPoint;
import com.shopMe.demo.CartItem.dto.CartItemDto;
import com.shopMe.demo.CartItem.dto.MapDto;
import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductService;
import com.shopMe.demo.Product.ProductStatus;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.exceptions.ProductNotExistException;
import com.shopMe.demo.exceptions.ShoppingCartException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartItemController {


  private final
  CartItemService cartItemService;

  private final ProductService productService;

  @Autowired
  public CartItemController(CartItemService cartItemService, UserService userService,
      ProductService productService) {
    this.cartItemService = cartItemService;
    this.productService = productService;
  }

  @PostMapping("/addtocart")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> add(@RequestBody CartItemDto cartItemDto)
      throws ShoppingCartException, ProductNotExistException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    CartItem cart = new CartItem();
    Product product = productService.findById(cartItemDto.getProductId());

    if (product.getStatus() == ProductStatus.HIRING) {
      return new ResponseEntity<>(
          new ApiResponse(false, "product has been ordered by someone else"),
          HttpStatus.BAD_REQUEST);
    }

    cart.setUser(user);
    cart.setProduct(product);
    cart.setMonth(cartItemDto.getMonth());
    cartItemService.addCart(cart);
    return new ResponseEntity<>(new ApiResponse(true, "added successfully"), HttpStatus.CREATED);
  }

  @PostMapping("/addtocart_combo")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> addtocart_combo(
      @RequestParam("addressId") Integer addressId,
      @RequestParam Double num1,
      @RequestParam Double num2) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    cartItemService.addCombo(addressId, num1, num2, user);
    return new ResponseEntity<>(new ApiResponse(true, "Đã thêm tất cả vào giỏ hàng"), HttpStatus.CREATED);
  }

  @PostMapping("/addalltocart")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<?> add(
      @RequestBody MapDto mapDto)
      throws ShoppingCartException, ProductNotExistException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Map<Integer, Integer> map = mapDto.getProductInfo();
    List<Product> cartList  = cartItemService.getCartByUser(user).stream().map(CartItem::getProduct).toList();

    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      CartItem cart = new CartItem();
      Product product = productService.findById(entry.getKey());
      cartList.stream().filter(p -> !Objects.equals(p.getId(), product.getId())).findFirst().ifPresent(p -> {
        cart.setUser(user);
        cart.setProduct(product);
        cart.setMonth(entry.getValue());
        try {
          cartItemService.addCart(cart);
        } catch (ShoppingCartException e) {
          throw new RuntimeException(e);
        }
      });
    }
    return ResponseEntity.ok().body(mapDto);
  }

  @PutMapping("/update/{productId}")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> updateMonth(@PathVariable("productId") Integer productId,
      @RequestParam("day") Integer month
  ) throws ProductNotExistException {

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    cartItemService.updateDay(productId, month, user);
    return new ResponseEntity<>(new ApiResponse(true, "updated successfully"), HttpStatus.OK);

  }

  @PutMapping("/remove/{productId}")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> removeProduct(@PathVariable("productId") Integer productId
  ) throws ProductNotExistException {

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    cartItemService.removeProduct(productId, user);
    return new ResponseEntity<>(new ApiResponse(true, "removed successfully"), HttpStatus.OK);

  }

  @RolesAllowed("ROLE_USER")
  @GetMapping("/")
  public ResponseEntity<Map<Set<AddressPoint>,List<CartItem>>> getCart() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Map<Set<AddressPoint>,List<CartItem>> list = cartItemService.getCartCombo(user);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }
}
