package com.shopMe.demo.Product;

import com.shopMe.demo.Address.Address;
import com.shopMe.demo.Address.AddressService;
import com.shopMe.demo.Amazon.AmazonS3Util;
import com.shopMe.demo.Category.Category;
import com.shopMe.demo.Category.CategoryService;
import com.shopMe.demo.Product.dto.AProductDto;
import com.shopMe.demo.Product.dto.AddProductDto;
import com.shopMe.demo.Product.dto.CategoryDto;
import com.shopMe.demo.Product.dto.UpdateProductDto;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.config.Helper;
import com.shopMe.demo.exceptions.AddressNotExistException;
import com.shopMe.demo.exceptions.CategoryNotFoundException;
import com.shopMe.demo.exceptions.ProductNotExistException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
public class ProductController {

  @Autowired
  private final ProductService productService;

  private final AddressService addressService;

  CategoryService categoryService;

  @Autowired
  public ProductController(ProductService productService, AddressService addressService,
      CategoryService categoryService) {
    this.productService = productService;
    this.addressService = addressService;
    this.categoryService = categoryService;
  }

  @GetMapping("/product/page/{pageNum}")
  public ResponseEntity<Page<AProductDto>> getProduct(
      @PathVariable("pageNum") int pageNum,
      @RequestParam("sortField") String sortField,
      @RequestParam("sort") String sort,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam Integer quantity) {
    Page<AProductDto> list = productService.getAllProduct(pageNum, sortField, sort, keyword,
        quantity);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @PostMapping("/product/add")
  public ResponseEntity<ApiResponse> add(AddProductDto productDto,
      MultipartFile multipartFile)
      throws AddressNotExistException, CategoryNotFoundException, IOException {
    Product product = new Product(productDto);
    Address address = addressService.getById(productDto.getAddressId());
    Category category = categoryService.getById(productDto.getCategoryId());

    product.setCategory(category);
    product.setAddress(address);
    Product savedProduct = productService.save(product);

    if (multipartFile == null) {
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh không được để trống"),
          HttpStatus.BAD_REQUEST);
    }
    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      savedProduct.setImage(fileName);

      String uploadDir = "product-images/" + savedProduct.getId();

      AmazonS3Util.removeFolder(uploadDir);
      AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
    } else {
      if (savedProduct.getImage().isEmpty()) {
        savedProduct.setImage(null);

      }
    }
    productService.save(savedProduct);
    return new ResponseEntity<>(new ApiResponse(true, "added successfully"), HttpStatus.CREATED);
  }

  @PutMapping("/product/update")
  ResponseEntity<ApiResponse> update(UpdateProductDto productDto, MultipartFile multipartFile)
      throws AddressNotExistException, ProductNotExistException, CategoryNotFoundException, IOException {
    Product productDB = productService.findById(productDto.getId());
    if (productDB.getStatus() == ProductStatus.HIRING) {
      return new ResponseEntity<>(
          new ApiResponse(false, "Product is being hired, can't change status"),
          HttpStatus.BAD_REQUEST);
    }

    productDB = productDB.copyUpdate(productDto);
    Category category = categoryService.getById(productDB.getCategory().getId());
    productDB.setCategory(category);

    Address address = addressService.getById(productDto.getAddressId());
    productDB.setAddress(address);

    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      productDB.setImage(fileName);

      String uploadDir = "product-images/" + productDB.getId();

      AmazonS3Util.removeFolder(uploadDir);
      AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
    }

    if (!Helper.pEnumContains(productDto.getStatus().toString())) {
      return new ResponseEntity<>(
          new ApiResponse(false, "status not exist" + Arrays.toString(ProductStatus.values())),
          HttpStatus.BAD_REQUEST);
    }

    productService.save(productDB);
    return new ResponseEntity<>(new ApiResponse(true, "updated successfully"), HttpStatus.OK);
  }


  @DeleteMapping("/product/delete/{productId}")
  ResponseEntity<ApiResponse> delete(@PathVariable Integer productId)
      throws ProductNotExistException {
    Product product = productService.findById(productId);
    String addressDir = "product-images/" + productId;
    AmazonS3Util.removeFolder(addressDir);
    productService.delete(product);
    return new ResponseEntity<>(new ApiResponse(true, "deleted successfully"), HttpStatus.OK);
  }

  @GetMapping("/admin/product/page/{pageNum}")
  public ResponseEntity<Page<Product>> getProductByStatus(
      @PathVariable("pageNum") int pageNum,
      @RequestParam("sortField") String sortField,
      @RequestParam("sort") String sort,
      @RequestParam("dataPerPage") int dataPerPage,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam ProductStatus status) {
    Page<Product> list = productService.getProductByStatus(status, sort, sortField, keyword,
        dataPerPage, pageNum);
    return ResponseEntity.ok().body(list);
  }


  @GetMapping("/category/{id}")
  public ResponseEntity<CategoryDto> getByCategoryId(
      @PathVariable("id") Integer id,
      @RequestParam(value = "keyword", required = false) String keyword
  ) throws CategoryNotFoundException {
    CategoryDto list = productService.getAllProductByCategory(
        keyword, id);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @RolesAllowed("ROLE_USER")
  @GetMapping("/category2/{id}")
  public ResponseEntity<CategoryDto> getByCategoryId2(
      @PathVariable("id") Integer id,
      @RequestParam(value = "keyword", required = false) String keyword
  ) throws UserNotFoundException, CategoryNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    CategoryDto list = productService.getAllProductByCategoryLoggedIn(
        keyword, id, user);
    return new ResponseEntity<>(list, HttpStatus.OK);
  }
}