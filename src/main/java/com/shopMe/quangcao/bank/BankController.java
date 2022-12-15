package com.shopMe.quangcao.bank;

import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.exceptions.BankException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankController {

  @Autowired
  private BankService bankService;

  @PostMapping("/add")
  public ResponseEntity<ApiResponse> addBank(
      @RequestBody @Valid Bank bank) throws BankException {

    bankService.addBank(bank);
    return new ResponseEntity<>(
        new ApiResponse(true, "Thêm ngân hàng thành công"),
        HttpStatus.OK);
  }

  @PostMapping("/remove/{id}")
  public ResponseEntity<ApiResponse> removeBank(
      @PathVariable("id") Integer id) throws BankException {

    bankService.removeBank(id);
    return new ResponseEntity<>(
        new ApiResponse(true, "Xóa ngân hàng thành công"),
        HttpStatus.OK);
  }

  @PostMapping("/edit/{id}")
  public ResponseEntity<ApiResponse> editBank(
      @RequestBody @Valid Bank bank) throws BankException {

    bankService.editBank(bank);
    return new ResponseEntity<>(
        new ApiResponse(true, "Xóa ngân hàng thành công"),
        HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<List<Bank>> getBank() {

    return new ResponseEntity<>(
        bankService.getAllBanks(),
        HttpStatus.OK);
  }
}
