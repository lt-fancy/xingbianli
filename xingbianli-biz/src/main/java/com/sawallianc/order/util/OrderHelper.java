package com.sawallianc.order.util;

import com.google.common.collect.Lists;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderDetailBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.commons.collections4.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

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

    public static OrderDetailDO detailDOFromBO(OrderDetailBO bo,OrderDO orderDO){
        if(null == bo){
            return null;
        }
        OrderDetailDO detail = new OrderDetailDO();
        detail.setOrderId(orderDO.getId());
        detail.setGoodsId(Long.parseLong(bo.getGoodsId()));
        detail.setNumber(Integer.parseInt(bo.getNumber()));
        detail.setPrice(Double.parseDouble(bo.getPrice()));
        detail.setPhone(orderDO.getPhone());
        detail.setRackUuid(orderDO.getRackUUID());
        return detail;
    }

    public static List<OrderDetailDO> detailDOSFromBOS(List<OrderDetailBO> list,OrderDO orderDO){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderDetailDO> result = Lists.newArrayListWithCapacity(list.size());
        for(OrderDetailBO bo : list){
            result.add(detailDOFromBO(bo,orderDO));
        }
        return result;
    }

    public static OrderBO boFromDo(OrderDO orderDO){
        if(null == orderDO){
            return null;
        }
        OrderBO bo = new OrderBO();
        bo.setBenefitPrice(String.valueOf(orderDO.getBenefitPrice()));
        bo.setGoodsSettlePrice(String.valueOf(orderDO.getGoodsSettlePrice()));
        bo.setGoodsTotalPrice(String.valueOf(orderDO.getGoodsTotalPrice()));
        bo.setId(orderDO.getId());
        bo.setPhone(orderDO.getPhone());
        bo.setRackUUID(orderDO.getRackUUID());
        bo.setOrderId(orderDO.getOrderId());
        bo.setGmtCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderDO.getGmtCreated()));
        return bo;
    }

    public static List<OrderBO> bosFromDos(List<OrderDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderBO> result = Lists.newArrayListWithCapacity(list.size());
        for(OrderDO orderDO : list){
            result.add(boFromDo(orderDO));
        }
        return result;
    }

}
