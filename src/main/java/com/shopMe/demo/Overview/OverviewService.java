package com.shopMe.demo.Overview;

import com.shopMe.demo.Order.Order;
import com.shopMe.demo.Order.OrderRepository;
import com.shopMe.demo.Order.OrderStatus;
import com.shopMe.demo.OrderDetail.OrderDetail;
import com.shopMe.demo.OrderDetail.OrderDetailRepository;
import com.shopMe.demo.Product.Product;
import com.shopMe.demo.Product.ProductRepository;
import com.shopMe.demo.Product.ProductStatus;
import com.shopMe.demo.config.Helper;
import com.shopMe.demo.user.UserRepository;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverviewService {

  @Autowired
  ProductRepository productrepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  OrderDetailRepository orderDetailRepository;

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  ProductRepository productRepository;

  public Long getTotalProduct() {
    return productrepository.count();
  }

  public Long getTotalProductByStatus(ProductStatus status) {
    List<Product> list = (List<Product>) productrepository.findAll();

    return list.stream()
        .filter(product -> product.getStatus() == status)
        .count();
  }

  public Long getTotalUser() {
    return userRepository.count();
  }

  public Long getTotalUserHiring() {
    return orderDetailRepository.findAll()
        .stream()
        .filter(o -> o.getOrders().getUser().getId() != null)
        .filter(Helper.distinctByKey(o -> o.getOrders().getUser().getId()))
        .filter(o -> o.getProduct().getStatus() == ProductStatus.HIRING)
        .count();
  }

  public Map<YearMonth, Long> getProductOrderedLastYear() {
    DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
    return orderDetailRepository.findAll()
        .stream()
        .filter(orderDetail -> orderDetail.getStartDate() != null)
        .map(orderDetail -> outputFormatter.format(orderDetail.getStartDate()))
        .map(LocalDate::parse)
        .limit(6)
        .collect(
            Collectors.groupingBy(
                YearMonth::from,
                Collectors.counting()
            )
        );
  }

  public Map<?, ?> getTotalEarningPerMonthOrWeek(int number, String type) {
    if (Objects.equals(type, "month")) {
      return orderRepository.findAll()
          .stream()
          .filter(order -> order.getConfirmedTime() != null)
          .filter(
              order -> order.getStatus() == OrderStatus.PAID
                  )
          .limit(number)
          .sorted(Comparator.comparing(Order::getConfirmedTime).reversed())
          .collect(Collectors.groupingBy(
                  o -> YearMonth.from(
                      o.getConfirmedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()),
                  Collectors.summingLong(o -> (long) o.getTotal())
              )
          );
    } else {
      TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear();
      return orderRepository.findAll()
          .stream()
          .filter(order -> order.getConfirmedTime() != null)
          .filter(
              order -> order.getStatus() == OrderStatus.PAID
                 )
          .limit(number)
          .sorted(Comparator.comparing(Order::getConfirmedTime).reversed())
          .collect(Collectors.groupingBy(
                  o -> o.getConfirmedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                      .get(weekOfYear),
                  Collectors.summingLong(o -> (long) o.getTotal())
              )
          );
    }

  }


  public float getEarningToday() {
    Date today = new Date();
    Calendar calendarTo = Calendar.getInstance();
    calendarTo.setTime(today);
    calendarTo.set(Calendar.HOUR_OF_DAY, 23);
    calendarTo.set(Calendar.MINUTE, 59);
    calendarTo.set(Calendar.SECOND, 59);
    calendarTo.set(Calendar.MILLISECOND, 999);

    Calendar calendarFrom = Calendar.getInstance();
    calendarFrom.setTime(today);
    calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
    calendarFrom.set(Calendar.MINUTE, 0);
    calendarFrom.set(Calendar.SECOND, 0);
    calendarFrom.set(Calendar.MILLISECOND, 0);

    Date to = calendarTo.getTime();
    Date from = calendarFrom.getTime();
    System.out.println(to);
    System.out.println(from);

    return orderRepository.findAll().stream()
        .filter(o -> o.getConfirmedTime() != null)
        .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.DONE)
        .filter(o -> o.getConfirmedTime().before(to) && o.getConfirmedTime().after(from))
        .map(Order::getTotal)
        .reduce((float) 0, Float::sum);

  }

  public float getTotalEarning() {
    return orderRepository.findAll().stream()
        .filter(o -> o.getConfirmedTime() != null)
        .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.DONE)
        .map(Order::getTotal)
        .reduce((float) 0, Float::sum);
  }


  public float getEarningLastWeek() {
    Date today = new Date();
    Calendar calendarTo = Calendar.getInstance();
    calendarTo.setTime(today);
    calendarTo.set(Calendar.HOUR_OF_DAY, 23);
    calendarTo.set(Calendar.MINUTE, 59);
    calendarTo.set(Calendar.SECOND, 59);
    calendarTo.set(Calendar.MILLISECOND, 999);

    Calendar calendarFrom = Calendar.getInstance();
    today = Helper.MinusDay(today, 7);
    calendarFrom.setTime(today);
    calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
    calendarFrom.set(Calendar.MINUTE, 0);
    calendarFrom.set(Calendar.SECOND, 0);
    calendarFrom.set(Calendar.MILLISECOND, 0);

    Date to = calendarTo.getTime();
    Date from = calendarFrom.getTime();
    System.out.println(to);
    System.out.println(from);

    return orderRepository.findAll().stream()
        .filter(o -> o.getConfirmedTime() != null)
        .filter(o -> o.getConfirmedTime().before(to) && o.getConfirmedTime().after(from))
        .map(Order::getTotal)
        .reduce((float) 0, Float::sum);

  }

  public Long getEarning(Date from, Date to) {
    Calendar calendarTo = Calendar.getInstance();
    calendarTo.setTime(to);
    calendarTo.set(Calendar.HOUR_OF_DAY, 23);
    calendarTo.set(Calendar.MINUTE, 59);
    calendarTo.set(Calendar.SECOND, 59);
    calendarTo.set(Calendar.MILLISECOND, 999);

    Calendar calendarFrom = Calendar.getInstance();
    calendarFrom.setTime(from);
    calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
    calendarFrom.set(Calendar.MINUTE, 0);
    calendarFrom.set(Calendar.SECOND, 0);
    calendarFrom.set(Calendar.MILLISECOND, 0);

    to = calendarTo.getTime();
    from = calendarFrom.getTime();
    System.out.println(to);
    System.out.println(from);
    return 1L;
  }

  public OverviewProductDto getProductHired(String sort, Date date1, Date date2, Integer page,
      Integer dataPerPage, String keyword) {
    List<OrderDetail> listDetail;
    final Map<Product, Long> map;
    final Map<Product, Long> finalMap;
    int total = 0;
    int totalHired = 0;

    List<Product> listProduct;

    if (keyword != null) {
      listProduct = productRepository.findAllP(keyword);
      listDetail = orderDetailRepository.findAllByDate(date1, date2, keyword);
    } else {
      listProduct = (List<Product>) productRepository.findAll();
      listDetail = orderDetailRepository.findAllByDate(date1, date2);
    }

    map = listDetail.stream()
        .filter(orderDetail -> orderDetail.getExpiredDate() != null)
        .collect(Collectors.groupingBy(OrderDetail::getProduct, Collectors.counting()));
    totalHired = map.size();
    listProduct.forEach(product -> {
      if (!map.containsKey(product)) {
        map.put(product, 0L);
      }
    });

    total = map.size();

    if (sort.equals("asc")) {
      finalMap = map.entrySet().stream()
          .sorted(Map.Entry.comparingByValue())
          .skip((long) (page - 1) * dataPerPage)
          .limit(dataPerPage)
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
              (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    } else {
      finalMap = map.entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .skip((long) (page - 1) * dataPerPage)
          .limit(dataPerPage)
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
              (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
    return new OverviewProductDto(finalMap, total, (total + dataPerPage - 1) / dataPerPage, page,
        totalHired);
  }


  public Map<YearMonth, Long> getMonthlyHiredProduct(int number) {
    List<OrderDetail> listDetail = orderDetailRepository.findAll();
    return listDetail.stream()
        .filter(orderDetail -> orderDetail.getExpiredDate() != null
            && orderDetail.getStartDate() != null)
        .filter(orderDetail -> orderDetail.getOrders().getConfirmedTime() != null)
        .limit(number)
        .sorted(Comparator.comparing(OrderDetail::getStartDate).reversed())
        .collect(Collectors.groupingBy(
            orderDetail -> YearMonth.from(
                orderDetail.getOrders().getConfirmedTime().toInstant()
                    .atZone(ZoneId.systemDefault())),
            Collectors.counting()
        ));
  }
}
