package com.shopMe.quangcao.product;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

import com.google.common.collect.Lists;
import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.address.AddressRepository;
import com.shopMe.quangcao.cartItem.CartItem;
import com.shopMe.quangcao.cartItem.CartItemService;
import com.shopMe.quangcao.category.Category;
import com.shopMe.quangcao.category.CategoryService;
import com.shopMe.quangcao.exceptions.ProductExistedException;
import com.shopMe.quangcao.orderDetail.OrderDetailRepository;
import com.shopMe.quangcao.product.dto.AProductDto;
import com.shopMe.quangcao.product.dto.CategoryDto;
import com.shopMe.quangcao.exceptions.CategoryNotFoundException;
import com.shopMe.quangcao.exceptions.ProductNotExistException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import com.shopMe.quangcao.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  @Autowired
  private
  ProductRepository productRepository;

  @Autowired
  private
  OrderDetailRepository orderDetailRepository;

  @Autowired
  private
  AddressRepository addressRepository;

  @Autowired
  private
  UserService userService;

  @Autowired
  private CartItemService cartItemService;

  @Autowired
  private CategoryService categoryService;


  public ProductService() {
  }

  public Product saveAndCheckExisting(Product product) throws ProductExistedException {
    if(existsByName(product.getName())) {
      throw new ProductExistedException("Trụ với tên " + product.getName() + " đã tồn tại");
    }
    return productRepository.save(product);
  }
  public void save(Product product) {
     productRepository.save(product);
  }

  public boolean existsByName(String name) {
    return productRepository.findByName(name) != null;
  }
  public void delete(Product product) {
    productRepository.delete(product);
  }

  public Product findById(Integer productId) throws ProductNotExistException {
    return productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotExistException("Product " + productId + "not exist"));
  }

  public Page<AProductDto> getAllProduct(int pageNum, String sortField, String sort, String keyword,
      int dataPerPage) {
    String[] field = {"street", "city"};
    Sort sort2;
    if (!Arrays.asList(field).contains(sortField)) {
      sort2 = Sort.by(sortField);
    } else {
      sort2 = Sort.by("id");
    }
    sort2 = sort.equals("asc") ? sort2.ascending() : sort2.descending();
    Pageable pageable = PageRequest.of(pageNum - 1, dataPerPage, sort2);

    List<Product> finalList;
    long totalProduct = 0;
    if (keyword != null) {
      Page<Product> listWithKeyword = productRepository.findAllByKeyWord(keyword, pageable);
      totalProduct = listWithKeyword.getTotalElements();
      finalList = listWithKeyword.getContent();
      if (sortField.equals("street") || sortField.equals("city")) {
        finalList = listWithKeyword.getContent().stream()
            .filter(Objects::nonNull)
            .skip((long) dataPerPage * (pageNum - 1))
            .limit(dataPerPage)
            .sorted((Comparator.comparing(p ->
                p.getAddress().getStreetOrCity(sortField))))
            .collect(Collectors.toList());
        if (sort.equals("desc")) {
          finalList = Lists.reverse(listWithKeyword.getContent());
        }
      }
    } else {
      Page<Product> listWithNoKeyword = productRepository.findAll(pageable);
      totalProduct = listWithNoKeyword.getTotalElements();
      finalList = listWithNoKeyword.getContent();
      if (sortField.equals("street") || sortField.equals("city")) {
        finalList = listWithNoKeyword.getContent().stream()
            .filter(Objects::nonNull)
            .skip((long) dataPerPage * (pageNum - 1))
            .limit(dataPerPage)
            .sorted((Comparator.comparing(p ->
                p.getAddress().getStreetOrCity(sortField))))
            .collect(Collectors.toList());
        if (sort.equals("desc")) {
          finalList = Lists.reverse(listWithNoKeyword.getContent());
        }
      }
    }
    List<AProductDto> listForStreet = new ArrayList<>();
    for (Product p : finalList) {
      AProductDto dto = new AProductDto(p);
      listForStreet.add(dto);
    }
    return new PageImpl<>(listForStreet, pageable, totalProduct);
  }

  public Page<Product> getProductByStatus(ProductStatus status, String sort, String sortField,
      String keyword, int dataPerPage, int pageNum) {
    String[] field = {"street", "city"};
    Sort sort2;
    long totalProduct = 0;
    if (!Arrays.asList(field).contains(sortField)) {
      sort2 = Sort.by(sortField);
    } else {
      sort2 = Sort.by("id");
    }
    sort2 = sort.equals("asc") ? sort2.ascending() : sort2.descending();
    Pageable pageable = PageRequest.of(pageNum - 1, dataPerPage, sort2);

    List<Product> finalList;
    if (keyword != null) {
      Page<Product> listWithKeyword = productRepository.findAllByKeyWord(keyword, pageable);

      finalList = listWithKeyword.getContent().stream().filter(p -> p.getStatus().equals(status))
          .collect(Collectors.toList());
      if (sortField.equals("street") || sortField.equals("city")) {
        finalList = listWithKeyword.getContent().stream()
            .filter(Objects::nonNull)
            .filter(p -> p.getStatus().equals(status))
            .skip((long) dataPerPage * (pageNum - 1))
            .limit(dataPerPage)
            .sorted((Comparator.comparing(p ->
                p.getAddress().getStreetOrCity(sortField))))
            .collect(Collectors.toList());
        if (sort.equals("desc")) {
          finalList = Lists.reverse(finalList);
        }
      }

      totalProduct = finalList.size();
    } else {
      List<Product> listWithNoKeyword = (List<Product>) productRepository.findAll();
      if (sortField.equals("street") || sortField.equals("city")) {
        finalList = listWithNoKeyword.stream()
            .filter(Objects::nonNull)
            .filter(p -> p.getStatus().equals(status))
            .sorted((Comparator.comparing(p ->
                p.getAddress().getStreetOrCity(sortField))))
            .collect(Collectors.toList());
        if (sort.equals("desc")) {
          finalList = Lists.reverse(finalList);
        }
        totalProduct = finalList.size();
        return new PageImpl<>(finalList, pageable, totalProduct);
      }

      finalList = listWithNoKeyword
          .stream().filter(p -> p.getStatus().equals(status))
          .collect(Collectors.toList());
    }
    return new PageImpl<>(finalList, pageable, totalProduct);
  }

  public CategoryDto getAllProductByCategory(
      String keyword, Integer categoryId) throws CategoryNotFoundException {
    List<Product> listProduct = productRepository.findByCategory(categoryId);
    Map<Address, List<Product>> list;
    Category category = categoryService.getById(categoryId);
    CategoryDto categoryDto = new CategoryDto();

    if (keyword != null) {
      list = listProduct.stream()
          .filter(p -> p.getAddress().getFullAddress().contains(keyword))
          .sorted(Comparator.comparing(Product::getExpiredDate, Comparator.nullsFirst(Comparator
              .naturalOrder())))
          .collect(Collectors.groupingBy(Product::getAddress));
    } else {
      list = listProduct.stream()
          .sorted(Comparator.comparing(Product::getExpiredDate, Comparator.nullsFirst(Comparator
              .naturalOrder())))
          .collect(Collectors.groupingBy(Product::getAddress));
    }

    list = list.entrySet().stream()
        .peek(e -> e.getValue()
            .forEach(p -> {
              p.setAddress(null);
              p.setCategory(null);
            }))
        .sorted(Map.Entry.comparingByKey(Comparator.comparing(Address::getStreet))).collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                LinkedHashMap::new));
    categoryDto.setCategoryMap(list);
    categoryDto.setCategory(category);

    return categoryDto;
  }

  public CategoryDto getAllProductByCategoryLoggedIn(
      String keyword, Integer categoryId, User userLg)
      throws UserNotFoundException, CategoryNotFoundException {
    User user = userService.findById(userLg.getId());
    List<Product> listProduct = productRepository.findByCategory(categoryId);
    Map<Address, List<Product>> list;
    Category category = categoryService.getById(categoryId);
    CategoryDto categoryDto = new CategoryDto();
    if (keyword != null) {
      list = listProduct.stream()
          .filter(p -> p.getAddress().getFullAddress().contains(keyword))
          .sorted(Comparator.comparing(Product::getExpiredDate, Comparator.nullsFirst(Comparator
              .naturalOrder())))
          .collect(Collectors.groupingBy(Product::getAddress));
    } else {
      list = listProduct.stream()
          .sorted(Comparator.comparing(Product::getExpiredDate, Comparator.nullsFirst(Comparator
              .naturalOrder())))
          .collect(Collectors.groupingBy(Product::getAddress));
    }

    Set<Product> wishlist = user.getWishlist();
    List<CartItem> cart = cartItemService.getCartByUser(user);
    list = list.entrySet().stream()
        .peek(e -> e.getValue()
            .forEach(p -> {
              p.setAddress(null);
              p.setCategory(null);
              if (wishlist.contains(p)) {
                p.setInWishList(true);
              }
              if (cart.stream().anyMatch(c -> c.getProduct().getId().equals(p.getId()))) {
                p.setInCart(true);
              }
            }))
        .sorted(Map.Entry.comparingByKey(Comparator.comparing(Address::getStreet))).collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                LinkedHashMap::new));
    categoryDto.setCategoryMap(list);
    categoryDto.setCategory(category);
    return categoryDto;
  }
}
