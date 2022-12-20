package com.shopMe.quangcao.product;

import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.address.AddressPoint.AddressPointService;
import com.shopMe.quangcao.address.AddressService;
import com.shopMe.quangcao.amazon.AmazonS3Util;
import com.shopMe.quangcao.category.Category;
import com.shopMe.quangcao.category.CategoryService;
import com.shopMe.quangcao.exceptions.ProductExistedException;
import com.shopMe.quangcao.product.dto.AProductDto;
import com.shopMe.quangcao.product.dto.AddProductDto;
import com.shopMe.quangcao.product.dto.CategoryDto;
import com.shopMe.quangcao.product.dto.UpdateProductDto;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.common.FileUploadUtil;
import com.shopMe.quangcao.common.Helper;
import com.shopMe.quangcao.exceptions.AddressNotExistException;
import com.shopMe.quangcao.exceptions.CategoryNotFoundException;
import com.shopMe.quangcao.exceptions.ProductNotExistException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
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


  @Autowired
  private AddressPointService addressPointService;
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
  public ResponseEntity<ApiResponse> add(@Valid AddProductDto productDto,
      MultipartFile multipartFile)
      throws AddressNotExistException, CategoryNotFoundException, IOException, ProductExistedException {
    if (multipartFile == null) {
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh không được để trống"),
          HttpStatus.BAD_REQUEST);
    }
    Product product = new Product(productDto);
    Address address = addressService.getById(productDto.getAddressId());
    Category category = categoryService.getById(productDto.getCategoryId());

    product.setCategory(category);
    product.setAddress(address);

    double random = productDto.getNum1() + Math.random() * (productDto.getNum2()
        - productDto.getNum1());
    product.setNumber(random);
    Product savedProduct = null;
    try {
      savedProduct = productService.saveAndCheckExisting(product);
    } catch (ProductExistedException e) {
      return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      savedProduct.setImage(fileName);
      String uploadDir = "product-images/" + savedProduct.getId();
      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    } else {
      if (savedProduct.getImage().isEmpty()) {
        savedProduct.setImage(null);
      }
    }
    productService.save(savedProduct);
    return new ResponseEntity<>(new ApiResponse(true, "Đã thêm thành công"), HttpStatus.CREATED);
  }

  @PutMapping("/product/update")
  ResponseEntity<ApiResponse> update(UpdateProductDto productDto, MultipartFile multipartFile)
      throws AddressNotExistException, ProductNotExistException, CategoryNotFoundException, IOException {
    Product productDB = productService.findById(productDto.getId());
    if (productDB.getStatus() == ProductStatus.HIRING) {
      return new ResponseEntity<>(
          new ApiResponse(false, "Trụ đang được thuê không thể cập nhật"),
          HttpStatus.BAD_REQUEST);
    }
    double random = productDto.getNum1() + Math.random() * (productDto.getNum2()
        - productDto.getNum1());

    productDB = productDB.copyUpdate(productDto);
    productDB.setNumber(random);
    Category category = categoryService.getById(productDto.getCategoryId());
    productDB.setCategory(category);

    Address address = addressService.getById(productDto.getAddressId());
    productDB.setAddress(address);

    if (multipartFile != null && !multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      productDB.setImage(fileName);
      String uploadDir = "product-images/" + productDB.getId();
      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    if (!Helper.pEnumContains(productDto.getStatus().toString())) {
      return new ResponseEntity<>(
          new ApiResponse(false, "status not exist" + Arrays.toString(ProductStatus.values())),
          HttpStatus.BAD_REQUEST);
    }

    productService.save(productDB);
    return new ResponseEntity<>(new ApiResponse(true, "cập nhật thành công"), HttpStatus.OK);
  }


  @DeleteMapping("/product/delete/{productId}")
  ResponseEntity<ApiResponse> delete(@PathVariable Integer productId)
      throws ProductNotExistException {
    Product product = productService.findById(productId);
    if (product.getStatus() == ProductStatus.HIRING) {
      return new ResponseEntity<>(
          new ApiResponse(false, "Trụ đang được thuê không thể xóa"),
          HttpStatus.BAD_REQUEST);
    }
    String addressDir = "product-images/" + productId;
    FileUploadUtil.removeDir(addressDir);
    productService.delete(product);
    return new ResponseEntity<>(new ApiResponse(true, "xóa thành công"), HttpStatus.OK);
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