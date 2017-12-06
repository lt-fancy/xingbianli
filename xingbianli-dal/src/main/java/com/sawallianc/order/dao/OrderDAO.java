package com.sawallianc.order.dao;

import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDAO {
    void makeOrder(OrderDO orderDO);

    void makeOrderDetail(List<OrderDetailDO> list);

    List<OrderDO> queryOrderInfo(@Param("phone")String phone,@Param("rack")String rack);

    List<OrderDetailDO> queryOrderDetailInfo(@Param("phone")String phone,@Param("rack")String rack);
}
