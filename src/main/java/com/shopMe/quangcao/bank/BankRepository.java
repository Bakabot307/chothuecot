package com.shopMe.quangcao.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {

  Bank findByBankCodeOrBankName(String name, String name2);
}
