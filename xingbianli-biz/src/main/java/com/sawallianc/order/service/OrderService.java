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

    List<OrderBO> queryOrderInfoByAlipayId(String alipayId);

    OrderBO getOrderByOrderNo(String orderNo);

    int updateOrderState2SucceedWeixin(String orderId,String weixinOrderId,String phone);

    int updateOrderState2SucceedAlipay(String orderId,String alipayOrderId,String phone);

    Integer queryIfDealtWeixin(String weixin);

    Integer queryIfDealtAlipay(String alipay);

    Integer queryIfNotifyAlipay(String notifyId);

    int insertAlipayNotify(String notifyId);

    List<OrderDetailDO> queryOrderDetailByOrderId(String orderId);
}
