package com.sawallianc.order.service;

import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;

import java.util.List;

public interface OrderService {
    OrderDO makeOrder(OrderVO orderVO,String orderId);

    List<OrderBO> queryOrderInfo(String phone);

    List<String> queryPayedWeixinOrderIds();

    int updateOrderState2Succeed(String orderId,String weixinOrderId);
}
