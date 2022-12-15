package com.shopMe.quangcao.bank;

import com.shopMe.quangcao.exceptions.BankException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {

  @Autowired
  private BankRepository bankRepository;

  public void addBank(Bank bank) throws BankException {
    Bank bankDb = bankRepository.findByBankCodeOrBankName(bank.getBankCode(),
        bank.getBankName());
    if (bankDb != null) {
      throw new BankException("Ngân hàng đã tồn tại");
    }
    bankRepository.save(bank);
  }

  public void removeBank(Integer id) throws BankException {
    Bank bank = getBankById(id);
    bankRepository.delete(bank);
  }

  public Bank getBankById(Integer id) throws BankException {
    Optional<Bank> bank = bankRepository.findById(id);
    if (bank.isEmpty()) {
      throw new BankException("Không tìm thấy ngân hàng");
    }
    return bank.get();
  }


  public List<Bank> getAllBanks() {
    return bankRepository.findAll();
  }

  public void editBank(Bank bank) throws BankException {
    Bank bankDB = getBankById(bank.getId());
    Bank bankDb = bankRepository.findByBankCodeOrBankName(bank.getBankCode(),bank.getBankName());
    if (bankDb != null && !Objects.equals(bankDB.getId(), bank.getId())) {
      throw new BankException("Ngân hàng đã tồn tại");
    }
    bankDB.setBankCode(bank.getBankCode());
    bankDB.setBankName(bank.getBankName());
    bankDB.setBankAccountName(bank.getBankAccountName());
    bankDB.setBankAccountNumber(bank.getBankAccountNumber());

    bankDB.setBankCode(bank.getBankCode());
    bankRepository.save(bankDB);
  }
}
