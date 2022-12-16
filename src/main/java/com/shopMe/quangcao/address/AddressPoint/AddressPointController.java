package com.shopMe.quangcao.address.AddressPoint;

import com.shopMe.quangcao.address.AddressPoint.dto.AddressPointDto;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.exceptions.AddressNotExistException;
import com.shopMe.quangcao.exceptions.CannotDeleteException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/addresspoint")
public class AddressPointController {

  @Autowired
  private AddressPointService addressPointService;

  @GetMapping("/address/{id}")
  public ResponseEntity<List<AddressPoint>> getByAddress(@PathVariable String id) {
    List<AddressPoint> list = addressPointService.getByAddress(Integer.parseInt(id));
    return ResponseEntity.ok(list);
  }

  @PutMapping("/address/add")
  public ResponseEntity<ApiResponse> add(@RequestBody AddressPointDto addressPointdto,
      Integer addressPointId)
      throws AddressNotExistException {

    addressPointService.add(addressPointdto, addressPointId);
    return ResponseEntity.ok(new ApiResponse(true, "Đã thêm thành công"));
  }


  @DeleteMapping("/address/delete/{id}")
  public ResponseEntity<ApiResponse> delete(@PathVariable Integer id)
      throws AddressNotExistException, CannotDeleteException {

    addressPointService.delete(id);
    return ResponseEntity.ok(new ApiResponse(true, "Đã xóa thành công"));
  }

  @PutMapping("/address/update/{id}")
  public ResponseEntity<ApiResponse> update(@RequestBody AddressPointDto addressPointdto,
      @PathVariable Integer id)
      throws AddressNotExistException {
    addressPointService.update(addressPointdto, id);
    return ResponseEntity.ok(new ApiResponse(true, "Đã cập nhật thành công"));
  }
}
