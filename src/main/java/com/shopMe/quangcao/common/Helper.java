package com.shopMe.quangcao.common;

import com.shopMe.quangcao.product.ProductStatus;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Helper {

  public static <T> Predicate<T> distinctByKey(
      Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  public static Date PlusMonth(Date date, Integer month) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).plusSeconds(month * 30 * 24 * 60 * 60);
    Date eDate = Date.from(instant);
    return eDate;
  }

  public static Date PlusDay(Date date, Integer day) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).plusSeconds(day * 24 * 60 * 60);
    return Date.from(instant);
  }

  public static Date PlusHour(Date date, Integer hour) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).plusSeconds(hour * 60 * 60);
    return Date.from(instant);
  }

  public static Date PlusSeconds(Date date, Integer seconds) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).plusSeconds(seconds);
    return Date.from(instant);
  }

  public static Date MinusDay(Date date, Integer day) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).minusSeconds(day * 24 * 60 * 60);
    return Date.from(instant);
  }

  public static Date MinusMonth(Date date, Integer month) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).minusSeconds(month * 24 * 60 * 60 * 30);
    return Date.from(instant);
  }

  public static Date MinusYear(Date date, Integer month) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    instant = ldt.toInstant(ZoneOffset.UTC).minusSeconds(month * 24 * 60 * 60 * 30 * 12);
    return Date.from(instant);
  }

  public static boolean pEnumContains(String test) {

    for (ProductStatus p : ProductStatus.values()) {
      if (p.name().equals(test)) {
        return true;
      }
    }
    return false;
  }


  public static Object ToStringMaker(Object o) {

    Class<? extends Object> c = o.getClass();
    Field[] fields = c.getDeclaredFields();
    for (Field field : fields) {
      String name = field.getName();
      field.setAccessible(true);

      try {
        return name;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }

    }
    return null;
  }
}

