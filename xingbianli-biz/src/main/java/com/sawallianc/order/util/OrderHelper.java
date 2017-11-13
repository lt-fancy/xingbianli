package com.sawallianc.order.util;

import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;

import java.util.UUID;

public final class OrderHelper {
    private OrderHelper(){

    }
    public static OrderDO doFromVo(OrderVO orderVO){
        if(null == orderVO){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        orderDO.setUserId(1L);
        orderDO.setOrderId(UUID.randomUUID().toString());
        orderDO.setBenefitPrice(Double.parseDouble(orderVO.getBenefitPrice()));
        orderDO.setGoodsSettlePrice(Double.parseDouble(orderVO.getGoodsSettlePrice()));
        orderDO.setGoodsTotalPrice(Double.parseDouble(orderVO.getGoodsTotalPrice()));
        orderDO.setGoodsId(orderVO.getGoodsIdCombine());
        orderDO.setRackUUID(orderVO.getRackUUID());
        return orderDO;
    }
}
