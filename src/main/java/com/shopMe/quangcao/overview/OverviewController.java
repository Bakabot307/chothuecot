package com.shopMe.quangcao.overview;

import com.shopMe.quangcao.product.ProductStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OverviewController {

  @Autowired
  private
  OverviewService overviewService;


  @GetMapping("/admin/overview")
  public ResponseEntity<Overview> getOverview() {
    Overview overview = new Overview();
    Long totalProduct, totalProductAvailable, totalUser, totalProductHiring, totalUserHiring;
    float totalEarningToday, totalEarning;
    Map<YearMonth, Long> totalProductOrderEachMonth;

    totalProduct = overviewService.getTotalProduct();
    totalProductAvailable = overviewService.getTotalProductByStatus(ProductStatus.AVAILABLE);
    totalProductHiring = overviewService.getTotalProductByStatus(ProductStatus.HIRING);
    totalUser = overviewService.getTotalUser();
    totalUserHiring = overviewService.getTotalUserHiring();
    totalProductOrderEachMonth = overviewService.getProductOrderedLastYear();
    totalEarningToday = overviewService.getEarningToday();
    totalEarning = overviewService.getTotalEarning();

    overview.setTotalProduct(totalProduct);
    overview.setTotalAvailable(totalProductAvailable);
    overview.setTotalUser(totalUser);
    overview.setTotalHiring(totalProductHiring);
    overview.setTotalUserHiring(totalUserHiring);
    overview.setTotalProductOrderEachMonth(totalProductOrderEachMonth);
    overview.setTotalEarning(totalEarning);
    overview.setTotalEarningToday(totalEarningToday);

    return new ResponseEntity<>(overview, HttpStatus.OK);
  }


  @GetMapping("/admin/overview/month_week_earning")
  public ResponseEntity<?> getEarningPerMonth(@RequestParam int number, @RequestParam String type) {
    Map<?, ?> test = overviewService.getTotalEarningPerMonthOrWeek(number, type);
    return new ResponseEntity<>(test, HttpStatus.OK);
  }

  @GetMapping("/admin/overview/monthly_product_hired")
  public ResponseEntity<?> getMonthlyHiredProduct(@RequestParam int number) {
    Map<YearMonth, Long> test = overviewService.getMonthlyHiredProduct(number);
    return new ResponseEntity<>(test, HttpStatus.OK);
  }

  @GetMapping("/admin/overview/time_product_hired")
  public ResponseEntity<?> timeProductHired(@RequestParam String sort,
      @RequestParam String date1,
      @RequestParam String date2,
      @RequestParam Integer page,
      @RequestParam Integer dataPerPage,
      @RequestParam(required = false) String keyword
  ) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    if(format.parse(date1).after(format.parse(date2))){
      return new ResponseEntity<>("Ngày bắt đầu phải nhỏ hơn", HttpStatus.BAD_REQUEST);
    }
    OverviewProductDto test = overviewService.getProductHired(sort, format.parse(date1),
        format.parse(date2), page, dataPerPage, keyword);
    return new ResponseEntity<>(test, HttpStatus.OK);
  }

}
