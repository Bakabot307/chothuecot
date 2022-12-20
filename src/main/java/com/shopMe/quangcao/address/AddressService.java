package com.shopMe.quangcao.address;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.shopMe.quangcao.address.AddressPoint.AddressPoint;
import com.shopMe.quangcao.address.AddressPoint.AddressPointService;
import com.shopMe.quangcao.address.dto.AddressDetaildto;
import com.shopMe.quangcao.address.dto.AddressDto;
import com.shopMe.quangcao.cartItem.CartItem;
import com.shopMe.quangcao.cartItem.CartItemService;
import com.shopMe.quangcao.orderDetail.OrderDetailRepository;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductRepository;
import com.shopMe.quangcao.product.ProductStatus;
import com.shopMe.quangcao.scheduleTask.UpdateStatus;
import com.shopMe.quangcao.exceptions.AddressNotExistException;
import com.shopMe.quangcao.user.User;
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
  private AddressPointService addressPointService;

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
      list =  addressRepository.findAllBySize();
      list = list.stream().filter(address -> address.getAddressPoints().size() >1).collect(
          Collectors.toList());
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
      if (Objects.equals(sort, "desc")) {
        dtos = Lists.reverse(dtos);
      }
    }
    dtos = dtos.stream()
        .filter(address -> address.getId() != null)
        .skip((long) dataPerPage * (pageNum - 1))
        .limit(dataPerPage)
        .collect(Collectors.toList());
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
    pageInfo.put("lastPage", keyword == null ? lastPage : dtos.size());
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
    Address address = getById(addressId);
    Set<AddressPoint> addressPoints = address.getAddressPoints();
    Double start = 0.0;
    Double end = 0.0;

    if(addressPoints.size()>2 &&num1==null && num2==null){
      start=addressPoints.iterator().next().getNumber();
      end= Iterables.getLast(addressPoints).getNumber();}
    if(num1!=null && num2!=null){
      start=num1;
      end=num2;
    }
    list = productRepository.findByAddressIdAndPoint(addressId, start, end);

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
    addressDetaildto.setAddress(address);
    addressDetaildto.setProduct(list);
    return addressDetaildto;
  }

  public AddressDetaildto findByAddressId2(Integer addressId, User user, Integer categoryId,
      Double num1, Double num2)
      throws AddressNotExistException {
    List<Product> list;
    Address address = getById(addressId);
    Set<AddressPoint> addressPoints = address.getAddressPoints();
    Double start = 0.0;
    Double end = 0.0;

    if(addressPoints.size()>2 &&num1==null && num2==null){
      start=addressPoints.iterator().next().getNumber();
      end= Iterables.getLast(addressPoints).getNumber();}
    if(num1!=null && num2!=null){
      start=num1;
      end=num2;
    }
      list = productRepository.findByAddressIdAndPoint(addressId, start, end);

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
    if (list == null ) {
      throw new AddressNotExistException("Đường không có trụ nào");
    }

    Set<Product> wishlist = user.getWishlist();
    List<CartItem> cart = cartItemService.getCartByUser(user);
    addressDetaildto.setAddress(address);
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
    return addressRepository.save(address);  }



  public Address getById(Integer id) throws AddressNotExistException {
    return addressRepository.findById(id)
        .orElseThrow(() -> new AddressNotExistException("vui lòng nhập đúng địa chỉ"));
  }

  public void delete(Integer id) {
    addressRepository.deleteById(id);
  }


  public List<Address> getAll() {
    List<Address> list =  (List<Address>) addressRepository.findAll();
    return list.stream().filter(address -> address.getAddressPoints().size()> 1).collect(Collectors.toList());
  }
}
