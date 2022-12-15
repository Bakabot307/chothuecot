package com.shopMe.quangcao.order;

public enum OrderStatus {
  NEW {
    @Override
    public String defaultDescription() {
      return "Đơn hàng của bạn đã đặt thành công";
    }
  },
  CANCELLED {
    @Override
    public String defaultDescription() {
      return "Đơn hàng của bạn đã bị hủy";
    }
  },
  DONE {
    @Override
    public String defaultDescription() {
      return "Đơn hàng đã được xử lý xong";
    }
  },
  USER_CONFIRMED {
    @Override
    public String defaultDescription() {
      return "Khách hàng đã xác nhận đơn hàng";
    }
  },
  PAID {
    @Override
    public String defaultDescription() {
      return "Admin đã xác nhận thanh toán";
    }
  },
  USER_CANCELLED {
    @Override
    public String defaultDescription() {
      return "Khách hàng đã hủy đơn hàng";
    }
  },
  EXTEND {
    @Override
    public String defaultDescription() {
      return "Khách hàng đã yêu cầu gia hạn";
    }
  }, PREORDER {
    @Override
    public String defaultDescription() {
      return "Khách hàng đã đặt hàng trước";
    }

  };


  public abstract String defaultDescription();

}
