package com.sawallianc.order.dao;

import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;

import java.util.List;

public interface OrderDAO {
    void makeOrder(OrderDO orderDO);
    void makeOrderDetail(List<OrderDetailDO> list);
}
