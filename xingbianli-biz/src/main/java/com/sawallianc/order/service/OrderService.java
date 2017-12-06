package com.sawallianc.order.service;

import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderVO;

import java.util.List;

public interface OrderService {
    void makeOrder(OrderVO orderVO);

    List<OrderBO> queryOrderInfo(String phone,String rackUUid);
}
