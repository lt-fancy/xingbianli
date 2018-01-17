package com.sawallianc.order.service;

import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;

import java.util.List;

public interface OrderService {
    OrderBO makeOrder(OrderVO orderVO,Integer orderState);

    List<OrderBO> queryOrderInfoByPhone(String phone);

    List<OrderBO> queryOrderInfoByOpenid(String openid);

    int updateOrderState2Succeed(String orderId,String weixinOrderId);

    Integer queryIfDealtWeixin(String weixin);

    List<OrderDetailDO> queryOrderDetailByOrderId(String orderId);
}
