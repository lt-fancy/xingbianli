package com.sawallianc.order.service;

import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;

import java.util.List;

public interface OrderService {
    OrderDO makeOrder(OrderVO orderVO,String orderId,Integer orderState);

    List<OrderBO> queryOrderInfo(String phone);

    int updateOrderState2Succeed(String orderId,String weixinOrderId);

    Integer queryIfDealtWeixin(String weixin);

    List<OrderDetailDO> queryOrderDetailByOrderId(String orderId);
}
