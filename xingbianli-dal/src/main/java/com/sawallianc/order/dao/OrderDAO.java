package com.sawallianc.order.dao;

import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDAO {
    void makeOrder(OrderDO orderDO);

    void makeOrderDetail(List<OrderDetailDO> list);

    OrderDO getOrderByOrderNo(@Param("orderNo")String orderNo);

    List<OrderDO> queryOrderInfoByPhone(@Param("phone")String phone);

    List<OrderDO> queryOrderInfoByOpenid(@Param("openid")String openid);

    List<OrderDO> queryOrderInfoByAlipayId(@Param("alipayid")String alipayid);

    List<OrderDetailDO> queryOrderDetailInfoByPhone(@Param("phone")String phone);

    List<OrderDetailDO> queryOrderDetailInfoByOpenid(@Param("openid")String openid);

    List<OrderDetailDO> queryOrderDetailInfoByAlipayId(@Param("alipayid")String alipayid);

    List<OrderDetailDO> queryOrderDetailInfoByOrderId(@Param("orderId")String orderId);

    Integer queryIfDealtWeixin(@Param("weixin") String weixin);

    Integer queryIfDealtAlipay(@Param("alipay") String alipay);

    Integer queryIfNotifyAlipay(@Param("notifyId") String notifyId);

    int insertAlipayNotify(@Param("notifyId") String notifyId);

    int updateOrderState2SucceedWeixin(@Param("orderId") String orderId,@Param("weixinOrderId") String weixinOrderId);

    int updateOrderState2SucceedAlipay(@Param("orderId") String orderId,@Param("alipayOrderId") String alipayOrderId);
}
