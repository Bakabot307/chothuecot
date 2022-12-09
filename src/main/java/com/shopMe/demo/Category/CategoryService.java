package com.shopMe.demo.Category;

import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductRepository;
import com.shopMe.demo.exceptions.CategoryNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CategoryService {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductRepository productRepository;

  public Page<Category> listByPage(int pageNum, int dataPerPage, String sortField, String sort,
      String keyword) {
    Sort sort2 = Sort.by(sortField);
    sort2 = sort.equals("asc") ? sort2.ascending() : sort2.descending();
    Pageable pageable = PageRequest.of(pageNum - 1, dataPerPage, sort2);
    if (keyword != null) {
      return categoryRepository.findAll(keyword, pageable);
    }
    return categoryRepository.findAll(pageable);
  }

  public void save(Category category) {
    categoryRepository.save(category);
  }

  public Category getById(Integer id) throws CategoryNotFoundException {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryNotFoundException("Vui lòng nhập đúng loại trụ"));
  }

  public void delete(Integer id) {
    categoryRepository.deleteById(id);
  }

  public List<Category> getAll() {
    return (List<Category>) categoryRepository.findAll();
  }


  public List<Category> getStreetCategory(Integer id) {
    return productRepository.getByAddress(id).stream().map(Product::getCategory)
        .filter(Objects::nonNull).distinct().collect(Collectors.toList());
  }
}
