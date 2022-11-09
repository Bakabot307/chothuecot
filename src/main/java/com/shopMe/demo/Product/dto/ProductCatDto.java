package com.shopMe.demo.Product.dto;

import com.shopMe.demo.Address.Address;
import com.shopMe.demo.Product.Product;
import java.util.List;
import java.util.Map;

public class ProductCatDto {

  Map<Address, List<Product>> products;
}
