package com.shopMe.quangcao.category;


import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.exceptions.CategoryNotFoundException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CategoryController {

  @Autowired
  CategoryService categoryService;

  @GetMapping("/admin/category")
  public ResponseEntity<Page<Category>> getFirstPage() {
    Page<Category> list = categoryService.listByPage(1, 5, "name", "asc", null);
    return ResponseEntity.ok().body(list);
  }

  @GetMapping("/admin/category/page/{pageNum}")
  public ResponseEntity<Page<Category>> getCats(
      @PathVariable(name = "pageNum") int pageNum,
      @RequestParam("sortField") String sortField,
      @RequestParam("sort") String sort,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam("dataPerPage") int dataPerPage
  ) {
    Page<Category> list = categoryService.listByPage(pageNum, dataPerPage, sortField, sort,
        keyword);
    return ResponseEntity.ok().body(list);
  }


  @PostMapping("/admin/category/add")
  public ResponseEntity<ApiResponse> add(@RequestBody @Valid Category category) {

    categoryService.save(category);
    return new ResponseEntity<>(new ApiResponse(true, "added successfully"), HttpStatus.CREATED);
  }

  @PutMapping("/admin/category/edit")
  public ResponseEntity<ApiResponse> edit(@RequestBody @Valid Category category)
      throws CategoryNotFoundException {

    Category editCate = categoryService.getById(category.getId());

    editCate.setName(category.getName());
    editCate.setDescription(category.getDescription());

    categoryService.save(editCate);
    return new ResponseEntity<>(new ApiResponse(true, "edited successfully"), HttpStatus.CREATED);
  }

  @DeleteMapping("/admin/category/delete")
  public ResponseEntity<ApiResponse> delete(@RequestParam("id") Integer id) {
    categoryService.delete(id);
    return new ResponseEntity<>(new ApiResponse(true, "deleted successfully"), HttpStatus.CREATED);
  }

  @GetMapping("admin/list_category")
  public List<Category> getCatList() {
    return categoryService.getAll();
  }

  @GetMapping("/category_of_address")
  public List<Category> getStreetCategory
      (@RequestParam("addressId") Integer addressId) {
    return categoryService.getStreetCategory(addressId);
  }
}
