package com.sawallianc.order.dao;

import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDAO {
    void makeOrder(OrderDO orderDO);

    void makeOrderDetail(List<OrderDetailDO> list);

    List<OrderDO> queryOrderInfo(@Param("phone")String phone);

    List<OrderDetailDO> queryOrderDetailInfoByPhone(@Param("phone")String phone);

    List<OrderDetailDO> queryOrderDetailInfoByOrderId(@Param("orderId")String orderId);

    Integer queryIfDealtWeixin(@Param("weixin") String weixin);
    int updateOrderState2Succeed(@Param("orderId") String orderId,@Param("weixinOrderId") String weixinOrderId);
}
