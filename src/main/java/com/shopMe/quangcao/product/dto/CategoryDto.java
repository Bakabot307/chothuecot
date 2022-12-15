package com.shopMe.quangcao.product.dto;

import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.category.Category;
import com.shopMe.quangcao.product.Product;
import java.util.List;
import java.util.Map;

public class CategoryDto {

  private Category category;
  private Map<Address, List<Product>> categoryMap;

  public CategoryDto() {
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Map<Address, List<Product>> getCategoryMap() {
    return categoryMap;
  }

  public void setCategoryMap(
      Map<Address, List<Product>> categoryMap) {
    this.categoryMap = categoryMap;
  }
}
