package com.shopMe.quangcao.overview;

import java.time.YearMonth;
import java.util.Map;

public class Overview {
    private Long totalProduct;
    private Long totalAvailable;
    private Long totalHiring;
    private Long totalUser;
    private Long totalUserHiring;
    private float TotalEarning;
    private float TotalEarningToday;

    private Map<YearMonth,Long> totalProductOrderEachMonth;
    public Overview() {
    }


    public void setTotalProduct(Long totalProduct) {
        this.totalProduct = totalProduct;
    }

    public Long getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Long totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public Long getTotalHiring() {
        return totalHiring;
    }

    public void setTotalHiring(Long totalHiring) {
        this.totalHiring = totalHiring;
    }

    public Long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Long totalUser) {
        this.totalUser = totalUser;
    }

    public Long getTotalUserHiring() {
        return totalUserHiring;
    }

    public void setTotalUserHiring(Long totalUserHiring) {
        this.totalUserHiring = totalUserHiring;
    }

    public float getTotalEarning() {
        return TotalEarning;
    }

    public void setTotalEarning(float totalEarning) {
        TotalEarning = totalEarning;
    }

    public float getTotalEarningToday() {
        return TotalEarningToday;
    }

    public void setTotalEarningToday(float totalEarningToday) {
        TotalEarningToday = totalEarningToday;
    }

    public Long getTotalProduct() {
        return totalProduct;
    }

    public Map<YearMonth, Long> getTotalProductOrderEachMonth() {
        return totalProductOrderEachMonth;
    }

    public void setTotalProductOrderEachMonth(Map<YearMonth, Long> totalProductOrderEachMonth) {
        this.totalProductOrderEachMonth = totalProductOrderEachMonth;
    }
}
