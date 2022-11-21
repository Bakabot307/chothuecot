package com.shopMe.demo.Address;

import com.google.common.collect.Lists;
import com.shopMe.demo.Address.dto.AddressDetaildto;
import com.shopMe.demo.Address.dto.AddressDto;
import com.shopMe.demo.CartItem.CartItem;
import com.shopMe.demo.CartItem.CartItemService;
import com.shopMe.demo.OrderDetail.OrderDetailRepository;
import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductRepository;
import com.shopMe.demo.Product.ProductStatus;
import com.shopMe.demo.ScheduleTask.UpdateStatus;
import com.shopMe.demo.exceptions.AddressNotExistException;
import com.shopMe.demo.user.User;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AddressService {


  private final AddressRepository addressRepository;


  private final ProductRepository productRepository;

  private final UpdateStatus updateStatus;

  private final OrderDetailRepository orderDetailRepository;

  @Autowired
  private CartItemService cartItemService;

  @Autowired
  public AddressService(AddressRepository addressRepository, ProductRepository productRepository,
      OrderDetailRepository orderDetailRepository,
      UpdateStatus updateStatus) {
    this.addressRepository = addressRepository;
    this.productRepository = productRepository;
    this.updateStatus = updateStatus;
    this.orderDetailRepository = orderDetailRepository;
  }


  public Page<Address> listByPage(int pageNum, int dataPerPage, String sortField, String sort,
      String keyword) {
    Sort sort2 = Sort.by(sortField);
    sort2 = sort.equals("asc") ? sort2.ascending() : sort2.descending();
    Pageable pageable = PageRequest.of(pageNum - 1, dataPerPage, sort2);
    if (keyword != null) {
      return addressRepository.findAllAddress(keyword, pageable);
    }
    return addressRepository.findAll(pageable);
  }


  public PageAddressDto getAllByPage(int pageNum, Integer dataPerPage, String sortField,
      String sort, String keyword) throws NoSuchFieldException {
    List<Address> list;
    if (keyword == null) {
      list = (List<Address>) addressRepository.findAll();
    } else {
      list = addressRepository.search(keyword);
    }

    List<AddressDto> dtos = new ArrayList<>();
    for (Address a : list) {
      List<Product> productList = productRepository.getByAddress(a.getId());
      AddressDto addressDto = new AddressDto(a);
      int totalProduct = productList.size();

      long productAvailable = productList
          .stream()
          .filter(product -> product.getStatus() == ProductStatus.AVAILABLE)
          .count();

      Optional<Float> maxPrice = productList
          .stream()
          .map(Product::getPrice)
          .reduce((x, y) -> x > y ? x : y);

      Optional<Float> minPrice = productList
          .stream()
          .map(Product::getPrice)
          .reduce((x, y) -> x < y ? x : y);
      maxPrice.ifPresent(addressDto::setMaxPrice);
      minPrice.ifPresent(addressDto::setMinPrice);
      addressDto.setTotalProductAvailable((int) productAvailable);
      addressDto.setTotalProduct(totalProduct);
      dtos.add(addressDto);
    }
    if (sortField != null || sort != null) {
      dtos.sort(Comparator.comparing(reflectiveGetter(sortField)));
      dtos.forEach(e -> System.out.println(e.getTotalProductAvailable()));
      if (Objects.equals(sort, "desc")) {
        dtos = Lists.reverse(dtos);
        System.out.println("reverse");
        dtos.forEach(e -> System.out.println(e.getTotalProductAvailable()));
      }
    }
    dtos = dtos.stream()
        .filter(address -> address.getId() != null)
        .limit(dataPerPage)
        .skip((long) dataPerPage * (pageNum - 1)).collect(Collectors.toList());
    System.out.println(dataPerPage * (pageNum - 1));
    PageAddressDto pageAddressDto = new PageAddressDto();
    Map<String, Integer> pageInfo = new HashMap<>();

    int totalData = list.size();
//    (total + dataPerPage - 1) / dataPerPage
    int lastPage = (totalData + dataPerPage - 1) / dataPerPage;
    int from = ((pageNum - 1) * dataPerPage) + 1;
    int to = Math.min(pageNum * dataPerPage, totalData);
    int perPage = dataPerPage;

    pageInfo.put("totalData", totalData);
    pageInfo.put("dataPerPage", perPage);
    pageInfo.put("currentPage", pageNum);
    pageInfo.put("lastPage", lastPage);
    pageInfo.put("from", from);
    pageInfo.put("to", to);

    pageAddressDto.setPageInfo(pageInfo);
    pageAddressDto.setContents(dtos);

    return pageAddressDto;
  }

  private Function<AddressDto, Comparable> reflectiveGetter(String fieldName)
      throws NoSuchFieldException {
    Field field = AddressDto.class.getDeclaredField(fieldName);
    field.setAccessible(true);

    return (addressDto) ->
    {
      try {
        return (Comparable) field.get(addressDto);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public AddressDetaildto findByAddressId(Integer addressId, Integer categoryId, Double num1,
      Double num2)
      throws AddressNotExistException {
    List<Product> list;
    if (num1 == null && num2 == null || num1 == 0
        && num2 == 0) {
      list = productRepository.getByAddress(addressId);
    } else {
      list = productRepository.findByAddressIdAndPoint(addressId, num1, num2);
    }
    System.out.println(list);
    if (list == null) {
      throw new AddressNotExistException("Address not exist");
    }
    if (categoryId != null) {
      list = list.stream().filter(product -> Objects.equals(product.getCategory().getId(),
              categoryId))
          .sorted(Comparator.comparing(Product::getExpiredDate,
              Comparator.nullsFirst(Comparator.naturalOrder())))
          .collect(Collectors.toList());
    } else {
      list = list.stream()
          .sorted(Comparator.comparing(Product::getExpiredDate,
              Comparator.nullsFirst(Comparator.naturalOrder())))
          .collect(Collectors.toList());
    }

    AddressDetaildto addressDetaildto = new AddressDetaildto();

    addressDetaildto.setAddress(getById(addressId));
    addressDetaildto.setProduct(list);
    return addressDetaildto;
  }

  public AddressDetaildto findByAddressId2(Integer addressId, User user, Integer categoryId,
      Double num1, Double num2)
      throws AddressNotExistException {
    List<Product> list;
    System.out.println(num1);
    System.out.println(num2);
    if (num1 == null && num2 == null || num1 == 0
        && num2 == 0) {
      list = productRepository.getByAddress(addressId);
    } else {
      list = productRepository.findByAddressIdAndPoint(addressId, num1, num2);
    }

    if (categoryId != null) {
      list = list.stream().filter(product -> Objects.equals(product.getCategory().getId(),
              categoryId))
          .sorted(Comparator.comparing(Product::getExpiredDate,
              Comparator.nullsFirst(Comparator.naturalOrder())))
          .collect(Collectors.toList());
    } else {
      list = list.stream()
          .sorted(Comparator.comparing(Product::getExpiredDate,
              Comparator.nullsFirst(Comparator.naturalOrder())))
          .collect(Collectors.toList());
    }
    AddressDetaildto addressDetaildto = new AddressDetaildto();
    if (list == null) {
      throw new AddressNotExistException("Address not exist");
    }

    Set<Product> wishlist = user.getWishlist();
    List<CartItem> cart = cartItemService.getCartByUser(user);

    addressDetaildto.setAddress(list.get(0).getAddress());
    list.forEach(product -> {
      if (wishlist.contains(product)) {
        product.setInWishList(true);
      }
      if (cart.stream().anyMatch(cartItem -> Objects.equals(cartItem.getProduct().getId(),
          product.getId()))) {
        product.setInCart(true);
      }
      product.setAddress(null);
    });
    addressDetaildto.setProduct(list);

    return addressDetaildto;
  }


  public Address save(Address address) {
    return addressRepository.save(address);
  }

  public Address getById(Integer id) throws AddressNotExistException {
    return addressRepository.findById(id)
        .orElseThrow(() -> new AddressNotExistException("address not exist"));
  }

  public void delete(Integer id) {
    addressRepository.deleteById(id);
  }


  public List<Address> getAll() {
    return (List<Address>) addressRepository.findAll();
  }
}
