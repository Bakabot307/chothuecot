package com.shopMe.quangcao.wishList;

import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.exceptions.ProductNotExistException;
import com.shopMe.quangcao.exceptions.ShoppingCartException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wishlist")
public class WishListController {

  @Autowired
  private WishListService wishListService;

  @PostMapping("/add")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> add(@RequestParam Integer productId)
      throws ShoppingCartException, ProductNotExistException, UserNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    wishListService.add(productId, user);
    return ResponseEntity.ok(new ApiResponse(true, "Đã thêm vào danh sách yêu thích"));
  }

  @PostMapping("/remove")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> remove(@RequestParam Integer productId)
      throws ProductNotExistException, UserNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    wishListService.remove(productId, user);
    return ResponseEntity.ok(new ApiResponse(true, "Đã xóa khỏi danh sách yêu thích"));
  }

  @GetMapping("/get")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<Set<Product>> getAll(@RequestParam Integer productId)
      throws UserNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Set<Product> set = wishListService.getWishList(user);
    return ResponseEntity.ok(set);
  }
}
