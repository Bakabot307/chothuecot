package com.shopMe.quangcao.address;

import com.shopMe.quangcao.address.dto.AddressDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageAddressDto {

  List<AddressDto> contents = new ArrayList<>();

  Map<String, Integer> PageInfo = new HashMap<>();

  public PageAddressDto() {
    //constructor
  }

  public List<AddressDto> getContents() {
    return contents;
  }

  public void setContents(List<AddressDto> contents) {
    this.contents = contents;
  }

  public Map<String, Integer> getPageInfo() {
    return PageInfo;
  }

  public void setPageInfo(Map<String, Integer> pageInfo) {
    PageInfo = pageInfo;
  }
}
