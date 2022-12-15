package com.shopMe.quangcao.address.AddressPoint;

import com.shopMe.quangcao.address.Address;
import com.shopMe.quangcao.address.AddressPoint.dto.AddressPointDto;
import com.shopMe.quangcao.address.AddressService;
import com.shopMe.quangcao.product.Product;
import com.shopMe.quangcao.product.ProductRepository;
import com.shopMe.quangcao.exceptions.AddressNotExistException;
import com.shopMe.quangcao.exceptions.CannotDeleteException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressPointService {

  @Autowired
  private AddressPointRepository addressPointRepository;

  @Autowired
  private AddressService addressService;

  @Autowired
  private ProductRepository productRepository;


  public void add(AddressPointDto addressPointDto, Integer addressPointId)
      throws AddressNotExistException {
    AddressPoint addressPoint = new AddressPoint();
    Address address = addressService.getById(addressPointDto.getAddressId());
    addressPoint.setLat(addressPointDto.getLat());
    addressPoint.setLng(addressPointDto.getLng());
    addressPoint.setName(addressPointDto.getName());
    addressPoint.setAddress(address);

    List<AddressPoint> list = addressPointRepository.findByAddressIdOrderByNumberAsc(
        address.getId());

    if (list.size() == 0) {
      addressPoint.setNumber(1.0);
    } else if (list.size() == 1) {
      addressPoint.setNumber(2.0);
    } else {
      AddressPoint addressPointBehind = getById(addressPointId);
      addressPoint.setNumber(addressPointBehind.getNumber() + 1);
      for (AddressPoint point : list) {
        if (point.getNumber() >= addressPoint.getNumber()) {
          point.setNumber(point.getNumber() + 1);
          addressPointRepository.save(point);
        }
      }
    }
    addressPointRepository.save(addressPoint);
  }

  public AddressPoint getById(Integer id) {
    return addressPointRepository.findById(id).get();
  }

  public List<AddressPoint> getByAddress(int parseInt) {
    return addressPointRepository.findByAddressIdOrderByNumberAsc(parseInt);
  }

  public void delete(Integer addressPointId) throws CannotDeleteException {
    AddressPoint addressPoint = getById(addressPointId);
    List<AddressPoint> list = addressPointRepository.findByAddressIdOrderByNumberAsc(
        addressPoint.getAddress().getId());
    List<Product> products = productRepository.findByAddressIdAndPoint(
        addressPoint.getAddress().getId(), addressPoint.getNumber(), addressPoint.getNumber() + 1);

    if (list.size() == 2) {
      throw new CannotDeleteException("Không thể xóa điểm này");
    }
    for (AddressPoint point : list) {
      if (point.getNumber() > addressPoint.getNumber()) {
        point.setNumber(point.getNumber() - 1);
        addressPointRepository.save(point);
      }
    }
    for (Product product : products) {
      if (product.getNumber() > addressPoint.getNumber()) {
        product.setNumber(product.getNumber() - 1);
        productRepository.save(product);
      } else if (product.getNumber() < addressPoint.getNumber()) {
        product.setNumber(product.getNumber() + 1);
        productRepository.save(product);
      }
    }
    addressPointRepository.delete(addressPoint);
  }

  public void update(AddressPointDto addressPointdto, Integer id) {
    System.out.println(addressPointdto.getName());
    AddressPoint addressPoint = getById(id);
    addressPoint.setLat(addressPointdto.getLat());
    addressPoint.setLng(addressPointdto.getLng());
    addressPoint.setName(addressPointdto.getName());
    addressPointRepository.save(addressPoint);
  }
}
