package com.sawallianc.order.util;

import com.google.common.collect.Lists;
import com.sawallianc.order.bo.OrderDetailBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.UUID;

public final class OrderHelper {
    private OrderHelper(){

    }
    public static OrderDO doFromVo(OrderVO orderVO){
        if(null == orderVO){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(UUID.randomUUID().toString());
        orderDO.setBenefitPrice(orderVO.getBenefitPrice());
        orderDO.setGoodsSettlePrice(orderVO.getGoodsSettlePrice());
        orderDO.setGoodsTotalPrice(orderVO.getGoodsTotalPrice());
        orderDO.setRackUUID(orderVO.getRackUUID());
        orderDO.setPhone(orderVO.getPhone());
        return orderDO;
    }

    public static OrderDetailDO detailDOFromBO(OrderDetailBO bo,Long id){
        if(null == bo){
            return null;
        }
        OrderDetailDO detail = new OrderDetailDO();
        detail.setOrderId(id);
        detail.setGoodsId(Long.parseLong(bo.getGoodsId()));
        detail.setNumber(Integer.parseInt(bo.getNumber()));
        detail.setPrice(Double.parseDouble(bo.getPrice()));
        return detail;
    }

    public static List<OrderDetailDO> detailDOSFromBOS(List<OrderDetailBO> list,Long id){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderDetailDO> result = Lists.newArrayListWithCapacity(list.size());
        for(OrderDetailBO bo : list){
            result.add(detailDOFromBO(bo,id));
        }
        return result;
    }

}
