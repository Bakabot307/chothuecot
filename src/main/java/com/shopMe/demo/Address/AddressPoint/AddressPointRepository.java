package com.shopMe.demo.Address.AddressPoint;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressPointRepository extends JpaRepository<AddressPoint, Integer> {

  List<AddressPoint> findByAddressIdOrderByNumberAsc(Integer addressId);


}
