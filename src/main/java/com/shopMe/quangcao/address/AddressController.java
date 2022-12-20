package com.shopMe.quangcao.address;

import com.shopMe.quangcao.address.dto.AddAddressDto;
import com.shopMe.quangcao.address.dto.AddressDetaildto;
import com.shopMe.quangcao.amazon.AmazonS3Util;
import com.shopMe.quangcao.product.ProductService;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.common.FileUploadUtil;
import com.shopMe.quangcao.exceptions.AddressNotExistException;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import com.shopMe.quangcao.user.UserService;
import java.io.IOException;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AddressController {

  @Autowired
  AddressService addressService;

  @Autowired
  ProductService productService;

  @Autowired
  UserService userService;

  @GetMapping("/admin/address")
  public ResponseEntity<Page<Address>> getFirstPage() {
    Page<Address> list = addressService.listByPage(1, 5, "street", "asc", null);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("/admin/address/page/{pageNum}")
  public ResponseEntity<Page<Address>> getAddresses(
      @PathVariable(name = "pageNum") int pageNum,
      @RequestParam("sortField") String sortField,
      @RequestParam("sort") String sort,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam("dataPerPage") int dataPerPage
  ) {
    Page<Address> list = addressService.listByPage(pageNum, dataPerPage, sortField, sort, keyword);
    return ResponseEntity.ok().body(list);
  }


  @GetMapping("/address/page/{pageNum}")
  public ResponseEntity<PageAddressDto> getByPageForHome(
      @PathVariable(name = "pageNum") int pageNum,
      @RequestParam("dataPerPage") Integer dataPerPage,
      @RequestParam(name = "sortField", required = false) String sortField,
      @RequestParam(name = "sort", required = false) String sort,
      @RequestParam(value = "keyword", required = false) String keyword
  ) throws NoSuchFieldException {
    PageAddressDto address = addressService.getAllByPage(pageNum, dataPerPage, sortField, sort,
        keyword);
    return ResponseEntity.ok().body(address);
  }

  @PostMapping("/admin/address/add")
  public ResponseEntity<ApiResponse> add(@Valid AddAddressDto addressDto,
      MultipartFile multipartFile) throws IOException {
    if (multipartFile == null) {
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh không được để trống"),
          HttpStatus.BAD_REQUEST);
    }
    Address newAddress = new Address(addressDto);
    Address savedAddress = addressService.save(newAddress);

    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      savedAddress.setImage(fileName);
      String uploadDir = "address-images/" + savedAddress.getId();

      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    } else {
      if (savedAddress.getImage() == null) {
        savedAddress.setImage(null);
      }
    }
    addressService.save(savedAddress);
    return new ResponseEntity<>(new ApiResponse(true, "Đã thêm thành công"), HttpStatus.CREATED);
  }

  @PutMapping("/admin/address/edit")
  public ResponseEntity<ApiResponse> edit(Address address,
      MultipartFile multipartFile)
      throws AddressNotExistException, IOException {
    Address editAddress = addressService.getById(address.getId());
    editAddress.setCity(address.getCity());
    editAddress.setStreet(address.getStreet());
    editAddress.setDescription(address.getDescription());
    if (multipartFile != null && !multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      editAddress.setImage(fileName);
      String uploadDir = "address-images/" + editAddress.getId();
      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }
    addressService.save(editAddress);
    return new ResponseEntity<>(new ApiResponse(true, "Cập nhật thành công"), HttpStatus.CREATED);
  }

  @DeleteMapping("/admin/address/delete")
  public ResponseEntity<ApiResponse> delete(@RequestParam("id") Integer id)
      throws AddressNotExistException {
    String addressDir = "address-images/" + id;
    FileUploadUtil.removeDir(addressDir);
    addressService.delete(id);
    return new ResponseEntity<>(new ApiResponse(true, "Đã xóa thành công"), HttpStatus.CREATED);
  }


  @GetMapping("/address/{addressId}")
  public ResponseEntity<AddressDetaildto> findByAddressId(
      @PathVariable("addressId") Integer addressId,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Double num1,
      @RequestParam(required = false) Double num2) throws AddressNotExistException {
    AddressDetaildto list = addressService.findByAddressId(addressId, categoryId, num1, num2);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("admin/list_address")
  public ResponseEntity<List<Address>> listAddress() {
    List<Address> list = addressService.getAll();
    return ResponseEntity.ok().body(list);
  }

  @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
  @GetMapping("/address2/{addressId}")
  public ResponseEntity<AddressDetaildto> findByAddressId2(
      @PathVariable("addressId") Integer addressId,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Double num1,
      @RequestParam(required = false) Double num2
  )
      throws AddressNotExistException, UserNotFoundException {
    User userDB = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userService.getById(userDB.getId());
    AddressDetaildto list = addressService.findByAddressId2(addressId, user, categoryId, num1,
        num2);
    return ResponseEntity.ok().body(list);
  }
}
